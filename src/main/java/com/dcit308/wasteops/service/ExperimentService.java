package com.dcit308.wasteops.service;

import com.dcit308.wasteops.algorithms.SearchAlgorithm;
import com.dcit308.wasteops.algorithms.SortAlgorithm;
import com.dcit308.wasteops.algorithms.graph.BFS;
import com.dcit308.wasteops.algorithms.graph.Dijkstra;
import com.dcit308.wasteops.algorithms.graph.Prim;
import com.dcit308.wasteops.algorithms.search.BinarySearch;
import com.dcit308.wasteops.algorithms.search.LinearSearch;
import com.dcit308.wasteops.algorithms.sort.InsertionSort;
import com.dcit308.wasteops.algorithms.sort.MergeSort;
import com.dcit308.wasteops.algorithms.sort.QuickSort;
import com.dcit308.wasteops.algorithms.sort.SelectionSort;
import com.dcit308.wasteops.db.AlgorithmRunRepository;
import com.dcit308.wasteops.db.DatabaseManager;
import com.dcit308.wasteops.domain.AlgorithmRun;
import com.dcit308.wasteops.structures.BinarySearchTree;
import com.dcit308.wasteops.structures.CustomHashTable;
import com.dcit308.wasteops.structures.CustomPriorityQueue;
import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.GraphAdjacencyList;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import com.dcit308.wasteops.structures.RedBlackTree;
import com.dcit308.wasteops.structures.SearchTreeADT;
import com.dcit308.wasteops.util.Timer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Runs the six required performance-experiment categories (search
 * comparison, sorting comparison, hash-table load factor, BST vs.
 * balanced tree, heap/priority dispatch, graph algorithms) at the
 * required input sizes, three-plus runs each, exports CSV, and
 * populates the algorithm_runs table.
 *
 * <p>Each experiment category is coded against the relevant interface
 * ({@code SearchAlgorithm}, {@code SortAlgorithm}, {@code SearchTreeADT},
 * {@code PriorityQueueADT}, {@code GraphADT}, {@code MapLookupADT}).
 * If a dependency throws {@code UnsupportedOperationException} (meaning
 * the owning issue has not yet been implemented), that category is
 * <b>gracefully skipped</b> with a clear message.
 *
 * <p>Owned by Issue #14, coordinating with every structure/algorithm
 * owner so each category is actually executable.
 */
public class ExperimentService {

    private static final int RUNS_PER_SIZE = 3;
    private static final int[] SEARCH_SORT_SIZES = {100, 500, 1_000, 5_000, 10_000};
    private static final int[] TREE_HEAP_SIZES = {100, 500, 1_000, 5_000};
    private static final int[] GRAPH_SIZES = {10, 50, 100, 500};
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final AlgorithmRunRepository repository;
    private final Timer timer = new Timer();
    private final Random random = new Random(42); // fixed seed for reproducibility

    public ExperimentService(DatabaseManager db) {
        this.repository = new AlgorithmRunRepository(db);
    }

    /**
     * Runs all six experiment categories, persists results to the
     * database, and exports CSVs to the {@code results/} directory.
     *
     * <p>Each category is independent; a failure or skip in one does
     * not affect the others.
     */
    public void runAllExperiments() {
        System.out.println("═══════════════════════════════════════════");
        System.out.println(" PERFORMANCE EXPERIMENTS");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();

        ensureResultsDir();

        runSafe("1. Search Comparison", this::experimentSearchComparison);
        runSafe("2. Sorting Comparison", this::experimentSortingComparison);
        runSafe("3. Hash-Table Load Factor", this::experimentHashTableLoadFactor);
        runSafe("4. BST vs Balanced Tree", this::experimentBstVsBalanced);
        runSafe("5. Heap / Priority Dispatch", this::experimentHeapPriority);
        runSafe("6. Graph Algorithms", this::experimentGraphAlgorithms);

        System.out.println();
        System.out.println("═══════════════════════════════════════════");
        System.out.println(" All experiments complete.");
        System.out.println("═══════════════════════════════════════════");
    }

    // =====================================================================
    //  Category 1 — Search Comparison (LinearSearch vs BinarySearch)
    // =====================================================================

    private void experimentSearchComparison() {
        List<AlgorithmRun> runs = new ArrayList<>();

        SearchAlgorithm<Integer> linear = new LinearSearch<>();
        SearchAlgorithm<Integer> binary = new BinarySearch<>();

        for (int size : SEARCH_SORT_SIZES) {
            Integer[] data = generateSortedArray(size);
            Integer target = data[size - 1]; // worst case — last element

            for (int run = 1; run <= RUNS_PER_SIZE; run++) {
                // Linear search
                long nsLinear = timeSearch(linear, data, target);
                runs.add(makeRun("LinearSearch", size, nsLinear, run));

                // Binary search (needs sorted input, which we have)
                long nsBinary = timeSearch(binary, data, target);
                runs.add(makeRun("BinarySearch", size, nsBinary, run));
            }
        }

        repository.saveAll(runs);
        exportCsv("results/search_comparison.csv", runs);
        System.out.println("    → results/search_comparison.csv (" + runs.size() + " records)");
    }

    // =====================================================================
    //  Category 2 — Sorting Comparison (4 algorithms)
    // =====================================================================

    private void experimentSortingComparison() {
        List<AlgorithmRun> runs = new ArrayList<>();

        @SuppressWarnings("unchecked")
        SortAlgorithm<Integer>[] sorters = new SortAlgorithm[]{
                new InsertionSort<>(), new SelectionSort<>(),
                new MergeSort<>(), new QuickSort<>()
        };
        String[] names = {"InsertionSort", "SelectionSort", "MergeSort", "QuickSort"};

        for (int size : SEARCH_SORT_SIZES) {
            Integer[] original = generateRandomArray(size);

            for (int s = 0; s < sorters.length; s++) {
                for (int run = 1; run <= RUNS_PER_SIZE; run++) {
                    Integer[] copy = original.clone();
                    long ns = timeSort(sorters[s], copy);
                    runs.add(makeRun(names[s], size, ns, run));
                }
            }
        }

        repository.saveAll(runs);
        exportCsv("results/sorting_comparison.csv", runs);
        System.out.println("    → results/sorting_comparison.csv (" + runs.size() + " records)");
    }

    // =====================================================================
    //  Category 3 — Hash-Table Load Factor
    // =====================================================================

    private void experimentHashTableLoadFactor() {
        List<AlgorithmRun> runs = new ArrayList<>();
        int[] tableSizes = {128, 256, 512, 1024};
        double[] loadFactors = {0.25, 0.50, 0.75, 0.90, 1.00};

        for (int tableCapacity : tableSizes) {
            for (double lf : loadFactors) {
                int numItems = (int) (tableCapacity * lf);
                if (numItems < 1) numItems = 1;

                for (int run = 1; run <= RUNS_PER_SIZE; run++) {
                    // Time put operations
                    CustomHashTable<Integer, Integer> ht = new CustomHashTable<>();
                    timer.start();
                    for (int i = 0; i < numItems; i++) {
                        ht.put(i, i);
                    }
                    long putNs = timer.stop();

                    // Time get operations (lookup all inserted keys)
                    timer.start();
                    for (int i = 0; i < numItems; i++) {
                        ht.get(i);
                    }
                    long getNs = timer.stop();

                    String label = String.format("HashTable_put_lf%.2f", lf);
                    runs.add(makeRun(label, numItems, putNs, run));

                    String labelGet = String.format("HashTable_get_lf%.2f", lf);
                    runs.add(makeRun(labelGet, numItems, getNs, run));
                }
            }
        }

        repository.saveAll(runs);
        exportCsv("results/hashtable_load_factor.csv", runs);
        System.out.println("    → results/hashtable_load_factor.csv (" + runs.size() + " records)");
    }

    // =====================================================================
    //  Category 4 — BST vs Balanced Tree (via SearchTreeADT)
    // =====================================================================

    private void experimentBstVsBalanced() {
        List<AlgorithmRun> runs = new ArrayList<>();

        for (int size : TREE_HEAP_SIZES) {
            for (int run = 1; run <= RUNS_PER_SIZE; run++) {
                // --- BST insert ---
                SearchTreeADT<Integer, Integer> bst = new BinarySearchTree<>();
                timer.start();
                for (int i = 0; i < size; i++) {
                    bst.insert(i, i); // sequential insert — worst case for BST
                }
                long bstInsertNs = timer.stop();
                runs.add(makeRun("BST_insert_sequential", size, bstInsertNs, run));

                // --- BST search ---
                timer.start();
                for (int i = 0; i < size; i++) {
                    bst.search(i);
                }
                long bstSearchNs = timer.stop();
                runs.add(makeRun("BST_search", size, bstSearchNs, run));

                // --- BST height ---
                int bstHeight = bst.height();

                // --- RedBlackTree insert ---
                SearchTreeADT<Integer, Integer> rbt = new RedBlackTree<>();
                timer.start();
                for (int i = 0; i < size; i++) {
                    rbt.insert(i, i);
                }
                long rbtInsertNs = timer.stop();
                runs.add(makeRun("RBTree_insert_sequential", size, rbtInsertNs, run));

                // --- RedBlackTree search ---
                timer.start();
                for (int i = 0; i < size; i++) {
                    rbt.search(i);
                }
                long rbtSearchNs = timer.stop();
                runs.add(makeRun("RBTree_search", size, rbtSearchNs, run));

                int rbtHeight = rbt.height();

                // Log height comparison on first run only
                if (run == 1) {
                    System.out.printf("    n=%-5d  BST height=%d  RBTree height=%d%n",
                            size, bstHeight, rbtHeight);
                }
            }
        }

        repository.saveAll(runs);
        exportCsv("results/bst_vs_balanced.csv", runs);
        System.out.println("    → results/bst_vs_balanced.csv (" + runs.size() + " records)");
    }

    // =====================================================================
    //  Category 5 — Heap / Priority Dispatch
    // =====================================================================

    private void experimentHeapPriority() {
        List<AlgorithmRun> runs = new ArrayList<>();

        for (int size : TREE_HEAP_SIZES) {
            for (int run = 1; run <= RUNS_PER_SIZE; run++) {
                // --- Insert into heap ---
                PriorityQueueADT<Integer> pq = new CustomPriorityQueue<>();
                timer.start();
                for (int i = 0; i < size; i++) {
                    pq.insert(random.nextInt(size * 10), i);
                }
                long insertNs = timer.stop();
                runs.add(makeRun("Heap_insert", size, insertNs, run));

                // --- Extract all from heap ---
                timer.start();
                while (!pq.isEmpty()) {
                    pq.extractMin();
                }
                long extractNs = timer.stop();
                runs.add(makeRun("Heap_extractMin", size, extractNs, run));
            }
        }

        repository.saveAll(runs);
        exportCsv("results/heap_priority.csv", runs);
        System.out.println("    → results/heap_priority.csv (" + runs.size() + " records)");
    }

    // =====================================================================
    //  Category 6 — Graph Algorithms (Dijkstra, BFS, Prim)
    // =====================================================================

    private void experimentGraphAlgorithms() {
        List<AlgorithmRun> runs = new ArrayList<>();

        for (int vertexCount : GRAPH_SIZES) {
            for (int run = 1; run <= RUNS_PER_SIZE; run++) {
                // Build a connected random graph
                GraphADT graph = buildRandomGraph(vertexCount);
                String startVertex = "V0";

                // --- BFS ---
                BFS bfs = new BFS();
                timer.start();
                bfs.traverse(graph, startVertex);
                long bfsNs = timer.stop();
                runs.add(makeRun("BFS", vertexCount, bfsNs, run));

                // --- Dijkstra ---
                Dijkstra dijkstra = new Dijkstra();
                PriorityQueueADT<String> pq = new CustomPriorityQueue<>();
                String endVertex = "V" + (vertexCount - 1);
                timer.start();
                dijkstra.shortestPath(graph, pq, startVertex, endVertex);
                long dijkstraNs = timer.stop();
                runs.add(makeRun("Dijkstra", vertexCount, dijkstraNs, run));

                // --- Prim ---
                Prim prim = new Prim();
                PriorityQueueADT<String> pqPrim = new CustomPriorityQueue<>();
                timer.start();
                prim.minimumSpanningTree(graph, pqPrim);
                long primNs = timer.stop();
                runs.add(makeRun("Prim", vertexCount, primNs, run));
            }
        }

        repository.saveAll(runs);
        exportCsv("results/graph_algorithms.csv", runs);
        System.out.println("    → results/graph_algorithms.csv (" + runs.size() + " records)");
    }

    // =====================================================================
    //  Helpers
    // =====================================================================

    /**
     * Wraps an experiment category so that if any dependency throws
     * {@code UnsupportedOperationException}, the category is skipped
     * with a clear message rather than crashing the whole run.
     */
    private void runSafe(String label, Runnable experiment) {
        System.out.println("── " + label + " ──");
        try {
            experiment.run();
        } catch (UnsupportedOperationException e) {
            System.out.println("    [SKIP] " + e.getMessage());
            System.out.println("    (dependency not yet implemented by its owning issue)");
        } catch (Exception e) {
            System.out.println("    [ERROR] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        System.out.println();
    }

    private long timeSearch(SearchAlgorithm<Integer> algo, Integer[] data, Integer target) {
        timer.start();
        algo.search(data, target);
        return timer.stop();
    }

    private long timeSort(SortAlgorithm<Integer> algo, Integer[] data) {
        timer.start();
        algo.sort(data);
        return timer.stop();
    }

    private Integer[] generateRandomArray(int size) {
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(size * 10);
        }
        return arr;
    }

    private Integer[] generateSortedArray(int size) {
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i;
        }
        return arr;
    }

    /**
     * Builds a connected random graph with the given number of vertices
     * and roughly 3× that many edges. Ensures connectivity by first
     * creating a spanning path V0→V1→...→V(n-1).
     */
    private GraphADT buildRandomGraph(int vertexCount) {
        GraphADT graph = new GraphAdjacencyList();
        for (int i = 0; i < vertexCount; i++) {
            graph.addVertex("V" + i);
        }
        // Spanning path for connectivity
        for (int i = 0; i < vertexCount - 1; i++) {
            double w = 1.0 + random.nextDouble() * 10.0;
            graph.addEdge("V" + i, "V" + (i + 1), w);
        }
        // Extra random edges
        int extraEdges = vertexCount * 2;
        for (int i = 0; i < extraEdges; i++) {
            int from = random.nextInt(vertexCount);
            int to = random.nextInt(vertexCount);
            if (from != to) {
                double w = 1.0 + random.nextDouble() * 10.0;
                graph.addEdge("V" + from, "V" + to, w);
            }
        }
        return graph;
    }

    private AlgorithmRun makeRun(String algorithmName, int inputSize, long timeNs, int runNumber) {
        String runId = String.format("%s_%d_run%d_%d",
                algorithmName, inputSize, runNumber, System.nanoTime());
        String dateRun = LocalDateTime.now().format(ISO_FMT);
        return new AlgorithmRun(runId, algorithmName, inputSize, timeNs, null, dateRun);
    }

    private void ensureResultsDir() {
        File dir = new File("results");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Exports a list of algorithm runs to a CSV file.
     *
     * @param path the output file path
     * @param runs the runs to export
     */
    private void exportCsv(String path, List<AlgorithmRun> runs) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("algorithm_name,input_size,run_id,time_ns,date_run");
            for (AlgorithmRun r : runs) {
                pw.printf("%s,%d,%s,%d,%s%n",
                        r.getAlgorithmName(),
                        r.getInputSize(),
                        r.getRunId(),
                        r.getTimeNanos(),
                        r.getDateRun());
            }
        } catch (IOException e) {
            System.err.println("    [WARN] Failed to write CSV " + path + ": " + e.getMessage());
        }
    }
}

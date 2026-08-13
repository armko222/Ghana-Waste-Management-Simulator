# Task Allocation — Ghana Waste Management Operations Optimizer

**Team:** 14 members

> **Current decisions:** Scheduled collections are removed — every
> request, however it originates, is a single `ServiceRequest`. Waste
> categories are fixed at three: General, Hazardous, Industrial (no
> Medical category). A simple console interface will be implemented, as
> a minor part of Issue #13, so the system can be demonstrated without
> editing source code — it is not a major focus and should not consume
> significant time.

### Important project rule

Every member must:
1. Implement their assigned data structure and/or algorithm.
2. Write tests covering normal, boundary, and invalid-input cases.
3. Provide the required evidence (trace table, proof sketch, or
   counterexample) where applicable.
4. Be able to explain how their work fits into the wider system.
5. Be able to defend at least one data structure and one algorithm
   during the oral defense.

All assessed data structures must be custom implementations. Do not use
Java's built-in `HashMap`, `TreeMap`, `PriorityQueue`, `Stack`,
`ArrayDeque`, or equivalents anywhere in `structures/`, `algorithms/`,
`scheduling/`, `indexing/`, or `service/`.

Interface files for every cross-member dependency already exist in
`structures/` and `algorithms/` (files ending `ADT.java`, plus
`SearchAlgorithm.java` and `SortAlgorithm.java`) — see
`Team_Handbook.docx`, "Working With Each Other's Code," before assuming
you need to wait on someone else's work.

---

# ISSUE #1 — Domain Model, Database & CSV Foundation

**Owner:** [assign]

## Purpose
Build the foundation everyone else depends on. Kept deliberately focused
so it isn't a bottleneck.

## What you're building

### Domain classes
- [ ] `domain/Location.java` — already written
- [ ] `domain/Road.java` — already written
- [ ] `domain/ServiceRequest.java` — already written
- [ ] `domain/Resource.java` — already written
- [ ] `domain/AuditEvent.java` — already written
- [ ] `domain/AlgorithmRun.java` — already written

### Database
- [ ] `db/DatabaseManager.java` — SQLite connection, automatic schema init
- [ ] `data/sql/schema.sql` — already written, covers all six tables

### CSV
- [ ] `db/CsvImporter.java` — validates records, rejects malformed rows
      clearly
- [ ] `db/LocationRepository.java`, `RoadRepository.java`,
      `ServiceRequestRepository.java`, `ResourceRepository.java`
- [ ] Real local dataset meeting the minimums: 50 locations, 100 roads,
      300 service requests, 30 resources
- [ ] All resources share a single home location
- [ ] Hazardous requests seeded with High priority; Industrial requests
      seeded with Medium priority when the dataset is generated

### Data structure
- [ ] `structures/DynamicArray.java` — insert, get, set, remove, resize
      (no `ArrayList` as internal storage)

### Tests
- Empty case, single element, resize trigger, out-of-bounds access
- CSV: valid input, malformed input rejected clearly

### Evidence
- Dynamic array resize trace
- Short note on how the dataset was constructed

## Depends on
Nothing. This is the first task to begin.

## Definition of Done
- [ ] Fresh clone builds with no manual setup beyond Java/Maven
- [ ] Database initializes and all CSV files import successfully
- [ ] Domain classes are stable for other members
- [ ] Reviewed and merged

---

# ISSUE #2 — Doubly Linked List

**Owner:** [assign]

## What you're building
- [ ] `structures/DoublyLinkedList.java` — addFirst, addLast,
      insertAfter, remove, size, isEmpty, working iterator
      (no `java.util.LinkedList` internally). Implements
      `structures/LinkedListADT.java`.
- [ ] A genuine use inside the system — likely collision chaining for
      Issue #9's hash table; confirm with Issue #9 first

### Tests
- Empty list, single node, multiple nodes
- Remove existing and non-existent values
- Iterate in insertion order, including after removals

### Evidence
- Node-structure diagram, iterator demonstration

## Depends on
Issue #1.

---

# ISSUE #3 — Stack & Audit Log

**Owner:** [assign]

## What you're building
- [ ] `structures/ArrayStack.java` — push, pop, peek, isEmpty
      (no `java.util.Stack`)
- [ ] `db/AuditEventRepository.java`
- [ ] Every meaningful dispatch action produces an `AuditEvent`, pushed
      onto this stack and persisted through `audit_events`
- [ ] A "show recent actions" operation reading the stack top-down

### Tests
- Pop/peek on empty stack — throws, doesn't fail silently
- LIFO order confirmed over multiple pushes

### Evidence
- Sample action sequence and resulting audit trail

## Depends on
Issue #1.

---

# ISSUE #4 — Circular Queue & Linear Search

**Owner:** [assign]

## What you're building
- [ ] `structures/CircularQueue.java` — enqueue, dequeue, peekFront,
      isFull, isEmpty, correct wrap-around handling. Implements
      `structures/QueueADT.java`.
- [ ] `algorithms/search/LinearSearch.java` — implements
      `algorithms/SearchAlgorithm.java`
- [ ] `scheduling/FifoDispatcher.java` — wraps this queue

### Tests
- Dequeue from empty, enqueue past capacity
- Wrap-around verified over enough operations to cycle the index
- Linear search: found, not found, empty input

### Evidence
- Front/rear index trace across a sequence including one wrap-around

## Depends on
Issue #1.

---

# ISSUE #5 — Deque, Heap & Binary Search

**Owner:** [assign]

## What you're building
- [ ] `structures/ArrayDeque.java` — addFront, addRear, removeFront,
      removeRear (not `java.util.ArrayDeque`). Implements
      `structures/DequeADT.java`.
- [ ] `structures/BinaryHeap.java` and `CustomPriorityQueue.java` —
      insert, extractMin, peekMin, heapify. Implements
      `structures/PriorityQueueADT.java`.
- [ ] `algorithms/search/BinarySearch.java` with an explicit sorted-input
      precondition — implements `algorithms/SearchAlgorithm.java`
- [ ] A test showing binary search fails on unsorted input (required
      counterexample)

### Tests
- Empty deque/heap, single element
- Heap property verified after randomized insert/extract sequences
- Binary search: found, not found, empty array, unsorted-input case

### Evidence
- Heap insertion/extraction order trace
- Counterexample write-up

## Depends on
Issue #1.

---

# ISSUE #6 — Binary Search Tree & Insertion Sort

**Owner:** [assign]

## What you're building
- [ ] `structures/BinarySearchTree.java` — insert, search,
      inorderTraversal, height. Implements `structures/SearchTreeADT.java`.
- [ ] `algorithms/sort/InsertionSort.java` — implements
      `algorithms/SortAlgorithm.java`
- [ ] `indexing/RequestIndex.java` indexing requests by deadline

### Tests
- Empty tree, single node
- Duplicate-key policy documented
- Search a present and an absent key

### Evidence
- Inorder traversal showing sorted output
- Insertion sort trace table

## Depends on
Issue #1.

---

# ISSUE #7 — Red-Black Tree & Selection Sort

**Owner:** [assign]

## What you're building
- [ ] `structures/RedBlackTree.java` — insertion with rotation and
      recolouring (a documented simplified alternative is acceptable).
      Implements `structures/SearchTreeADT.java`.
- [ ] `algorithms/sort/SelectionSort.java` — implements
      `algorithms/SortAlgorithm.java`
- [ ] Height comparison: same sequential data into this tree and Issue
      #6's plain BST — both implement `SearchTreeADT`, so call `height()`
      on each identically

### Tests
- Empty tree, single node
- Height stays proportional to log(n) after sequential inserts

### Evidence
- 2–3 rotation diagrams
- Height comparison against Issue #6's BST

## Depends on
Issue #6 (patterns/reference — not strictly blocking).

---

# ISSUE #8 — B-Tree & Merge Sort

**Owner:** [assign]

## What you're building
- [ ] `structures/BTree.java` — search, insert, node splitting
      (a documented index-page simulation is acceptable)
- [ ] `algorithms/sort/MergeSort.java` — implements
      `algorithms/SortAlgorithm.java`
- [ ] `indexing/ResourceIndex.java` using this B-tree

### Tests
- Empty tree, single key
- A test forcing a node split, confirms tree remains searchable after

### Evidence
- Search trace and node-split walkthrough
- Merge sort trace table

## Depends on
Issue #1.

---

# ISSUE #9 — Hash Table, Set, Map & QuickSort

**Owner:** [assign]

## What you're building
- [ ] `structures/CustomHashTable.java` — put, get, remove, containsKey,
      documented collision strategy. Implements
      `structures/MapLookupADT.java`.
- [ ] `structures/CustomSet.java`, `CustomMap.java`
- [ ] `algorithms/sort/QuickSort.java` — implements
      `algorithms/SortAlgorithm.java`
- [ ] Decide: chaining (using Issue #2's `LinkedListADT`) or open
      addressing — confirm with Issue #2 if chaining

### Tests
- Empty table, single entry
- Forced collision using known colliding keys
- Remove a non-existent key

### Evidence
- Collision statistics at three or more different load factors

## Depends on
Issue #1. Optionally Issue #2 if chaining is chosen.

---

# ISSUE #10 — Graph (Adjacency List), Disjoint Set & BFS

**Owner:** [assign]

## What you're building
- [ ] `structures/GraphAdjacencyList.java` — addVertex, addEdge,
      getNeighbors, getWeight, built from Locations and Roads. Implements
      `structures/GraphADT.java`.
- [ ] `structures/DisjointSet.java` — makeSet, find (path compression),
      union. Implements `structures/DisjointSetADT.java`.
- [ ] `algorithms/graph/BFS.java`

### Tests
- Empty graph, single vertex, disconnected graph
- Disjoint set: repeated union, path compression verified

### Evidence
- Graph diagram, BFS traversal trace

## Depends on
Issue #1.

---

# ISSUE #11 — Graph (Adjacency Matrix) & DFS

**Owner:** [assign]

## What you're building
- [ ] `structures/GraphAdjacencyMatrix.java` — same network as Issue
      #10. Implements `structures/GraphADT.java`.
- [ ] `algorithms/graph/DFS.java`
- [ ] Confirm both representations agree — coordinate with Issue #10

### Tests
- Empty graph, single vertex, disconnected graph
- Cross-check against Issue #10's adjacency list

### Evidence
- DFS traversal trace
- Adjacency-list vs. matrix agreement results

## Depends on
Issue #1. Coordinate with Issue #10 (not strictly blocking).

---

# ISSUE #12 — Dijkstra, Prim, Kruskal & Routing Service

**Owner:** [assign]

## What you're building
- [ ] `algorithms/graph/Dijkstra.java` — uses `PriorityQueueADT` and
      `GraphADT`. Do not wait for Issue #5/#10/#11 — build against the
      interfaces, see `Team_Handbook.docx`.
- [ ] `algorithms/graph/Prim.java`, `Kruskal.java` — Kruskal uses
      `DisjointSetADT`
- [ ] `service/RoutingService.java` exposing: fastest route, reachable
      locations, minimum connecting network
- [ ] Apply the index-number-derived route-penalty parameter to edge
      weights (see `util/IndexParameterDeriver.java`)

### Tests
- Single-vertex graph, disconnected graph → defined unreachable result
- MST on an already-minimal connected graph

### Evidence
- Dijkstra distance/predecessor trace on the real dataset
- Kruskal or Prim MST trace with running cost

## Depends on
Issues #5, #10, #11.

---

# ISSUE #13 — Dispatch, Optimisation & Console Interface

**Owner:** Project Lead

## The dispatch rules — build exactly this
- [ ] **FIFO mode:** order by `time_submitted` ascending. No other factor.
- [ ] **Urgency mode:** order by `urgency` (1–5) descending. Tie-break:
      earlier `time_submitted`.
- [ ] **Priority mode:** order by `priority` tier (High → Medium → Low).
      Tie-break within a tier: `urgency` descending, then
      `time_submitted` ascending.
- [ ] Category is not a dispatch-time rule — it's already reflected in
      priority via Issue #1's dataset seeding.

## What you're building
- [ ] `scheduling/FifoDispatcher.java`, `UrgencyDispatcher.java`,
      `PriorityDispatcher.java` — built against `QueueADT` and
      `PriorityQueueADT`, not Issue #4/#5's concrete classes directly
- [ ] `algorithms/optimisation/GreedyDispatch.java` — nearest available
      resource to the highest-priority request
- [ ] A constructed input where greedy is provably worse than DP
      (required counterexample)
- [ ] `algorithms/optimisation/KnapsackDP.java` — budget-constrained
      request selection, using the index-number-derived budget parameter
- [ ] `service/DispatchService.java` exposing all three dispatch modes
      plus greedy and DP

### Minor task — console interface
This is a small, secondary part of this issue, not its main focus.
- [ ] `ui/console/ConsoleMenu.java`, `ui/console/MenuHandlers.java` — a
      plain text menu, no graphical design work
- [ ] Wire in your own dispatch operations as menu options
- [ ] Other services' menu options are added incrementally by Issue #14

### Tests
- Empty request list, single request
- Budget of zero; budget exceeding all requests combined
- Each dispatch mode produces a different, correct order on the same
  input

### Evidence
- DP memoisation/tabulation table with reconstruction
- Greedy-vs-DP counterexample write-up
- One proof sketch (greedy exchange argument or DP optimal substructure)

## Depends on
Issues #4, #5.

---

# ISSUE #14 — Integration, Database, Reporting & Performance

**Owner:** [assign]

## What you're building

### Integration
- [ ] `Main.java` — starts the database connection, hands off to the
      console menu
- [ ] Extends the console menu started in Issue #13 with other members'
      operations as they land
- [ ] Continuous PR review across all other issues
- [ ] Coordinate any schema changes another issue needs

### Reporting
- [ ] `service/ReportingService.java`:
  - Request counts by status (New, Assigned, In Transit, Completed)
  - Breakdown by waste category and by priority
  - Resource utilisation (available vs. busy)
  - For completed requests: actual time taken vs. deadline

### Performance
- [ ] `service/ExperimentService.java` and `util/Timer.java`
- [ ] `domain/AlgorithmRun.java` and `db/AlgorithmRunRepository.java` —
      already written
- [ ] Six required categories: search comparison (via
      `SearchAlgorithm`), sorting comparison (via `SortAlgorithm`),
      hash-table load factor, BST vs. balanced tree (via
      `SearchTreeADT`), heap/priority dispatch, graph algorithms — each
      at the specified input sizes, 3+ runs, exported to CSV, stored in
      `algorithm_runs`

### Index parameters
- [ ] `util/IndexParameterDeriver.java` — already written with
      placeholder logic; finalise once every member's index number is
      collected

### Integration tests
- [ ] Full path: CSV → database → domain objects → structures →
      algorithms → service results

## Depends on
Meaningfully complete versions of other issues, though this work is
continuous.

## Definition of Done
- [ ] Database loads and all core services are reachable
- [ ] Console menu runs every implemented operation without source edits
- [ ] All 6 experiment categories produce CSVs and populate
      `algorithm_runs`
- [ ] Operational reports can be generated
- [ ] End-to-end integration tests pass
- [ ] Reviewed and merged

---

# TEAM-WIDE REQUIREMENTS

## Testing
At least 40 unit tests, covering normal, boundary, and invalid-input
cases, including: empty structure, single element, duplicate keys,
disconnected graph, unreachable path, queue full/empty, hash collision.

## Trace evidence
At least six trace tables: binary search, insertion sort, merge sort or
quicksort, Dijkstra, Kruskal or Prim, dynamic programming.

## Correctness evidence
At least three proof sketches: a loop invariant, an induction/recursion
argument, a greedy-or-DP correctness argument.

## Counterexamples
At least two: the greedy dispatch failure case (Issue #13), the
binary-search invalid-precondition case (Issue #5).

## Performance
Every experiment at the required input sizes, 3+ runs each, same
machine, raw results retained, exported to CSV, with a brief discussion
of theoretical versus observed complexity.

## Database
The final application reads from and writes to the database throughout
operation, not only at startup.

## GitHub
Work from your assigned issue, use a feature branch, commit regularly,
open a pull request, and get it reviewed before merging. No direct
pushes to `main`.

---

# DEPENDENCY OVERVIEW

```
Issue #1
   │
   ├── #2 Linked List
   ├── #3 Stack & Audit Log
   ├── #4 Queue & Linear Search
   ├── #5 Deque, Heap & Binary Search
   ├── #6 BST & Insertion Sort
   ├── #8 B-Tree & Merge Sort
   ├── #9 Hash Table
   └── #10 Graph (List) & Disjoint Set & BFS
          │
          ├── #11 Graph (Matrix) & DFS
          │
   #6 ─────┴── #7 Red-Black Tree & Selection Sort
          │
#5, #10, #11 ── #12 Routing (Dijkstra, Prim, Kruskal)
          │
   #4, #5 ── #13 Dispatch, Optimisation & Console (Lead)
          │
          └── #14 Integration, Database, Reporting & Performance
                 │
          Complete System
```

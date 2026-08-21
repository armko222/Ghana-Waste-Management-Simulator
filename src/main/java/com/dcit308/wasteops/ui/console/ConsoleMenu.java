package com.dcit308.wasteops.ui.console;

import com.dcit308.wasteops.db.CsvImporter;
import com.dcit308.wasteops.db.DatabaseManager;
import com.dcit308.wasteops.service.DispatchService;
import com.dcit308.wasteops.service.ExperimentService;
import com.dcit308.wasteops.service.ReportingService;
import com.dcit308.wasteops.service.RoutingService;

import java.util.Scanner;

/**
 * Plain-text interactive console menu.
 *
 * <p>Satisfies the brief's requirement (Section 8.iv) that an examiner can
 * exercise every implemented feature without editing source code.
 *
 * <p>Started by Issue #13 (dispatch operations); extended here (Issue #14)
 * with reporting, experiments, and any further operations as teammate PRs land.
 *
 * <p>Every menu option is wrapped so that if a dependency has not yet been
 * implemented (throws {@code UnsupportedOperationException}), a clear
 * "[NOT AVAILABLE]" message is shown instead of a crash.
 *
 * Owned by Issue #13 (start) / Issue #14 (extend).
 */
public class ConsoleMenu {

    private final DatabaseManager db;
    private final ReportingService reportingService;
    private final ExperimentService experimentService;
    private final DispatchService dispatchService;
    private final RoutingService routingService;
    private final Scanner scanner;

    public ConsoleMenu(DatabaseManager db) {
        this.db = db;
        this.reportingService = new ReportingService(db);
        this.experimentService = new ExperimentService(db);
        this.dispatchService = new DispatchService();
        this.routingService = new RoutingService();
        this.scanner = new Scanner(System.in);
    }

    /** Starts the interactive menu loop. Runs until the user chooses to exit. */
    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().strip();
            System.out.println();
            switch (choice) {
                case "1"  -> handleSafe("CSV Import", this::handleImportData);
                case "2"  -> handleSafe("Operational Report", this::handleOperationalReport);
                case "3"  -> handleSafe("Performance Experiments", this::handleRunExperiments);
                case "4"  -> handleSafe("FIFO Dispatch", this::handleDispatchFifo);
                case "5"  -> handleSafe("Urgency Dispatch", this::handleDispatchUrgency);
                case "6"  -> handleSafe("Priority Dispatch", this::handleDispatchPriority);
                case "7"  -> handleSafe("Fastest Route", this::handleFastestRoute);
                case "8"  -> handleSafe("Reachable Locations", this::handleReachableLocations);
                case "9"  -> handleSafe("Min Connecting Network", this::handleMinNetwork);
                case "10" -> handleSafe("Greedy Dispatch", this::handleGreedyDispatch);
                case "11" -> handleSafe("DP Budget Selection", this::handleDpBudget);
                case "0"  -> running = false;
                default   -> System.out.println("[WARN] Unknown option — please enter a number from the menu.");
            }
            System.out.println();
        }
        System.out.println("Goodbye.");
    }

    // -------------------------------------------------------------------------
    // Menu display
    // -------------------------------------------------------------------------

    private void printMenu() {
        System.out.println("─────────────────────────────────────────");
        System.out.println(" MAIN MENU");
        System.out.println("─────────────────────────────────────────");
        System.out.println("  1  Import CSV data");
        System.out.println("  2  Operational report");
        System.out.println("  3  Run performance experiments");
        System.out.println("  4  Dispatch — FIFO");
        System.out.println("  5  Dispatch — by urgency");
        System.out.println("  6  Dispatch — by priority");
        System.out.println("  7  Find fastest route");
        System.out.println("  8  Find reachable locations");
        System.out.println("  9  Minimum connecting network");
        System.out.println(" 10  Greedy dispatch");
        System.out.println(" 11  DP budget selection");
        System.out.println("  0  Exit");
        System.out.println("─────────────────────────────────────────");
        System.out.print("Choice: ");
    }

    // -------------------------------------------------------------------------
    // Handler methods — each delegates to the appropriate service
    // -------------------------------------------------------------------------

    private void handleImportData() {
        System.out.print("CSV directory path [data/csv]: ");
        String path = scanner.nextLine().strip();
        if (path.isEmpty()) {
            path = "data/csv";
        }
        new CsvImporter().importAll(path);
    }

    private void handleOperationalReport() {
        System.out.println(reportingService.generateOperationalReport());
    }

    private void handleRunExperiments() {
        experimentService.runAllExperiments();
    }

    private void handleDispatchFifo() {
        System.out.println(dispatchService.dispatchNextFifo());
    }

    private void handleDispatchUrgency() {
        System.out.println(dispatchService.dispatchNextByUrgency());
    }

    private void handleDispatchPriority() {
        System.out.println(dispatchService.dispatchNextByPriority());
    }

    private void handleFastestRoute() {
        System.out.print("Source location ID: ");
        String from = scanner.nextLine().strip();
        System.out.print("Destination location ID: ");
        String to = scanner.nextLine().strip();
        var result = routingService.fastestRoute(from, to);
        if (result.reachable) {
            System.out.println("Route: " + result.path);
            System.out.printf("Total weight: %.2f%n", result.totalWeight);
        } else {
            System.out.println("No route found — destination is unreachable.");
        }
    }

    private void handleReachableLocations() {
        System.out.print("From location ID: ");
        String from = scanner.nextLine().strip();
        var locations = routingService.reachableLocations(from);
        System.out.println("Reachable locations (" + locations.size() + "): " + locations);
    }

    private void handleMinNetwork() {
        var result = routingService.minimumConnectingNetwork();
        System.out.println("Minimum connecting network edges:");
        for (String edge : result.edgeDescriptions) {
            System.out.println("  " + edge);
        }
        System.out.printf("Total cost: %.2f%n", result.totalCost);
    }

    private void handleGreedyDispatch() {
        // Greedy dispatch needs a request + available resources.
        // Issue #13 owns the logic — delegate to DispatchService.
        System.out.println("[INFO] Greedy dispatch — delegates to Issue #13's GreedyDispatch.");
        throw new UnsupportedOperationException(
                "TODO: Issue #13 — expose greedy dispatch in DispatchService.");
    }

    private void handleDpBudget() {
        System.out.print("Budget constraint: ");
        String input = scanner.nextLine().strip();
        int budget;
        try {
            budget = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid budget — enter an integer.");
            return;
        }
        // KnapsackDP logic is owned by Issue #13.
        System.out.println("[INFO] DP budget selection with budget = " + budget);
        throw new UnsupportedOperationException(
                "TODO: Issue #13 — expose DP budget selection in DispatchService.");
    }

    // -------------------------------------------------------------------------
    // Safely wraps any handler so UnsupportedOperationException from
    // unimplemented dependencies produces a clear message, not a crash.
    // -------------------------------------------------------------------------

    private void handleSafe(String label, Runnable handler) {
        try {
            handler.run();
        } catch (UnsupportedOperationException e) {
            System.out.println("[NOT AVAILABLE] " + label);
            System.out.println("  → " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] " + label + ": "
                    + e.getClass().getSimpleName() + " — " + e.getMessage());
        }
    }
}

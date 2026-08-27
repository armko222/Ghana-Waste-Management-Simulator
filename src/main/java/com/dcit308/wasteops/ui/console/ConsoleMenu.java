package com.dcit308.wasteops.ui.console;

import java.util.List;
import java.util.Scanner;

import com.dcit308.wasteops.algorithms.optimisation.GreedyDispatch;
import com.dcit308.wasteops.algorithms.optimisation.KnapsackDP;
import com.dcit308.wasteops.db.CsvImporter;
import com.dcit308.wasteops.db.DatabaseManager;
import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.service.DispatchService;
import com.dcit308.wasteops.service.ExperimentService;
import com.dcit308.wasteops.service.ReportingService;
import com.dcit308.wasteops.service.RoutingService;

/**
 * Plain-text interactive console menu.
 *
 * <p>
 * The console is intentionally simple. Its purpose is to allow an
 * examiner to exercise the implemented system without editing source
 * code.
 *
 * <p>
 * Issue #13 provides the dispatch and optimisation operations.
 * Routing, reporting, and experiments are exposed here as those
 * services become available.
 *
 * <p>
 * This console is not intended to be the final user interface.
 * A graphical Java UI can later call the service layer directly.
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

        this.reportingService =
                new ReportingService(db);

        this.experimentService =
                new ExperimentService(db);

        /*
         * DispatchService requires the application's shared
         * DatabaseManager so that it can access pending requests.
         */
        this.dispatchService =
                new DispatchService(db);

        this.routingService =
                new RoutingService(db);

        this.scanner =
                new Scanner(System.in);
    }

    /**
     * Starts the interactive menu loop.
     */
    public void run() {

        boolean running = true;

        while (running) {

            printMenu();

            String choice =
                    scanner.nextLine().strip();

            System.out.println();

            switch (choice) {

                case "1" ->
                        handleSafe(
                                "CSV Import",
                                this::handleImportData
                        );

                case "2" ->
                        handleSafe(
                                "Operational Report",
                                this::handleOperationalReport
                        );

                case "3" ->
                        handleSafe(
                                "Performance Experiments",
                                this::handleRunExperiments
                        );

                case "4" ->
                        handleSafe(
                                "FIFO Dispatch",
                                this::handleDispatchFifo
                        );

                case "5" ->
                        handleSafe(
                                "Urgency Dispatch",
                                this::handleDispatchUrgency
                        );

                case "6" ->
                        handleSafe(
                                "Priority Dispatch",
                                this::handleDispatchPriority
                        );

                case "7" ->
                        handleSafe(
                                "Fastest Route",
                                this::handleFastestRoute
                        );

                case "8" ->
                        handleSafe(
                                "Reachable Locations",
                                this::handleReachableLocations
                        );

                case "9" ->
                        handleSafe(
                                "Min Connecting Network",
                                this::handleMinNetwork
                        );

                case "10" ->
                        handleSafe(
                                "Greedy Dispatch",
                                this::handleGreedyDispatch
                        );

                case "11" ->
                        handleSafe(
                                "DP Budget Selection",
                                this::handleDpBudget
                        );

                case "12" ->
                        handleSafe(
                                "View Pending Requests",
                                this::handleViewPending
                        );

                case "0" ->
                        running = false;

                default ->
                        System.out.println(
                                "[WARN] Unknown option — "
                                        + "please enter a number from the menu."
                        );
            }

            System.out.println();
        }

        System.out.println("Goodbye.");
    }

    // -------------------------------------------------------------------------
    // Menu display
    // -------------------------------------------------------------------------

    private void printMenu() {

        System.out.println(
                "─────────────────────────────────────────"
        );

        System.out.println(
                " GHANA WASTE MANAGEMENT OPERATIONS OPTIMIZER"
        );

        System.out.println(
                "─────────────────────────────────────────"
        );

        System.out.println(
                "  1  Import CSV data"
        );

        System.out.println(
                "  2  Operational report"
        );

        System.out.println(
                "  3  Run performance experiments"
        );

        System.out.println(
                "  4  Dispatch — FIFO"
        );

        System.out.println(
                "  5  Dispatch — by urgency"
        );

        System.out.println(
                "  6  Dispatch — by priority"
        );

        System.out.println(
                "  7  Find fastest route"
        );

        System.out.println(
                "  8  Find reachable locations"
        );

        System.out.println(
                "  9  Minimum connecting network"
        );

        System.out.println(
                " 10  Greedy request selection"
        );

        System.out.println(
                " 11  DP budget selection"
        );

        System.out.println(
                " 12  View pending requests"
        );

        System.out.println(
                "  0  Exit"
        );

        System.out.println(
                "─────────────────────────────────────────"
        );

        System.out.print("Choice: ");
    }

    // -------------------------------------------------------------------------
    // Data
    // -------------------------------------------------------------------------

    private void handleImportData() {

        System.out.print(
                "CSV directory path [data/csv]: "
        );

        String path =
                scanner.nextLine().strip();

        if (path.isEmpty()) {
            path = "data/csv";
        }

        new CsvImporter().importAll(path);

        System.out.println(
                "[OK] CSV data imported successfully."
        );
    }

    // -------------------------------------------------------------------------
    // Reporting / experiments
    // -------------------------------------------------------------------------

    private void handleOperationalReport() {

        System.out.println(
                reportingService.generateOperationalReport()
        );
    }

    private void handleRunExperiments() {

        experimentService.runAllExperiments();
    }

    // -------------------------------------------------------------------------
    // Dispatch
    // -------------------------------------------------------------------------

    private void handleDispatchFifo() {

        ServiceRequest request =
                dispatchService.dispatchNextFifo();

        printDispatchedRequest(
                "FIFO",
                request
        );
    }

    private void handleDispatchUrgency() {

        ServiceRequest request =
                dispatchService.dispatchNextByUrgency();

        printDispatchedRequest(
                "Urgency",
                request
        );
    }

    private void handleDispatchPriority() {

        ServiceRequest request =
                dispatchService.dispatchNextByPriority();

        printDispatchedRequest(
                "Priority",
                request
        );
    }

    private void printDispatchedRequest(
            String mode,
            ServiceRequest request) {

        if (request == null) {

            System.out.println(
                    "[INFO] No pending requests."
            );

            return;
        }

        System.out.println(
                "[OK] Next request (" + mode + "):"
        );

        System.out.println(
                "     ID: " + request.getRequestId()
        );

        System.out.println(
                "     Category: " + request.getCategory()
        );

        System.out.println(
                "     Priority: " + request.getPriority()
        );

        System.out.println(
                "     Urgency: " + request.getUrgency()
        );

        System.out.println(
                "     Submitted: " + request.getTimeSubmitted()
        );
    }

    // -------------------------------------------------------------------------
    // Pending requests
    // -------------------------------------------------------------------------

    private void handleViewPending() {

        List<ServiceRequest> pending =
                dispatchService.getPendingRequests();

        if (pending.isEmpty()) {

            System.out.println(
                    "[INFO] No pending requests."
            );

            return;
        }

        System.out.println(
                "Pending requests (" + pending.size() + "):"
        );

        for (ServiceRequest request : pending) {

            System.out.println(
                    "  "
                            + request.getRequestId()
                            + " | priority="
                            + request.getPriority()
                            + " | urgency="
                            + request.getUrgency()
                            + " | submitted="
                            + request.getTimeSubmitted()
            );
        }
    }

    // -------------------------------------------------------------------------
    // Optimisation
    // -------------------------------------------------------------------------

    private void handleGreedyDispatch() {

        int budget =
                askForBudget();

        GreedyDispatch.Selection result =
                dispatchService.selectPendingByGreedy(
                        budget
                );

        printSelection(
                "Greedy",
                result.chosen,
                result.totalWeight,
                result.totalValue
        );
    }

    private void handleDpBudget() {

        int budget =
                askForBudget();

        KnapsackDP.Selection result =
                dispatchService.selectPendingByDp(
                        budget
                );

        printSelection(
                "Dynamic Programming",
                result.chosen,
                result.totalWeight,
                result.totalValue
        );
    }

    private int askForBudget() {

        System.out.print(
                "Budget constraint: "
        );

        String input =
                scanner.nextLine().strip();

        try {

            int budget =
                    Integer.parseInt(input);

            if (budget < 0) {

                System.out.println(
                        "[ERROR] Budget cannot be negative."
                );

                return askForBudget();
            }

            return budget;

        } catch (NumberFormatException e) {

            System.out.println(
                    "[ERROR] Invalid budget — enter an integer."
            );

            return askForBudget();
        }
    }

    private void printSelection(
            String label,
            List<ServiceRequest> chosen,
            int totalWeight,
            int totalValue) {

        System.out.println(
                label
                        + " selection:"
        );

        System.out.println(
                "  Total weight: "
                        + totalWeight
        );

        System.out.println(
                "  Total value: "
                        + totalValue
        );

        if (chosen.isEmpty()) {

            System.out.println(
                    "  No requests selected."
            );

            return;
        }

        System.out.println(
                "  Selected requests:"
        );

        for (ServiceRequest request : chosen) {

            System.out.println(
                    "    "
                            + request.getRequestId()
                            + " | category="
                            + request.getCategory()
                            + " | priority="
                            + request.getPriority()
                            + " | urgency="
                            + request.getUrgency()
            );
        }
    }

    // -------------------------------------------------------------------------
    // Routing
    // -------------------------------------------------------------------------

    private void handleFastestRoute() {

        System.out.print(
                "Source location ID: "
        );

        String from =
                scanner.nextLine().strip();

        System.out.print(
                "Destination location ID: "
        );

        String to =
                scanner.nextLine().strip();

        var result =
                routingService.fastestRoute(
                        from,
                        to
                );

        if (result.reachable) {

            System.out.println(
                    "Route: " + result.path
            );

            System.out.printf(
                    "Total weight: %.2f%n",
                    result.totalWeight
            );

        } else {

            System.out.println(
                    "No route found — destination is unreachable."
            );
        }
    }

    private void handleReachableLocations() {

        System.out.print(
                "From location ID: "
        );

        String from =
                scanner.nextLine().strip();

        var locations =
                routingService.reachableLocations(
                        from
                );

        System.out.println(
                "Reachable locations ("
                        + locations.size()
                        + "): "
                        + locations
        );
    }

    private void handleMinNetwork() {

        var result =
                routingService.minimumConnectingNetwork();

        System.out.println(
                "Minimum connecting network edges:"
        );

        for (String edge :
                result.edgeDescriptions) {

            System.out.println(
                    "  " + edge
            );
        }

        System.out.printf(
                "Total cost: %.2f%n",
                result.totalCost
        );
    }

    // -------------------------------------------------------------------------
    // Error handling
    // -------------------------------------------------------------------------

    private void handleSafe(
            String label,
            Runnable handler) {

        try {

            handler.run();

        } catch (UnsupportedOperationException e) {

            System.out.println(
                    "[NOT AVAILABLE] "
                            + label
            );

            System.out.println(
                    "  → " + e.getMessage()
            );

        } catch (Exception e) {

            System.out.println(
                    "[ERROR] "
                            + label
                            + ": "
                            + e.getClass().getSimpleName()
                            + " — "
                            + e.getMessage()
            );
        }
    }
}
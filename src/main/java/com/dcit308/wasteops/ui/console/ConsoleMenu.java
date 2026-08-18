package com.dcit308.wasteops.ui.console;

import com.dcit308.wasteops.db.DatabaseManager;
import com.dcit308.wasteops.service.ReportingService;

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
 * Owned by Issue #13 (start) / Issue #14 (extend).
 */
public class ConsoleMenu {

    private final DatabaseManager db;
    private final ReportingService reportingService;
    private final Scanner scanner;

    public ConsoleMenu(DatabaseManager db) {
        this.db = db;
        this.reportingService = new ReportingService(db);
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
                case "1" -> handleImportData();
                case "2" -> handleOperationalReport();
                case "3" -> handleRunExperiments();
                case "0" -> running = false;
                default  -> System.out.println("[WARN] Unknown option — please enter a number from the menu.");
            }
            System.out.println();
        }
        System.out.println("Goodbye.");
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void printMenu() {
        System.out.println("─────────────────────────────────────────");
        System.out.println(" MAIN MENU");
        System.out.println("─────────────────────────────────────────");
        System.out.println(" 1  Import CSV data");
        System.out.println(" 2  Operational report");
        System.out.println(" 3  Run performance experiments");
        System.out.println(" 0  Exit");
        System.out.println("─────────────────────────────────────────");
        System.out.print("Choice: ");
    }

    private void handleImportData() {
        // TODO (Issue #1 / #14): delegate to CsvImporter once Issue #1 lands.
        System.out.println("[INFO] CSV import — awaiting Issue #1 implementation.");
    }

    private void handleOperationalReport() {
        System.out.println(reportingService.generateOperationalReport());
    }

    private void handleRunExperiments() {
        // TODO (Issue #14): delegate to ExperimentService once algorithm
        // implementations from other issues are available.
        System.out.println("[INFO] Performance experiments — to be wired once algorithm PRs land.");
    }
}

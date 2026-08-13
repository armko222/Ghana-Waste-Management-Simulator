package com.dcit308.wasteops;

/**
 * Entry point. Currently a placeholder — Issue #14 wires this to
 * db.DatabaseManager and ui.console.ConsoleMenu once those exist.
 *
 * See README.md for the intended startup sequence:
 *   1. DatabaseManager initializes schema (data/sql/schema.sql) if needed
 *   2. ConsoleMenu is launched, offering "Import Data" as its first option
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Ghana Waste Management Operations Optimizer");
        System.out.println("============================================");
        System.out.println();
        System.out.println("TODO (Issue #14): wire this to:");
        System.out.println("  1. db.DatabaseManager.connect() / initSchemaIfNeeded()");
        System.out.println("  2. ui.console.ConsoleMenu.run()");
        System.out.println();
        System.out.println("See README.md for the full startup sequence.");
    }
}

package com.dcit308.wasteops;

import com.dcit308.wasteops.db.DatabaseManager;
import com.dcit308.wasteops.ui.console.ConsoleMenu;

/**
 * Application entry point.
 *
 * <p>Startup sequence:
 * <ol>
 *   <li>Open the SQLite connection via {@link DatabaseManager#connect()}</li>
 *   <li>Apply schema if the database is new via {@link DatabaseManager#initSchemaIfNeeded()}</li>
 *   <li>Hand off to the interactive console menu</li>
 * </ol>
 *
 * Owned by Issue #14.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Ghana Waste Management Operations Optimizer");
        System.out.println("============================================");
        System.out.println();

        DatabaseManager db = new DatabaseManager();
        try {
            db.connect();
            db.initSchemaIfNeeded();
            System.out.println("[OK] Database ready.");
            System.out.println();

            new ConsoleMenu(db).run();

        } catch (RuntimeException e) {
            System.err.println("[ERROR] Startup failed: " + e.getMessage());
            System.exit(1);
        } finally {
            db.close();
        }
    }
}

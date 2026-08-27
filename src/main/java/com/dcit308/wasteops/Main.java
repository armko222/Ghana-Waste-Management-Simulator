package com.dcit308.wasteops;

import com.dcit308.wasteops.db.DatabaseManager;
import com.dcit308.wasteops.ui.console.ConsoleMenu;

/**
 * Application entry point.
 *
 * <p>
 * Opens the SQLite database and starts the interactive console.
 *
 * <p>
 * The console is currently the application's interface. A graphical
 * Java UI can later use the same service layer without changing the
 * underlying algorithms or data structures.
 *
 * Owned by Issue #14.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println(
                "Ghana Waste Management Operations Optimizer"
        );

        System.out.println(
                "============================================"
        );

        System.out.println();

        DatabaseManager db =
                new DatabaseManager();

        try {

            db.connect();

            System.out.println(
                    "[OK] Database ready."
            );

            System.out.println();

            ConsoleMenu menu =
                    new ConsoleMenu(db);

            menu.run();

        } catch (RuntimeException e) {

            System.err.println(
                    "[ERROR] Startup failed: "
                            + e.getMessage()
            );

            e.printStackTrace();

            System.exit(1);

        } finally {

            db.close();
        }
    }
}
package com.dcit308.wasteops.ui.console;

/**
 * Individual handler methods for each menu option, kept separate from
 * ConsoleMenu's loop/display logic. Started by Issue #13, extended by
 * Issue #14.
 *
 * <p>Note: as of Issue #14's implementation, all handler logic has been
 * inlined into {@link ConsoleMenu} for simplicity — each menu option's
 * handler is small enough that a separate class adds indirection without
 * benefit. If Issue #13 or a later refactor adds significantly heavier
 * handlers, they can be extracted back here.
 *
 * <p>This class remains as the designated extension point per the
 * original architecture document.
 */
public class MenuHandlers {

    // Handler methods are currently inlined in ConsoleMenu.
    // See ConsoleMenu.handleImportData(), handleOperationalReport(),
    // handleRunExperiments(), handleDispatchFifo(), etc.
    //
    // Issue #13 members: add your dispatch operation handlers here if
    // they grow complex enough to warrant extraction from the menu class.
}

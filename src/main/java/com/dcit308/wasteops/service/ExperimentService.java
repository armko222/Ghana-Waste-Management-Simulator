package com.dcit308.wasteops.service;

/**
 * Runs the six required performance-experiment categories (search
 * comparison, sorting comparison, hash-table load factor, BST vs.
 * balanced tree, heap/priority dispatch, graph algorithms) at the
 * required input sizes, three-plus runs each, exports CSV, and
 * populates the algorithm_runs table.
 *
 * Owned by Issue #14, coordinating with every structure/algorithm owner
 * so each category is actually executable -- use the SearchAlgorithm
 * and SortAlgorithm interfaces (see structures/ and algorithms/) to time
 * every implementation through one shared loop rather than bespoke code
 * per algorithm.
 */
public class ExperimentService {

    public void runAllExperiments() {
        throw new UnsupportedOperationException("TODO: Issue #14 \u2014 implement the six required experiment categories.");
    }
}

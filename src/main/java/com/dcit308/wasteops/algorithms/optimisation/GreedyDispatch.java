package com.dcit308.wasteops.algorithms.optimisation;

import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.domain.Resource;
import java.util.List;

/**
 * Quick, practical dispatch choice: nearest available suitable resource
 * for the highest-priority request. Must be paired with a constructed
 * counterexample showing where it loses to KnapsackDP (required
 * evidence, brief Section 10).
 *
 * Owned by Issue #13.
 */
public class GreedyDispatch {

    public Resource selectResource(ServiceRequest request, List<Resource> availableResources) {
        throw new UnsupportedOperationException("TODO: Issue #13 \u2014 implement greedy resource selection.");
    }
}

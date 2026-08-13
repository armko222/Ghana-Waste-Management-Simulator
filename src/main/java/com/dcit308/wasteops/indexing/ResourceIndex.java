package com.dcit308.wasteops.indexing;

import com.dcit308.wasteops.domain.Resource;
import com.dcit308.wasteops.structures.BTree;

/**
 * Fast lookup of Resources, backed by Issue #8's BTree.
 *
 * Owned by Issue #8.
 */
public class ResourceIndex {

    private final BTree<String, Resource> tree;

    public ResourceIndex(BTree<String, Resource> tree) {
        this.tree = tree;
    }

    public void index(Resource resource) {
        throw new UnsupportedOperationException("TODO: Issue #8 \u2014 implement index.");
    }

    public Resource findById(String resourceId) {
        throw new UnsupportedOperationException("TODO: Issue #8 \u2014 implement findById.");
    }
}

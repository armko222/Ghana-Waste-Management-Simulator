package com.dcit308.wasteops.indexing;

import com.dcit308.wasteops.domain.Resource;
import com.dcit308.wasteops.structures.BTree;

/**
 * Fast lookup of Resources by resourceId, backed by Issue #8's BTree.
 * This is the "genuine use in the system" evidence the brief asks for
 * (Section 6): whenever dispatch (Issue #13) needs to find an available
 * resource by ID quickly, or reporting (Issue #14) needs to look one up,
 * this index is what they go through instead of scanning the full
 * resource list.
 *
 * Owned by Issue #8.
 */
public class ResourceIndex {

    private final BTree<String, Resource> tree;

    public ResourceIndex(BTree<String, Resource> tree) {
        if (tree == null) {
            throw new IllegalArgumentException("tree cannot be null");
        }
        this.tree = tree;
    }

    /** Indexes resource by its resourceId. Re-indexing the same ID overwrites the entry. */
    public void index(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("resource cannot be null");
        }
        tree.insert(resource.getResourceId(), resource);
    }

    /** Returns the Resource with this ID, or null if none is indexed. */
    public Resource findById(String resourceId) {
        if (resourceId == null) {
            return null;
        }
        return tree.search(resourceId);
    }
}

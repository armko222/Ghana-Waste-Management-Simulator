package com.dcit308.wasteops.indexing;

import com.dcit308.wasteops.domain.Resource;
import com.dcit308.wasteops.structures.BTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for Issue #8's ResourceIndex, built on its BTree.
 */
class ResourceIndexTest {

    private BTree<String, Resource> tree;
    private ResourceIndex index;

    @BeforeEach
    void setUp() {
        tree = new BTree<>();
        index = new ResourceIndex(tree);
    }

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("rejects a null tree")
        void rejectsNullTree() {
            assertThrows(IllegalArgumentException.class, () -> new ResourceIndex(null));
        }
    }

    @Nested
    @DisplayName("Indexing and lookup")
    class IndexingAndLookup {

        @Test
        @DisplayName("an indexed resource is found by its own resourceId")
        void findsIndexedResource() {
            Resource resource = new Resource("RES-001", Resource.Type.GENERAL, "LOC-01", 500);

            index.index(resource);

            assertSame(resource, index.findById("RES-001"));
        }

        @Test
        @DisplayName("an unindexed resourceId returns null")
        void unindexedIdReturnsNull() {
            assertNull(index.findById("RES-999"));
        }

        @Test
        @DisplayName("a null resourceId returns null rather than throwing")
        void nullIdReturnsNull() {
            assertNull(index.findById(null));
        }

        @Test
        @DisplayName("indexing a null resource throws")
        void indexingNullResourceThrows() {
            assertThrows(IllegalArgumentException.class, () -> index.index(null));
        }

        @Test
        @DisplayName("re-indexing the same resourceId overwrites the previous entry")
        void reindexingOverwrites() {
            Resource original = new Resource("RES-001", Resource.Type.GENERAL, "LOC-01", 500);
            Resource replacement = new Resource("RES-001", Resource.Type.HAZARDOUS, "LOC-02", 200);

            index.index(original);
            index.index(replacement);

            assertSame(replacement, index.findById("RES-001"));
            assertEquals(1, tree.size());
        }

        @Test
        @DisplayName("multiple distinct resources are all independently retrievable")
        void multipleResourcesAreIndependentlyRetrievable() {
            Resource r1 = new Resource("RES-001", Resource.Type.GENERAL, "LOC-01", 500);
            Resource r2 = new Resource("RES-002", Resource.Type.HAZARDOUS, "LOC-01", 200);
            Resource r3 = new Resource("RES-003", Resource.Type.INDUSTRIAL, "LOC-02", 750);

            index.index(r1);
            index.index(r2);
            index.index(r3);

            assertSame(r1, index.findById("RES-001"));
            assertSame(r2, index.findById("RES-002"));
            assertSame(r3, index.findById("RES-003"));
            assertEquals(3, tree.size());
        }
    }
}

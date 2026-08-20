package com.dcit308.wasteops.structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #10's DisjointSet.
 *
 * Same package as the class under test (different source root), so
 * package-private members stay reachable if later tests need them.
 */
class DisjointSetTest {

    // ---- reflection helpers --------------------------------------------
    // DisjointSet exposes only makeSet/find/union/connected -- it never
    // hands out its internal DSNode tree. Path compression is an internal
    // structural guarantee, not an observable one through that API, so
    // proving it actually happened means reaching into the private
    // DSNode.parent pointers directly rather than only checking find()'s
    // return value.

    private static Class<?> dsNodeClass() {
        for (Class<?> nested : DisjointSet.class.getDeclaredClasses()) {
            if (nested.getSimpleName().equals("DSNode")) {
                return nested;
            }
        }
        throw new IllegalStateException("DisjointSet.DSNode not found -- has the class been renamed?");
    }

    private static Field accessibleField(Class<?> owner, String name) throws NoSuchFieldException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /** Finds the private DSNode backing a given element, by id. */
    private static Object dsNodeFor(DisjointSet ds, String element) throws Exception {
        Field headField = accessibleField(DisjointSet.class, "entriesHead");
        Field idField = accessibleField(dsNodeClass(), "id");
        Field nextField = accessibleField(dsNodeClass(), "next");

        Object current = headField.get(ds);
        while (current != null) {
            if (element.equals(idField.get(current))) {
                return current;
            }
            current = nextField.get(current);
        }
        throw new AssertionError("No DSNode found for element: " + element);
    }

    private static Object parentOf(Object dsNode) throws Exception {
        return accessibleField(dsNodeClass(), "parent").get(dsNode);
    }

    // ---- repeated union ---------------------------------------------------

    @Nested
    @DisplayName("Repeated union")
    class RepeatedUnion {

        @Test
        @DisplayName("unioning the same pair twice is a no-op the second time")
        void unioningSamePairTwiceIsIdempotent() {
            DisjointSet ds = new DisjointSet();
            ds.makeSet("A");
            ds.makeSet("B");

            ds.union("A", "B");
            String rootAfterFirst = ds.find("A");

            ds.union("A", "B");
            String rootAfterSecond = ds.find("A");

            assertEquals(rootAfterFirst, rootAfterSecond);
            assertTrue(ds.connected("A", "B"));
        }

        @Test
        @DisplayName("unioning already-connected elements (via a third element) is a no-op")
        void unioningAlreadyTransitivelyConnectedIsNoOp() {
            DisjointSet ds = new DisjointSet();
            for (String s : List.of("A", "B", "C")) {
                ds.makeSet(s);
            }

            ds.union("A", "B");
            ds.union("B", "C");
            assertTrue(ds.connected("A", "C"), "A and C should already be connected through B");

            // A and C were never unioned directly -- only via B. This should
            // be a harmless no-op, not throw or corrupt existing links.
            ds.union("A", "C");

            assertTrue(ds.connected("A", "B"));
            assertTrue(ds.connected("B", "C"));
            assertTrue(ds.connected("A", "C"));
        }

        @Test
        @DisplayName("a chain of unions transitively connects every element")
        void chainOfUnionsTransitivelyConnects() {
            DisjointSet ds = new DisjointSet();
            for (String s : List.of("A", "B", "C", "D", "E")) {
                ds.makeSet(s);
            }

            ds.union("A", "B");
            ds.union("B", "C");
            ds.union("C", "D");
            ds.union("D", "E");

            // A and E were never unioned directly.
            assertTrue(ds.connected("A", "E"));
            assertEquals(ds.find("A"), ds.find("E"));
        }

        @Test
        @DisplayName("unioning two separate chains keeps unrelated elements apart")
        void unioningSeparateChainsKeepsOthersApart() {
            DisjointSet ds = new DisjointSet();
            for (String s : List.of("A", "B", "C", "D")) {
                ds.makeSet(s);
            }

            ds.union("A", "B");
            ds.union("C", "D");
            assertFalse(ds.connected("A", "C"), "separate chains must not be connected yet");

            ds.union("B", "C");

            assertTrue(ds.connected("A", "D"), "merging the two chains connects everything");
        }

        @Test
        @DisplayName("union on an element that was never makeSet throws")
        void unionOnUnknownElementThrows() {
            DisjointSet ds = new DisjointSet();
            ds.makeSet("A");

            assertThrows(IllegalArgumentException.class, () -> ds.union("A", "Ghost"));
            assertThrows(IllegalArgumentException.class, () -> ds.union("Ghost", "A"));
        }
    }

    // ---- path compression ---------------------------------------------

    @Nested
    @DisplayName("Path compression")
    class PathCompression {

        @Test
        @DisplayName("find() flattens an indirect chain so the visited node points directly at the root")
        void findFlattensChainToRoot() throws Exception {
            DisjointSet ds = new DisjointSet();
            for (String s : List.of("A", "B", "C", "D")) {
                ds.makeSet(s);
            }

            // Two equal-rank pairs, then merge the pairs -- this produces a
            // genuine two-hop chain (D -> C -> A) rather than everything
            // attaching directly to one root, which union-by-rank alone
            // would otherwise tend to produce for simple cases.
            ds.union("A", "B"); // B -> A, rank(A) = 1
            ds.union("C", "D"); // D -> C, rank(C) = 1
            ds.union("A", "C"); // equal ranks -> C -> A, rank(A) = 2

            Object nodeD = dsNodeFor(ds, "D");
            Object nodeC = dsNodeFor(ds, "C");
            Object nodeA = dsNodeFor(ds, "A");

            // Sanity check: before find(), D really is two hops from the
            // root (D -> C -> A), not already flat.
            assertSame(nodeC, parentOf(nodeD), "D should start out pointing at C, not the root");
            assertNotSame(nodeA, parentOf(nodeD), "test setup should produce a real chain, not an already-flat one");

            String root = ds.find("D");

            assertEquals("A", root);
            assertSame(nodeA, parentOf(nodeD), "find() must compress D's path to point directly at the root");
        }

        @Test
        @DisplayName("a node already pointing at the root is unaffected by compression")
        void rootsOwnParentPointerIsUnaffected() throws Exception {
            DisjointSet ds = new DisjointSet();
            ds.makeSet("A");
            ds.makeSet("B");
            ds.union("A", "B"); // B -> A

            Object nodeA = dsNodeFor(ds, "A");

            ds.find("B");

            assertSame(nodeA, parentOf(nodeA), "a root's parent must always point at itself");
        }

        @Test
        @DisplayName("repeated find() calls after compression keep returning the same root")
        void repeatedFindStaysConsistentAfterCompression() {
            DisjointSet ds = new DisjointSet();
            for (String s : List.of("A", "B", "C", "D")) {
                ds.makeSet(s);
            }
            ds.union("A", "B");
            ds.union("C", "D");
            ds.union("A", "C");

            String firstCall = ds.find("D");
            String secondCall = ds.find("D");

            assertEquals(firstCall, secondCall);
            assertEquals("A", secondCall);
        }
    }
}

package com.dcit308.wasteops.structures.testutil;

import com.dcit308.wasteops.structures.DisjointSetADT;

import java.util.HashMap;
import java.util.Map;

/**
 * A deliberately naive, HashMap-backed DisjointSetADT implementation.
 *
 * This exists ONLY so that Issue #12 (Kruskal) can write and run real,
 * passing tests before Issue #10's actual DisjointSet (with path
 * compression and union by rank) exists. Same stand-in pattern as
 * NaivePriorityQueue — see Team_Handbook.docx, "Working With Each
 * Other's Code."
 *
 * NOT for production use.
 */
public class NaiveDisjointSet implements DisjointSetADT {

    private final Map<String, String> parent = new HashMap<>();

    @Override
    public void makeSet(String element) {
        parent.put(element, element);
    }

    @Override
    public String find(String element) {
        String root = element;
        while (!parent.get(root).equals(root)) {
            root = parent.get(root);
        }
        // Path compression
        String current = element;
        while (!current.equals(root)) {
            String next = parent.get(current);
            parent.put(current, root);
            current = next;
        }
        return root;
    }

    @Override
    public void union(String a, String b) {
        String rootA = find(a);
        String rootB = find(b);
        if (!rootA.equals(rootB)) {
            parent.put(rootA, rootB);
        }
    }
}

package tests;

import structures.ArrayDeque;
import structures.BinaryHeap;
import algorithms.search.BinarySearch;

public class Role5Tests {

    public static void main(String[] args) {
        runDequeTests();
        runHeapTests();
        runBinarySearchTests();
    }

    private static void runDequeTests() {
        System.out.println("--- DEQUE TESTS (URGENT DISPATCH EXAMPLE) ---");
        ArrayDeque<String> deque = new ArrayDeque<>();
        
        // 1. Empty Deque Test
        try {
            deque.removeFront();
        } catch (Exception e) {
            System.out.println("Empty Deque Exception Caught: " + e.getMessage());
        }

        // 2. Single Element Test
        deque.addFront("REQ-101 (Standard General Waste)");
        System.out.println("Removed Single Element: " + deque.removeRear() + " | Is Empty? " + deque.isEmpty());

        // 3. Wrap-around Test & Urgent Request Insertion Example
        deque.addRear("REQ-102 (Industrial Waste)");
        deque.addRear("REQ-103 (Standard General Waste)");
        
        // A hazardous spill occurs, it must bypass the standard FIFO queue!
        System.out.println(">> EMERGENCY: Hazardous spill reported! Bypassing queue...");
        deque.addFront("REQ-999 (URGENT Hazardous Waste Spill)");
        
        System.out.println("Next Dispatch (Front): " + deque.removeFront()); // Should output the URGENT spill
        System.out.println("Following Dispatch (Front): " + deque.removeFront()); // Should output REQ-102
        System.out.println();
    }

    private static void runHeapTests() {
        System.out.println("--- HEAP TESTS (URGENCY SCORE DISPATCH) ---");
        BinaryHeap<Integer> heap = new BinaryHeap<>();
        
        // 1. Empty Heap Test
        try {
            heap.extractMin();
        } catch (Exception e) {
            System.out.println("Empty Heap Exception Caught: " + e.getMessage());
        }

        // 2. Single Element Test
        heap.insert(42);
        System.out.println("Extracted Single Element: " + heap.extractMin() + " | Is Empty? " + heap.isEmpty());

        // 3. Randomized Insert/Extract Test (Lower number = Higher Urgency)
        System.out.print("Dispatch Order by Urgency Score (Expected: 1 3 5 8 10): ");
        heap.insert(8);
        heap.insert(1);
        heap.insert(10);
        heap.insert(3);
        heap.insert(5);
        
        while (!heap.isEmpty()) {
            System.out.print(heap.extractMin() + " ");
        }
        System.out.println("\n");
    }

    private static void runBinarySearchTests() {
        System.out.println("--- BINARY SEARCH TESTS (LOCATION LOOKUP) ---");
        
        // 1. Empty Array Test
        String[] emptyArray = {};
        System.out.println("Search in Empty Array: " + BinarySearch.search(emptyArray, "Target"));

        // 2. Found / Not Found Test
        String[] sortedLocations = {"Agbogbloshie", "Cantonments", "Makola", "Osu", "Ridge"};
        System.out.println("Found Target 'Makola' (Expected 2): " + BinarySearch.search(sortedLocations, "Makola"));
        System.out.println("Not Found Target 'Madina' (Expected -1): " + BinarySearch.search(sortedLocations, "Madina"));

        // 3. Unsorted Counterexample Test
        String[] unsortedLocations = {"Ridge", "Agbogbloshie", "Osu"};
        System.out.print("Unsorted Precondition Test: ");
        try {
            BinarySearch.search(unsortedLocations, "Osu");
        } catch (IllegalStateException e) {
            System.out.println("Exception successfully caught -> " + e.getMessage());
        }
    }
}
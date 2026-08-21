# Issue #5 — Evidence

Deque, Heap & Binary Search implementations.

## 1. Heap Insertion & Extraction Order Trace

This trace demonstrates the internal array state of `BinaryHeap` during a sequence of operations, proving the heap property is maintained via `heapifyUp` and `heapifyDown` for priority dispatch.

**Action Sequence:** `Insert(25)`, `Insert(5)`, `Insert(30)`, `Insert(12)`, `ExtractMin()`

**Insert 25**:
    Array State: `[25]`
**Insert 5**:
    Added to end: `[25, 5]`
    *`heapifyUp(1)` swaps 5 and 25.
    *Array State: `[5, 25]`
**Insert 30**:
    Added to end: `[5, 25, 30]`. No swap needed (30 > 5).
    Array State: `[5, 25, 30]`
**Insert 12**:
    Added to end: `[5, 25, 30, 12]`.
    `heapifyUp(3)` swaps 12 with parent (25).
    Array State: `[5, 12, 30, 25]`
**ExtractMin()**:
    Returns root (`5`).
    Move last element (`25`) to root: `[25, 12, 30, null]`.
    `heapifyDown(0)` swaps 25 with smallest child (`12`).
    Final Array State: `[12, 25, 30]`

## 2. Binary Search Counterexample Write-up

Binary search achieves O(log n) efficiency by relying on a strict invariant: for any given index `mid`, all elements to its left are smaller or equal, and all elements to its right are larger. When a target is less than `array[mid]`, the algorithm discards the right half of the array.

If the input array is unsorted (e.g., `["Ridge", "Agbogbloshie", "Osu"]`), this foundational assumption is violated. If we search for "Ridge" and the algorithm lands on "Agbogbloshie" as the midpoint, it evaluates that "Ridge" > "Agbogbloshie" alphabetically. It will therefore discard the left half of the array and search the right half, completely missing "Ridge" which is located at index 0. Because of this, an explicit `isSorted()` precondition check is required before execution to prevent the algorithm from returning a false `-1` (not found).

# Role 5: Deque, Heap & Binary Search Evidence

1. Heap Insertion & Extraction Order Trace

The following trace demonstrates the internal array state of the `BinaryHeap` during a sequence of insertions and extractions, proving the heap property (parent ≤ children) is maintained via `heapifyUp` and `heapifyDown`.

Action Sequence: `Insert(25)`, `Insert(5)`, `Insert(30)`, `Insert(12)`, `ExtractMin()`

Insert 25:
    Array State: `[25]`
Insert 5:
    Added to end: `[25, 5]`
    heapifyUp(1)` swaps 5 and 25.
    Array State: `[5, 25]`
Insert 30:
    Added to end: `[5, 25, 30]`. No swap needed (30>5).
    Array State:`[5, 25, 30]`
Insert 12:
    Added to end: `[5, 25, 30, 12]`.
    `heapifyUp(3)` swaps 12 with parent (25).
    Array State: `[5, 12, 30, 25]`
ExtractMin():
    Returns root (`5`).
    Move last element (`25`) to root:`[25, 12, 30,null]`.
    `heapifyDown(0)`swaps 25 with smallest child (`12`).
    Final Array State:`[12, 25, 30]`

2.Binary Search Counterexample Write-up
Binary search achieves O(log n) efficiency by relying on a strict invariant: for any given index `mid`, all elements to its left are smaller or equal, and all elements to its right are larger. When a target is less than `array[mid]`, the algorithm discards the right half of the array.

If the input array is unsorted (e.g., `["Cherry", "Apple", "Banana"]`), this foundational assumption is violated. If we search for "Apple", the algorithm checks the midpoint ("Apple"). If we were searching for "Banana", the algorithm might incorrectly evaluate the alphabetical constraints of the unsorted data, discarding the partition where the target actually resides. Because of this, an explicit `isSorted()` precondition check is required before execution to prevent the algorithm from returning a false `-1` (not found).

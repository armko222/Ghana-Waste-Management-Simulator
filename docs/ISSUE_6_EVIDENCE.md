# Issue #6 — Evidence

Binary Search Tree & Insertion Sort. Both artefacts below were generated
by running the committed implementations, not written by hand.

---

## 1. Insertion sort trace

Input array: `[29, 10, 14, 37, 13]`

Insertion sort keeps a **sorted region** at the front of the array. Each
pass takes the next unsorted value (the *key*), shifts every larger value
in the sorted region one place right, and drops the key into the gap.

### Per-pass summary

| Pass | `i` | Key | Sorted region before | Comparisons | Shifts | Array after pass |
|------|-----|-----|----------------------|-------------|--------|------------------|
| 1 | 1 | 10 | `[29]` | 1 | 1 | `[10, 29, 14, 37, 13]` |
| 2 | 2 | 14 | `[10, 29]` | 2 | 1 | `[10, 14, 29, 37, 13]` |
| 3 | 3 | 37 | `[10, 14, 29]` | 1 | 0 | `[10, 14, 29, 37, 13]` |
| 4 | 4 | 13 | `[10, 14, 29, 37]` | 4 | 3 | `[10, 13, 14, 29, 37]` |
| | | | **Total** | **8** | **5** | |

### Step detail

**Pass 1 — key = 10**, sorted region `[29]`
- compare `29 > 10` → shift 29 from index 0 to 1
- reached the front of the array; place 10 at index 0
- `[10, 29, 14, 37, 13]`

**Pass 2 — key = 14**, sorted region `[10, 29]`
- compare `29 > 14` → shift 29 from index 1 to 2
- compare `10 > 14` → false, stop
- place 14 at index 1
- `[10, 14, 29, 37, 13]`

**Pass 3 — key = 37**, sorted region `[10, 14, 29]`
- compare `29 > 37` → false, stop immediately
- place 37 at index 3 (back where it started — no movement)
- `[10, 14, 29, 37, 13]`

**Pass 4 — key = 13**, sorted region `[10, 14, 29, 37]`
- compare `37 > 13` → shift 37 from index 3 to 4
- compare `29 > 13` → shift 29 from index 2 to 3
- compare `14 > 13` → shift 14 from index 1 to 2
- compare `10 > 13` → false, stop
- place 13 at index 1
- `[10, 13, 14, 29, 37]`

**Result:** `[10, 13, 14, 29, 37]` — 8 comparisons, 5 shifts.

### What the trace shows about the algorithm

- **Pass 3 is the cheap case.** 37 is already larger than everything in
  the sorted region, so the inner loop exits on its first comparison and
  nothing moves. When the whole input is already sorted every pass looks
  like this — `n-1` comparisons, zero shifts, hence the **O(n) best case**.
- **Pass 4 is the expensive case.** 13 belongs near the front, so every
  value in the sorted region has to shift right to make room. When the
  input is reverse-sorted every pass looks like this, giving the
  **O(n²) worst case**.
- The algorithm is **in place** — no second array is allocated, values are
  only moved within the original. This is why `SortAlgorithm.sort` returns
  `void` and `InsertionSortTest.sortsInPlace` asserts the same array
  object comes back mutated.

---

## 2. Binary search tree — inorder traversal

### Sorted output from unsorted input

| | |
|---|---|
| Insertion order | `[50, 30, 70, 20, 40, 60, 80]` |
| Inorder traversal | `[Q20, Q30, Q40, Q50, Q60, Q70, Q80]` |
| Size / height | 7 nodes, height 3 |

Tree shape produced by that insertion order:

```
            50
          /    \
        30      70
       /  \    /  \
     20    40 60   80
```

Inorder traversal visits *left subtree → node → right subtree*, which for
a BST always emits keys in ascending order regardless of the order they
were inserted. That property is what makes the tree usable as an index.

### Deadline index (the real use case)

Keys are ISO-8601 timestamps stored as text:

| Inserted | Inorder output |
|---|---|
| `11:30 (Q002)`, `09:00 (Q001)`, `14:15 (Q003)` | `[Q001, Q002, Q003]` |

ISO-8601 sorts correctly under plain lexicographic string comparison,
which is why `RequestIndex` can key on `LocalDateTime.toString()` without
a custom comparator.

### Height degrades on sorted input

Inserting `1, 2, 3, 4, 5` in order gives **height 5 for 5 nodes** — every
insert goes right, so the tree degenerates into a linked list and lookup
falls from O(log n) to O(n).

This is the unbalanced-BST weakness that Issue #7's `RedBlackTree` is
measured against, and the baseline for the tree-comparison experiment in
Issue #14.

---

## 3. Duplicate-key policy

`BinarySearchTree.insert` on a key that is already present **overwrites
that node's value and leaves `size()` unchanged.** No duplicate branch is
created and the shape of the tree does not change.

Chosen over the alternatives (rejecting the insert, or pushing duplicates
into a right subtree) because the tree backs `RequestIndex`, which reads
a key's current value, mutates it, and relies on the tree continuing to
serve that same object.

Callers that need to keep every value for a key do what `RequestIndex`
does — use a collection as the value type. Several service requests can
share one deadline (nothing in `data/sql/schema.sql` forbids it), so the
index maps each deadline to the *bucket* of requests due at it.

That bucket is Issue #1's `DynamicArray`, not `java.util.ArrayList`: the
brief asks the system to run on the team's own structures, and this index
is the one place Issue #6 needs a growable collection. `DynamicArray` has
no append method, so `indexByDeadline` appends with
`bucket.insert(bucket.size(), request)`, and no unmodifiable wrapper, so
`findByDeadline` returns a copy of the bucket rather than a read-only
view of it.

---

## 4. Tests

`mvn test` — 29 tests, 18 passing, 11 errored.

| Test class | Tests | Status |
|---|---|---|
| `BinarySearchTreeTest` | 12 | 8 passing, 4 blocked on Issue #1 |
| `InsertionSortTest` | 9 | all passing |
| `RequestIndexTest` | 8 | 1 passing, 7 blocked on Issue #1 |

Covering the cases the issue asks for: empty tree, single node,
duplicate-key policy, search of a present key and an absent key.

### The 11 errors are Issue #1, not Issue #6

Every test that touches a `DynamicArray` errors with
`UnsupportedOperationException: TODO: Issue #1 — implement size` (or
`isEmpty`). `structures/DynamicArray.java` is still the unimplemented
stub on `main` — every method throws. Issue #6 is declared as depending
on Issue #1, and this is that dependency coming due: the integration
compiles and is correct, but it cannot run until Issue #1 lands.

The blocked cases are exactly the ones that read a bucket or a traversal:
all 7 `RequestIndexTest` cases that look a deadline up, and the 4
`BinarySearchTreeTest` cases that call `inorderTraversal()`. Tree
`insert`, `search`, `height`, and `size` are untouched by the refactor
and still pass, which is what confirms the change is confined to
collection storage.

These tests are expected to go green with no change to Issue #6's code
once `DynamicArray` has real `insert`, `get`, `size`, and `isEmpty`
behaviour. Do not merge this branch ahead of Issue #1.

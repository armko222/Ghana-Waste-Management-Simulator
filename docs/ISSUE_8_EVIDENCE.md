# Issue #8 — Evidence

B-Tree, MergeSort, and ResourceIndex. All traces below were produced by
compiling and running the committed classes directly (a throwaway
`Demo.java` driving `BTree` through a forced-split insertion sequence,
plus a small reflection-based dumper to print real internal node state
before/after each insert) — not written by hand.

---

## 1. Node structure

Each `BTree` node holds up to `2t - 1` keys, where `t` is the tree's
minimum degree (constructor parameter, default `t = 3`). This is the
"index-page" idea the brief allows: a node behaves like a database index
page that holds several keys per block, rather than exactly one key the
way a binary tree node does — that is what keeps the tree shallow as it
grows, instead of tall and thin.

```
Node<K,V>
  keys:     [k0, k1, ..., ki]          (up to 2t-1 keys, kept sorted)
  values:   [v0, v1, ..., vi]          (parallel to keys)
  children: [c0, c1, ..., ci, ci+1]    (i+2 children if internal, empty if leaf)
```

For an internal node, `children[j]` holds all keys strictly between
`keys[j-1]` and `keys[j]` (with `children[0]` holding everything below
`keys[0]`, and the last child holding everything above the last key) —
the same ordering invariant a BST has, just with several keys and
several children per node instead of one and two.

---

## 2. Node-split walkthrough

Trace below uses `new BTree<Integer,String>(2)` — minimum degree `t=2`,
so a node overflows once it holds `2t-1+1 = 4` keys, and splits at that
point. This is the smallest legal minimum degree, chosen here purely to
force a split after only a few inserts; the default (`t=3`, used
everywhere else in the system) behaves identically, just with a slightly
higher threshold. Real node contents, captured via reflection on the
running tree — not written by hand:

| Step | Root/tree state after the insert |
|---|---|
| `insert(10)` | `[10]` (leaf) |
| `insert(20)` | `[10, 20]` (leaf) |
| `insert(30)` | `[10, 20, 30]` (leaf, now full: 2t-1=3 keys) |
| `insert(40)` | **split.** Root becomes internal `[20]`, with leaf children `[10]` and `[30, 40]` |
| `insert(50)` | `[20]` (internal) → children `[10]`, `[30, 40, 50]` (right child now full) |
| `insert(60)` | **split again.** Root `[20, 40]` (internal) → children `[10]`, `[30]`, `[50, 60]` |
| `insert(70)` | `[20, 40]` (internal) → children `[10]`, `[30]`, `[50, 60, 70]` |

Full before/after dump for the split at `insert(40)`:

```
### before insert(40) ###
node [10, 20, 30] [leaf]

### after insert(40) ###
node [20] [internal]
    node [10] [leaf]
    node [30, 40] [leaf]
```

What happened: the leaf `[10, 20, 30]` was full (3 keys, the max for
`t=2`). Rather than growing past capacity, `splitChild` moved its median
key (`20`, at index `t-1=1`) up into a brand-new root, and split the
remaining keys either side of the median into two new leaf children —
everything below `20` (`[10]`) stayed left, everything above (`[30]`,
then joined by the `40` being inserted) went right. This is the one
place the tree ever grows in height: only when the *root* is found to be
full on the way in, which is why `insert()` checks `isFull(root)` before
descending at all, rather than only noticing overflow deep in the tree
and having no parent to push a median key into.

The second split (`insert(60)`) repeats the same operation one level
down, on the now-full leaf `[50, ...]`'s sibling — confirming this isn't
special-cased for the root only.

---

## 3. Search trace

Continuing from the fully-built tree above (`[20, 40]` root, three leaf
children), searching several keys — real output, not hand-computed:

```
search(10) = R10   (found in the left leaf, one hop below root)
search(40) = R40   (found in the root itself — no descent needed)
search(70) = R70   (found in the right leaf, one hop below root)
search(999) = null (not present)
```

Walking `search(70)` by hand to show the algorithm: at the root
`[20, 40]`, scan left-to-right comparing `70` against each key — `70 >
20`, `70 > 40`, so `70` belongs past both keys, in `children[2]`
(the third child, `[50, 60, 70]`). That node is a leaf, so scan its keys
directly: `70` matches `keys[2]`, so return `values[2]` (`"R70"`).

`search(999)`: same descent logic, but `999` is greater than every key
in whichever leaf it lands in, so the leaf is reached, no match is
found, and — because it's a leaf — the search returns `null` instead of
trying to descend further (there's nowhere left to go).

---

## 4. Duplicate-key (upsert) policy

Inserting a key already present **overwrites its value in place** and
does **not** increase `size()` — the same "insert = upsert" convention
`BinarySearchTree` documents for consistency across the two required
search-tree structures. Verified directly, including the case where the
key being re-inserted is one that previously moved up during a split
(so it now lives in an internal node, not a leaf):

```
size after re-inserting key 5 with a new value: 1
search(5) = "second"   (was "first")
```

---

## 5. Merge sort trace table

Real output from `new MergeSort<Integer>().sort(...)` on
`[38, 27, 43, 3, 9, 82, 10]`:

```
before: [38, 27, 43, 3, 9, 82, 10]
after:  [3, 9, 10, 27, 38, 43, 82]
```

Decomposition (top-down, splitting at the midpoint each time) and the
merges that build the result back up:

| Step | Operation | Result |
|---|---|---|
| 1 | split `[38,27,43,3,9,82,10]` at mid | `[38,27,43]` \| `[3,9,82,10]` |
| 2 | split `[38,27,43]` at mid | `[38]` \| `[27,43]` |
| 3 | split `[27,43]` at mid | `[27]` \| `[43]` |
| 4 | merge `[27]`, `[43]` | `[27,43]` |
| 5 | merge `[38]`, `[27,43]` | `[27,38,43]` |
| 6 | split `[3,9,82,10]` at mid | `[3,9]` \| `[82,10]` |
| 7 | split `[3,9]` at mid | `[3]` \| `[9]` |
| 8 | merge `[3]`, `[9]` | `[3,9]` |
| 9 | split `[82,10]` at mid | `[82]` \| `[10]` |
| 10 | merge `[82]`, `[10]` | `[10,82]` |
| 11 | merge `[3,9]`, `[10,82]` | `[3,9,10,82]` |
| 12 | merge `[27,38,43]`, `[3,9,10,82]` | `[3,9,10,27,38,43,82]` |

Each merge (step 4 onward) walks both sorted runs left-to-right,
comparing their current fronts and taking the smaller each time — the
`buffer[left].compareTo(buffer[right]) <= 0` check in `merge()` breaks
ties in favour of the left run, which is what makes the sort stable
(demonstrated directly in `MergeSortTest.Stability`).

---

## 6. Design decisions

**Auxiliary space, by design, not by accident.** `MergeSort` allocates
one buffer the size of the input array up front (`Arrays.copyOf`) and
reuses it across every merge call, rather than allocating a new array
per merge — that keeps the O(n) space to a single array, not O(n log n)
worth of short-lived arrays. This is the well-known trade-off against
`InsertionSort`/`SelectionSort` (O(1) space, O(n²) time) and `QuickSort`
(in-place partitioning, but worst-case O(n²) time): `MergeSort` spends
memory to *guarantee* O(n log n) regardless of input order, which the
sorting-comparison experiment (brief Section 9) is designed to surface
directly by timing all four side by side.

**B-tree upsert-on-duplicate**, matching `BinarySearchTree`'s documented
policy, so callers of either search structure don't need to remember
different behaviour for the two required tree types.

**`BTree` does not implement `SearchTreeADT`.** That interface is scoped
to the two tree types the brief specifically asks to be compared against
each other (`BinarySearchTree` vs `RedBlackTree`, per `SearchTreeADT`'s
own header comment and the Section 9 "BST vs balanced tree" experiment);
`BTree` isn't part of that comparison; it also doesn't have a natural
`height()` in the same sense a binary tree does, since "height" isn't a
particularly meaningful number for a wide, shallow B-tree the way it is
for a binary tree. `ResourceIndex`'s constructor accordingly takes a
concrete `BTree<String, Resource>`, not the shared interface — matching
how the stub was already shaped before this issue's implementation.

**Minimum degree is a constructor parameter, not a fixed constant.** The
default (`t=3`) is what the rest of the system uses; smaller values
(like `t=2` throughout this evidence file) exist purely to force splits
after only a handful of inserts, so the trace above stays short enough
to read by hand while still exercising the real splitting code path —
no test-only branch or special mode inside `BTree` itself.

---

## 7. Genuine use in the system

`ResourceIndex` wraps a `BTree<String, Resource>` keyed by `resourceId`,
so dispatch/reporting code can look a resource up by ID directly instead
of scanning the full resource list — the same "genuine use" pattern
`RequestIndex` follows for `BinarySearchTree`, per brief Section 6.
`index()` upserts by `resourceId` (re-indexing an existing ID replaces
the stored `Resource`); `findById()` returns `null` for an ID that was
never indexed, rather than throwing, so a "not found" lookup is a normal
result to check for, not an exceptional one.

---

## 8. Tests

`BTreeTest`, `MergeSortTest`, and `ResourceIndexTest` cover the cases the
issue asks for, plus the boundary/invalid-input cases the brief requires
for every custom structure (Section 8):

| Class | Nested group | Covers |
|---|---|---|
| `BTreeTest` | `EmptyTree` | size/isEmpty, search on empty, null-key and invalid-minDegree rejection |
| `BTreeTest` | `SingleKey` | insert-then-find, size, absent-key search |
| `BTreeTest` | `DuplicateKeys` | upsert, including upsert of a key that previously moved up during a split |
| `BTreeTest` | `NodeSplit` | forced single split remains fully searchable; repeated splits (50 keys); descending insertion order |
| `MergeSortTest` | `BoundaryCases` | null, empty, single-element arrays |
| `MergeSortTest` | `OrderingCases` | already-sorted, reverse-sorted, the traced example, duplicates, 500-element random input checked against `Arrays.sort` |
| `MergeSortTest` | `Stability` | equal-valued elements keep original relative order |
| `ResourceIndexTest` | `Construction` | null-tree rejection |
| `ResourceIndexTest` | `IndexingAndLookup` | index-then-find, unindexed ID, null resource/ID handling, overwrite-on-reindex, multiple independent entries |

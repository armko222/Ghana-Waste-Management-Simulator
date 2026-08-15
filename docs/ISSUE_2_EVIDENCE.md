# Issue #2 — Evidence

Doubly Linked List. The trace below was produced by compiling and running
the committed `DoublyLinkedList` directly (a throwaway `Demo.java` driving
it through `addFirst`, `addLast`, `insertAfter`, and `remove`, then walking
its iterator) — not written by hand.

---

## 1. Node structure

Each node holds a value and two links, to its predecessor and successor.
The list keeps `head` and `tail` pointers so both ends are O(1) to reach,
and a running `size` so `size()` doesn't need a traversal.

```
head                                    tail
 |                                        |
 v                                        v
+---+---+---+   +---+---+---+   +---+---+---+
| - | A | *-+-->| *-+ B |-*-+-->| *-+ C | - |
+---+---+---+   +---+---+---+   +---+---+---+
      ^   prev/next            prev/next   ^
      |________________________<___________|
```

`prev` on the head node and `next` on the tail node are both `null` —
that's how `remove()` and `insertAfter()` detect "this was the boundary of
the list" and know to update `head`/`tail` instead of a neighbour's link.

---

## 2. Operation trace

Starting from an empty list:

| Operation | Resulting list | size |
|---|---|---|
| `addLast(A)`, `addLast(B)`, `addLast(C)` | `[A, B, C]` | 3 |
| `addFirst(Z)` | `[Z, A, B, C]` | 4 |
| `insertAfter(B, B2)` | `[Z, A, B, B2, C]` | 5 |
| `remove(A)` | `[Z, B, B2, C]` | 4 |
| `remove(Z)` — removes the head | `[B, B2, C]` | 3 |
| `remove(ghost)` — absent value, no-op | `[B, B2, C]` | 3 |

Two things worth calling out:

- **`remove(Z)` removing the head** exercises the boundary case where
  `target.prev` is `null` — `head` has to be repointed at `target.next`
  directly, rather than through a neighbour's link.
- **`remove(ghost)`** confirms the documented "if present" contract:
  removing a value that was never in the list leaves size and contents
  unchanged rather than throwing.

---

## 3. Iterator demonstration

Continuing from the `[B, B2, C]` state above, walking the iterator by
hand (`hasNext()` / `next()`, exactly as the enhanced for-loop does under
the hood):

```
hasNext=true -> next() = B
hasNext=true -> next() = B2
hasNext=true -> next() = C
hasNext=false
next() on exhausted iterator throws NoSuchElementException
```

The iterator walks forward via each node's `next` link starting from
`head`, so it visits nodes in insertion order and reflects whatever the
list currently looks like — that's why `remove()` earlier in the trace
is already gone from this walk without any special-casing. Calling
`next()` past the end throws `NoSuchElementException`, matching the
standard `java.util.Iterator` contract, rather than returning `null` or
looping.

`DoublyLinkedListTest.Iteration.reflectsRemovalsOnFreshIterator` covers
this same behaviour as an assertion: build a list, iterate it, remove a
middle value, and confirm a *new* iterator skips the removed node.

---

## 4. Design decisions

**`insertAfter`/`remove` on a value that isn't in the list are silent
no-ops**, not exceptions. `LinkedListADT.remove` is documented as
"removes the first node matching value, *if present*" — `insertAfter`
doesn't say that explicitly, but is given the same treatment for
consistency, so callers don't have to remember that one of the two
"find by value" methods throws and the other doesn't. This matters for
Issue #9: if chaining is chosen for collision handling, probing for a key
that turns out not to be in a bucket's chain is a completely normal case,
not an error.

**Duplicate values**: `remove` and the `ref` lookup in `insertAfter` both
match the *first* occurrence only (via linear scan from `head`), so a
value that appears twice keeps its second occurrence untouched. Covered
by `MultipleNodes.removingOnlyFirstMatch`.

**No `java.util` collection is used internally.** Node linkage is plain
object references (`prev`/`next` fields); the iterator walks those links
directly rather than copying into an array or list first — unlike
`BinarySearchTree.inorderTraversal`, this structure never needs an
auxiliary collection at all, since forward iteration is exactly what the
`next` pointers already give it.

---

## 5. Genuine use in the system

Per the issue and `LinkedListADT`'s header comment, the intended real use
is collision chaining in Issue #9's `CustomHashTable`, *if* chaining is
the collision strategy that role chooses over open addressing — that
decision belongs to Issue #9's owner and isn't implemented yet (`main`
still has `CustomHashTable` and `DynamicArray` as unimplemented stubs
from Issue #1). `DoublyLinkedList` implements `LinkedListADT` exactly as
specified — `addFirst`, `addLast`, `insertAfter`, `remove`, `size`,
`isEmpty`, `iterator()` — so once that choice is confirmed, dropping it
in as a bucket's chain is a one-line change on Issue #9's side, per the
interface-first workflow in `Team_Handbook.docx` section 8.

---

## 6. Tests

`mvn test` — 20 tests, 0 failures, in `DoublyLinkedListTest`.

| Nested group | Tests |
|---|---|
| `EmptyList` | 3 |
| `SingleNode` | 3 |
| `MultipleNodes` | 10 |
| `Iteration` | 4 |

Covering the cases the issue asks for: empty list, single node, multiple
nodes, removal of both existing and non-existent values, and iteration in
insertion order including after a removal.

Issue #3 Evidence - ArrayStack and Audit Log Persistence

Note: this checkout contains `ArrayStack.java` as the fixed-capacity stack implementation. I did not find a separate `FixedStack.java` or `FixedStackTest.java`, so the evidence below refers to the implemented array-backed stack.

## 1. Structure Design

`ArrayStack<T>` is a fixed-capacity, array-backed stack implemented from scratch in `src/main/java/com/dcit308/wasteops/structures/ArrayStack.java`. It stores elements in an `Object[]`, tracks the top element with an integer index, and starts empty with `top = -1`.

Core behavior:

- `push(T data)` writes to `elements[++top]`.
- `pop()` removes `elements[top--]`.
- `peek()` returns `elements[top]` without mutation.
- `isEmpty()` checks `top == -1`.
- `isFull()` checks `top == capacity - 1`.
- `getRecentActions()` walks from `top` down to `0`, so the newest audit event is inspected first.

All stack operations are $O(1)$ except `getRecentActions()`, which is $O(n)$ because it traverses the stored audit trail. Boundary checks are explicit:

- overflow on `push()` throws `IllegalStateException("Stack overflow")`
- underflow on `pop()` throws `IllegalStateException("Stack is underflow")`
- empty `peek()` throws `IllegalStateException("Stack is empty")`

## 2. Operation Trace

Example trace for a stack with capacity 3:

| Step | Operation | Resulting stack state (bottom -> top) | Size | Outcome |
| --- | --- | --- | --- | --- |
| 1 | `push(E1)` | `[E1]` | 1 | Pushed |
| 2 | `push(E2)` | `[E1, E2]` | 2 | Pushed |
| 3 | `push(E3)` | `[E1, E2, E3]` | 3 | Pushed, stack is full |
| 4 | `peek()` | `[E1, E2, E3]` | 3 | Returns `E3` |
| 5 | `push(E4)` | `[E1, E2, E3]` | 3 | Throws `IllegalStateException("Stack overflow")` |
| 6 | `pop()` | `[E1, E2]` | 2 | Returns `E3` |
| 7 | `pop()` | `[E1]` | 1 | Returns `E2` |
| 8 | `pop()` | `[]` | 0 | Returns `E1` |
| 9 | `pop()` | `[]` | 0 | Throws `IllegalStateException("Stack is underflow")` |

Console output from `getRecentActions()` while the stack contains `E1`, `E2`, and `E3`:

```text
getRecentActions()
-> [DISPATCH] E3: truck assigned to request REQ-101
-> [STATUS_CHANGE] E2: request REQ-101 moved to IN_TRANSIT
-> [IMPORT] E1: initial audit seed loaded
```

## 3. Database Persistence & Audit Log Service

`AuditEvent` in `src/main/java/com/dcit308/wasteops/domain/AuditEvent.java` is the domain record for audit logging. It stores:

- `eventId`
- `eventType`
- `relatedRequestId` as a nullable field
- `description`
- `timestamp` as `LocalDateTime`

`AuditEventRepository` in `src/main/java/com/dcit308/wasteops/db/AuditEventRepository.java` persists and reloads these records using JDBC:

- `save()` uses a `PreparedStatement` for `INSERT`.
- nullable `relatedRequestId` is handled with `stmt.setNull(3, Types.VARCHAR)` when needed.
- timestamps are written with `Timestamp.valueOf(item.getTimestamp())`.
- `findById()` and `findAll()` map JDBC rows back to `AuditEvent` objects.
- `findAll()` returns newest records first via `ORDER BY timestamp DESC`.

The repository test uses an in-memory SQLite database and creates the `auditevent` table for isolation, then verifies insert, lookup, null handling, and ordering.

## 4. Design Decisions

Why a custom array-backed stack instead of `java.util.Stack` or a built-in collection:

- The course brief requires custom data structures in `structures/`.
- A fixed-capacity array makes overflow and underflow explicit and testable.
- Array storage gives predictable $O(1)$ push/pop/peek behavior.
- `getRecentActions()` naturally supports top-down audit inspection without mutating the log.
- Using `java.util.Stack` would violate the assignment rule and hide the capacity discipline needed for evidence.

## 5. Genuine Use in the System

Issue #3 is the audit trail layer for operational work:

- Role 3 owns the audit log service and persistence layer.
- Role 13, dispatch, is the main producer of meaningful operational events such as assigning a truck, changing a request status, or recording import activity.
- Each meaningful action should create an `AuditEvent`, push it onto the stack for immediate in-memory review, and persist it through `AuditEventRepository`.
- The stack supports a recent-actions view, while the repository provides durable history for later review, reporting, and debugging.

The current codebase contains the stack, the domain object, and the repository. `DispatchService` and `MenuHandlers` are the integration points where dispatch operations will emit those events.

## 6. Tests

Coverage currently includes:

- `ArrayStackTest`
  - push/pop preserves LIFO order
  - `peek()` returns the newest item without removing it
  - overflow on a full stack throws `IllegalStateException("Stack overflow")`
  - underflow on empty `pop()` and `peek()` throws `IllegalStateException`

- `AuditEventRepositoryTest`
  - save and retrieve by ID
  - null `relatedRequestId` is stored and read back correctly
  - `findAll()` returns events in descending timestamp order

The repository tests run against an in-memory SQLite connection, so they do not depend on external state.

# Ghana Waste Management Operations Optimizer

DCIT 204/308 Joint Semester Project — University of Ghana.

Read `Team_Handbook.docx` first if you haven't already — it explains what
this project is, how the system works, and what your specific role
covers. This file only covers getting set up and working day to day.

## Requirements

- Java 17 or newer
- Maven
- Git

No database software to install, no Docker, no extra setup — the
database is a single file, created automatically the first time the
program runs.

## Getting started

```bash
git clone <repository-url>
cd <repository-folder>
mvn clean install
```

If this succeeds, everything is working. If it doesn't, check that
`java -version` reports 17 or higher and `mvn -version` runs at all
before asking for help — that covers most first-time issues.

## Running the project

```bash
mvn exec:java
```

The first time this runs, it creates a local database file automatically
using `data/sql/schema.sql` — nothing needs to be installed or configured
beforehand. From the menu, choose the option to import data, which loads
the CSV files in `data/csv/` into the database.

## Running the tests

```bash
mvn test
```

Run this before opening a pull request. If it fails, fix it before
asking for review.

## Finding your task

Every task is a GitHub Issue in this repository, numbered 1–14. Find the
one with your name on it — it lists exactly what to build, what to test,
and what "done" looks like. If anything in your issue is unclear, ask in
the issue itself rather than guessing.

## Branching

One branch per issue, created from the latest `main`:

```
feature/<issue-number>-short-description
```

Examples: `feature/5-heap-priority-queue`, `feature/12-dijkstra-routing`.

## Commits

Keep commits small and describe what changed:

```
git commit -m "feat: implement heap insert and extractMin"
```

## Opening a pull request

1. Push your branch.
2. Open a pull request against `main`.
3. In the description, note which issue it closes (`Closes #5`).
4. Make sure `mvn test` passes before requesting review.
5. Get it reviewed and approved by at least one other member before
   merging. Do not merge your own work without review, and do not push
   directly to `main`.

## If your work depends on someone else's

Don't wait. See the "Working With Each Other's Code" section in the
handbook — there's an agreed, documented way to start real, testable work
immediately even if the piece you depend on isn't finished yet.

## Where things live

```
data/csv/          starter dataset
data/sql/          database schema
src/main/java/     application code — see your issue for your exact file
src/test/java/     tests — mirrors the structure above
results/           performance experiment output (generated, not committed)
report/            final written report
```

## Questions

If something is unclear after reading your issue and the handbook, raise
it — don't guess and build around an assumption. It's much easier to
sort out early than after several people have built on top of it.

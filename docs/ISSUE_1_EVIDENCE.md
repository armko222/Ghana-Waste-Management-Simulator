# Issue #1 Evidence — Domain Model, Database & CSV Foundation

## Dynamic Array Resize Trace

`DynamicArray<String>` starts with a default capacity of 10.

The backing `Object[]` grows as follows when elements are appended:

| Operation | Size Before | Capacity Before | Action | Capacity After |
|-----------|-------------|-----------------|--------|----------------|
| new DynamicArray<>() | 0 | 10 | — | 10 |
| insert(0, "A") | 0 | 10 | no resize | 10 |
| … insert up to index 9 … | 9 | 10 | no resize | 10 |
| insert(10, "K") | 10 | 10 | resize to 20 | 20 |
| … insert up to index 19 … | 19 | 20 | no resize | 20 |
| insert(20, "U") | 20 | 20 | resize to 40 | 40 |

Each resize doubles the current capacity and copies all existing elements into the new array.

**Why doubling:** Doubling gives amortised O(1) insertion and limits the number of expensive array copies. For the datasets in this project (50 locations, 100 roads, 300 requests, 30 resources) the array never exceeds a few hundred elements, so memory overhead is negligible.

## Dataset Construction Note

The four CSV template files were constructed as follows:

- **locations** — 50 locations covering Legon campus and surrounding Accra neighbourhoods (Achimota, East Legon, Spintex, Airport, Osu, Labone, Cantonments, Ridge, Kaneshie, Madina, Adenta, Tema, Nungua). Each has a unique `location_id` (L001–L050), a name, an area/neighbourhood, a `location_type`, and approximate GPS coordinates.

- **roads** — 100 directed roads connecting the locations. Distances and travel times are realistic for the Accra road network. `condition_weight` ranges from 1.0 to 1.3 to reflect varying road quality.

- **resources** — 30 resources (10 GENERAL, 8 HAZARDOUS, 12 INDUSTRIAL) with capacities from 500 to 1600. Every resource shares a single home location (`L001` — Balme Library, Legon) so that dispatch distances are consistent and the greedy/knapsack optimisers have a common baseline.

- **service_requests** — 300 requests across five categories (Medical, Document, Hazardous, Industrial, General) spread over two simulated days (2026-07-01 and 2026-07-02).
  - **Hazardous requests** are seeded with **High** priority.
  - **Industrial requests** are seeded with **Medium** priority.
  - All other categories use urgency-derived priority (urgency 1–2 → Low, 3 → Medium, 4–5 → High).

The dataset meets all minimums stated in the brief and the seeding rules from the issue description.

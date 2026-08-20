-- Ghana Waste Management Operations Optimizer
-- Schema for SQLite (see docs/DECISION_LOG.md, Decision D5, for engine choice)
-- Matches docs/DATA_DICTIONARY.md field-for-field.

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS locations (
    location_id     TEXT PRIMARY KEY,
    name            TEXT NOT NULL,
    area            TEXT NOT NULL,
    location_type   TEXT NOT NULL,
    x_coord         REAL NOT NULL,
    y_coord         REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS roads (
    road_id           TEXT PRIMARY KEY,
    from_location_id  TEXT NOT NULL REFERENCES locations(location_id),
    to_location_id    TEXT NOT NULL REFERENCES locations(location_id),
    distance_km       REAL NOT NULL,
    travel_time_min   REAL NOT NULL,
    condition_weight  REAL NOT NULL DEFAULT 1.0
);

CREATE TABLE IF NOT EXISTS resources (
    resource_id         TEXT PRIMARY KEY,
    resource_type       TEXT NOT NULL,       -- General | Hazardous | Industrial
    home_location_id    TEXT NOT NULL REFERENCES locations(location_id),
    capacity            INTEGER NOT NULL,
    availability_status TEXT NOT NULL DEFAULT 'AVAILABLE'
        CHECK (availability_status IN ('AVAILABLE','IN_TRANSIT','COLLECTING','RETURNING'))
);

CREATE TABLE IF NOT EXISTS service_requests (
    request_id             TEXT PRIMARY KEY,
    source_location_id     TEXT NOT NULL REFERENCES locations(location_id),
    destination_location_id TEXT NOT NULL REFERENCES locations(location_id),
    category                TEXT NOT NULL,
    urgency                 INTEGER NOT NULL CHECK (urgency BETWEEN 1 AND 5),
    priority                TEXT NOT NULL CHECK (priority IN ('High','Medium','Low')),
    time_submitted           TEXT NOT NULL,   -- ISO 8601
    deadline                 TEXT NOT NULL,   -- ISO 8601
    status                   TEXT NOT NULL DEFAULT 'NEW'
        CHECK (status IN ('NEW','ASSIGNED','IN_TRANSIT','COMPLETED')),
    assigned_resource_id     TEXT REFERENCES resources(resource_id)
);

CREATE TABLE IF NOT EXISTS algorithm_runs (
    run_id          TEXT PRIMARY KEY,
    algorithm_name  TEXT NOT NULL,
    input_size      INTEGER NOT NULL,
    time_ns         INTEGER NOT NULL,
    memory_kb       INTEGER,
    date_run        TEXT NOT NULL            -- ISO 8601
);

CREATE TABLE IF NOT EXISTS audit_events (
    event_id            TEXT PRIMARY KEY,
    event_type          TEXT NOT NULL,       -- DISPATCH | STATUS_CHANGE | IMPORT | ...
    related_request_id  TEXT REFERENCES service_requests(request_id),
    description          TEXT,
    timestamp             TEXT NOT NULL      -- ISO 8601
);

-- Useful indexes for the console's search/filter operations.
CREATE INDEX IF NOT EXISTS idx_requests_status   ON service_requests(status);
CREATE INDEX IF NOT EXISTS idx_requests_priority ON service_requests(priority);
CREATE INDEX IF NOT EXISTS idx_roads_from        ON roads(from_location_id);
CREATE INDEX IF NOT EXISTS idx_roads_to          ON roads(to_location_id);

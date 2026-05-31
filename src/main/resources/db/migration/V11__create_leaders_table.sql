-- Leaders: historical leaders/commanders tied to a nation (monarchs, generals, heads of state).
-- A nation has many leaders; a leader belongs to exactly one nation (multi-nation careers
-- are simplified to the leader's primary nation, per PHASE_3_DESIGN.md §3.2).
CREATE TABLE leaders (
    id          BIGSERIAL    PRIMARY KEY,
    nation_id   BIGINT       NOT NULL REFERENCES nations(id),
    name        VARCHAR(255) NOT NULL,
    role        VARCHAR(255) NOT NULL,
    title       VARCHAR(255),
    birth_year  INTEGER,
    death_year  INTEGER,
    description VARCHAR(2000)
);

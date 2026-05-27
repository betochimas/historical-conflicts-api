-- Theaters: a geographic/operational region of a conflict that groups battles.
CREATE TABLE theaters (
    id          BIGSERIAL    PRIMARY KEY,
    conflict_id BIGINT       NOT NULL REFERENCES conflicts(id),
    name        VARCHAR(255) NOT NULL,
    region      VARCHAR(255),
    start_date  DATE,
    end_date    DATE,
    outcome     VARCHAR(255),
    description VARCHAR(2000)
);

-- A battle is optionally fought within one theater of its conflict.
-- Nullable: battles may belong to a conflict with no theater assigned yet.
-- No ON DELETE clause = RESTRICT: a theater with battles can't be deleted (surfaced as 409).
ALTER TABLE battles ADD COLUMN theater_id BIGINT REFERENCES theaters(id);

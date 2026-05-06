CREATE TABLE conflict_participants (
    id                BIGSERIAL    PRIMARY KEY,
    conflict_id       BIGINT       NOT NULL REFERENCES conflicts(id),
    nation_id         BIGINT       NOT NULL REFERENCES nations(id),
    role              VARCHAR(255) NOT NULL,
    troops_committed  INTEGER,
    casualties        INTEGER,
    outcome           VARCHAR(255),
    CONSTRAINT uq_conflict_nation UNIQUE (conflict_id, nation_id)
);
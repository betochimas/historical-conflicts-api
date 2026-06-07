-- Treaties: typically conclude a conflict. conflict_id is NULLABLE (the lone nullable FK) — some
-- treaties aren't tied to a specific conflict in our data, and a conflict can have several treaties
-- (an armistice and a peace treaty). Signatories are a first-class join entity (treaty_signatories),
-- mirroring conflict_participants, with a UNIQUE (treaty_id, nation_id) constraint.
CREATE TABLE treaties (
    id          BIGSERIAL    PRIMARY KEY,
    conflict_id BIGINT       REFERENCES conflicts(id),
    name        VARCHAR(255) NOT NULL,
    treaty_type VARCHAR(255) NOT NULL,
    signed_date DATE,
    location    VARCHAR(255),
    description  VARCHAR(2000)
);

CREATE TABLE treaty_signatories (
    id            BIGSERIAL    PRIMARY KEY,
    treaty_id     BIGINT       NOT NULL REFERENCES treaties(id),
    nation_id     BIGINT       NOT NULL REFERENCES nations(id),
    role          VARCHAR(255) NOT NULL,
    ratified_date DATE,
    CONSTRAINT uq_treaty_nation UNIQUE (treaty_id, nation_id)
);

-- Alliances: standing alliances/coalitions of nations (Triple Entente, Central Powers, ...).
-- Membership is modelled as a first-class join entity (alliance_members), mirroring
-- conflict_participants, so a membership can carry its own attributes (joined/left dates) and
-- has a UNIQUE (alliance_id, nation_id) constraint.
CREATE TABLE alliances (
    id             BIGSERIAL    PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    alliance_type  VARCHAR(255) NOT NULL,
    formed_date    DATE,
    dissolved_date DATE,
    description    VARCHAR(2000)
);

CREATE TABLE alliance_members (
    id          BIGSERIAL PRIMARY KEY,
    alliance_id BIGINT    NOT NULL REFERENCES alliances(id),
    nation_id   BIGINT    NOT NULL REFERENCES nations(id),
    joined_date DATE,
    left_date   DATE,
    CONSTRAINT uq_alliance_nation UNIQUE (alliance_id, nation_id)
);

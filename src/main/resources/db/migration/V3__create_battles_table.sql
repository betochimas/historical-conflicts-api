CREATE TABLE battles (
    id          BIGSERIAL    PRIMARY KEY,
    conflict_id BIGINT       NOT NULL REFERENCES conflicts(id),
    name        VARCHAR(255) NOT NULL,
    date        DATE,
    location    VARCHAR(255),
    terrain     VARCHAR(255),
    outcome     VARCHAR(255),
    description VARCHAR(2000)
);
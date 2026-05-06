CREATE TABLE conflicts (
    id            BIGSERIAL    PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    conflict_type VARCHAR(255),
    start_date    DATE,
    end_date      DATE,
    outcome       VARCHAR(255),
    description   VARCHAR(2000)
);
CREATE TABLE nations (
    id              BIGSERIAL   PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    region          VARCHAR(255) NOT NULL,
    government_type VARCHAR(255),
    founded_year    INTEGER,
    dissolved_year  INTEGER,
    description     VARCHAR(2000)
)
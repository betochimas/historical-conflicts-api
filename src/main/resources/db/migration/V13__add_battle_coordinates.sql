-- Battle coordinates: optional point geometry for map rendering (Interactive Map feature, B1).
-- Nullable — not every battle has/needs a pin; the frontend skips nulls.
ALTER TABLE battles
    ADD COLUMN latitude  DOUBLE PRECISION,
    ADD COLUMN longitude DOUBLE PRECISION;

-- Backfill the seeded WWI battles (V6) with coordinates (Wikidata P625, rounded).
UPDATE battles b SET latitude = 48.96, longitude = 3.39
FROM conflicts c WHERE b.conflict_id = c.id
  AND c.name = 'World War I' AND b.name = 'First Battle of the Marne';

UPDATE battles b SET latitude = 53.50, longitude = 20.13
FROM conflicts c WHERE b.conflict_id = c.id
  AND c.name = 'World War I' AND b.name = 'Battle of Tannenberg';

UPDATE battles b SET latitude = 49.21, longitude = 5.42
FROM conflicts c WHERE b.conflict_id = c.id
  AND c.name = 'World War I' AND b.name = 'Battle of Verdun';

UPDATE battles b SET latitude = 50.00, longitude = 2.68
FROM conflicts c WHERE b.conflict_id = c.id
  AND c.name = 'World War I' AND b.name = 'Battle of the Somme';

UPDATE battles b SET latitude = 40.25, longitude = 26.28
FROM conflicts c WHERE b.conflict_id = c.id
  AND c.name = 'World War I' AND b.name = 'Gallipoli Campaign';

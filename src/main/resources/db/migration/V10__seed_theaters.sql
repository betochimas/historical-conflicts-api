-- ============================================================
-- Seed data: World War I theaters, and assignment of the V6 battles to them.
-- FKs resolved by name-subquery (no hardcoded IDs), per V6's convention.
-- Mirrored in db/demo/demo_reset.sql — keep the two in sync.
-- ============================================================

-- Theaters
INSERT INTO theaters (conflict_id, name, region, start_date, end_date, outcome, description)
SELECT id, 'Western Front', 'France & Belgium', '1914-08-04', '1918-11-11', 'Allied victory',
       'The decisive theater: a continuous line of trenches from the North Sea to the Swiss border, where the war was ultimately won and lost.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO theaters (conflict_id, name, region, start_date, end_date, outcome, description)
SELECT id, 'Eastern Front', 'Eastern Europe', '1914-08-17', '1918-03-03', 'Central Powers victory',
       'A war of movement across vast distances between the Central Powers and Russia, ending with Russia''s withdrawal at Brest-Litovsk.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO theaters (conflict_id, name, region, start_date, end_date, outcome, description)
SELECT id, 'Gallipoli & Middle East', 'Ottoman Empire', '1915-04-25', '1918-10-30', 'Mixed',
       'Allied operations against the Ottoman Empire, including the failed Dardanelles landings and later campaigns in Mesopotamia and Palestine.'
FROM conflicts WHERE name = 'World War I';

-- Assign the seeded battles to their theaters (scoped to the WWI conflict).
UPDATE battles SET theater_id = (
    SELECT t.id FROM theaters t JOIN conflicts c ON t.conflict_id = c.id
    WHERE t.name = 'Western Front' AND c.name = 'World War I')
WHERE name IN ('First Battle of the Marne', 'Battle of Verdun', 'Battle of the Somme');

UPDATE battles SET theater_id = (
    SELECT t.id FROM theaters t JOIN conflicts c ON t.conflict_id = c.id
    WHERE t.name = 'Eastern Front' AND c.name = 'World War I')
WHERE name = 'Battle of Tannenberg';

UPDATE battles SET theater_id = (
    SELECT t.id FROM theaters t JOIN conflicts c ON t.conflict_id = c.id
    WHERE t.name = 'Gallipoli & Middle East' AND c.name = 'World War I')
WHERE name = 'Gallipoli Campaign';

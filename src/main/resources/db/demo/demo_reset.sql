-- ============================================================
-- Demo data reset script (run nightly by DemoDataResetService when
-- app.demo.reset.enabled=true). Restores the domain tables to their
-- seeded state so visitor edits via the shared demo account don't
-- accumulate. The `users` table is intentionally NOT truncated, so the
-- seeded demo account survives.
--
-- The INSERT bodies below mirror V6__seed_data.sql. They're duplicated
-- here because Flyway migrations are immutable history and can't be
-- re-run; keep the two in sync if the seed ever changes.
-- ============================================================

TRUNCATE conflict_participants, battles, conflicts, nations RESTART IDENTITY CASCADE;

-- Nations
INSERT INTO nations (name, region, founded_year, dissolved_year, description) VALUES
    ('United Kingdom',  'Europe',               1707, NULL, 'Island nation and global empire; anchor of the Allied cause on the Western Front.'),
    ('France',          'Europe',               1792, NULL, 'Republic and colonial power; primary theatre of the Western Front was fought on French soil.'),
    ('German Empire',   'Europe',               1871, 1918, 'Dominant Central Power; launched the Schlieffen Plan to fight a two-front war.'),
    ('Russian Empire',  'Europe & Asia',        1721, 1917, 'Largest Allied Power by manpower; collapsed in revolution before the armistice.'),
    ('Austria-Hungary', 'Europe',               1867, 1918, 'Dual monarchy whose ultimatum to Serbia in July 1914 triggered the chain of declarations.'),
    ('Ottoman Empire',  'Middle East & Europe', 1299, 1922, 'Central Power controlling the Dardanelles; opened fronts in the Middle East and Caucasus.'),
    ('United States',   'North America',        1776, NULL, 'Entered the war in April 1917; fresh troops and industrial output tipped the balance for the Allies.');

-- Conflict
INSERT INTO conflicts (name, conflict_type, start_date, end_date, outcome, description) VALUES
    ('World War I', 'WAR', '1914-07-28', '1918-11-11', 'Allied victory',
     'Global conflict centred in Europe, sparked by the assassination of Archduke Franz Ferdinand in Sarajevo. Fought across Europe, the Middle East, Africa, and at sea; resulted in the collapse of four empires and over 20 million dead.');

-- Battles
INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, description)
SELECT id, 'First Battle of the Marne', '1914-09-05', 'Marne River, France',
       'River valley and open farmland',
       'Allied victory',
       'Halted the German advance on Paris and ended the war of movement on the Western Front, forcing both sides into the trenches.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, description)
SELECT id, 'Battle of Tannenberg', '1914-08-26', 'Tannenberg, East Prussia',
       'Forests and lake district',
       'German victory',
       'Decisive German encirclement and near-destruction of the Russian Second Army; cemented Hindenburg and Ludendorff as national heroes.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, description)
SELECT id, 'Battle of Verdun', '1916-02-21', 'Verdun, France',
       'Fortified hills and shell-cratered wasteland',
       'French victory',
       'Longest battle of the war; Germany''s strategy to bleed France white backfired after ten months of fighting and roughly 700,000 casualties on both sides.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, description)
SELECT id, 'Battle of the Somme', '1916-07-01', 'Somme, France',
       'Open fields and trench networks',
       'Inconclusive',
       'Allied offensive launched to relieve pressure on Verdun. Gained little ground at the cost of over one million casualties; the first day remains the bloodiest in British military history.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, description)
SELECT id, 'Gallipoli Campaign', '1915-04-25', 'Gallipoli Peninsula, Ottoman Empire',
       'Steep cliffs, narrow beaches, and dense scrubland',
       'Ottoman victory',
       'Allied attempt to open a sea route to Russia and knock the Ottomans out of the war. Poor planning and stiff resistance ended in a full Allied withdrawal after eight months.'
FROM conflicts WHERE name = 'World War I';

-- Conflict participants
INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'DEFENDER', 8900000, 702000, 'Victory'
FROM conflicts c, nations n
WHERE c.name = 'World War I' AND n.name = 'United Kingdom';

INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'DEFENDER', 8400000, 1397000, 'Victory'
FROM conflicts c, nations n
WHERE c.name = 'World War I' AND n.name = 'France';

INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'ATTACKER', 13500000, 2037000, 'Defeat'
FROM conflicts c, nations n
WHERE c.name = 'World War I' AND n.name = 'German Empire';

INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'ALLY', 12000000, 1700000, 'Defeat'
FROM conflicts c, nations n
WHERE c.name = 'World War I' AND n.name = 'Russian Empire';

INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'ATTACKER', 7800000, 1567000, 'Defeat'
FROM conflicts c, nations n
WHERE c.name = 'World War I' AND n.name = 'Austria-Hungary';

INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'COALITION', 2850000, 771000, 'Defeat'
FROM conflicts c, nations n
WHERE c.name = 'World War I' AND n.name = 'Ottoman Empire';

INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'ALLY', 4700000, 116516, 'Victory'
FROM conflicts c, nations n
WHERE c.name = 'World War I' AND n.name = 'United States';

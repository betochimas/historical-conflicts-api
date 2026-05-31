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

TRUNCATE conflict_participants, leaders, battles, theaters, conflicts, nations RESTART IDENTITY CASCADE;

-- Nations
INSERT INTO nations (name, region, founded_year, dissolved_year, description) VALUES
    ('United Kingdom',  'Europe',               1707, NULL, 'Island nation and global empire; anchor of the Allied cause on the Western Front.'),
    ('France',          'Europe',               1792, NULL, 'Republic and colonial power; primary theatre of the Western Front was fought on French soil.'),
    ('German Empire',   'Europe',               1871, 1918, 'Dominant Central Power; launched the Schlieffen Plan to fight a two-front war.'),
    ('Russian Empire',  'Europe & Asia',        1721, 1917, 'Largest Allied Power by manpower; collapsed in revolution before the armistice.'),
    ('Austria-Hungary', 'Europe',               1867, 1918, 'Dual monarchy whose ultimatum to Serbia in July 1914 triggered the chain of declarations.'),
    ('Ottoman Empire',  'Middle East & Europe', 1299, 1922, 'Central Power controlling the Dardanelles; opened fronts in the Middle East and Caucasus.'),
    ('United States',   'North America',        1776, NULL, 'Entered the war in April 1917; fresh troops and industrial output tipped the balance for the Allies.'),
    ('Empire of Japan', 'East Asia',            1868, 1947, 'Rapidly industrialised Meiji-era power; its victory in the Russo-Japanese War was the first modern defeat of a European great power by an Asian state.');

-- Conflict
INSERT INTO conflicts (name, conflict_type, start_date, end_date, outcome, description) VALUES
    ('World War I', 'WAR', '1914-07-28', '1918-11-11', 'Allied victory',
     'Global conflict centred in Europe, sparked by the assassination of Archduke Franz Ferdinand in Sarajevo. Fought across Europe, the Middle East, Africa, and at sea; resulted in the collapse of four empires and over 20 million dead.');

-- Battles (coordinates mirror the V13 backfill)
INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'First Battle of the Marne', '1914-09-05', 'Marne River, France',
       'River valley and open farmland',
       'Allied victory', 48.96, 3.39,
       'Halted the German advance on Paris and ended the war of movement on the Western Front, forcing both sides into the trenches.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Battle of Tannenberg', '1914-08-26', 'Tannenberg, East Prussia',
       'Forests and lake district',
       'German victory', 53.50, 20.13,
       'Decisive German encirclement and near-destruction of the Russian Second Army; cemented Hindenburg and Ludendorff as national heroes.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Battle of Verdun', '1916-02-21', 'Verdun, France',
       'Fortified hills and shell-cratered wasteland',
       'French victory', 49.21, 5.42,
       'Longest battle of the war; Germany''s strategy to bleed France white backfired after ten months of fighting and roughly 700,000 casualties on both sides.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Battle of the Somme', '1916-07-01', 'Somme, France',
       'Open fields and trench networks',
       'Inconclusive', 50.00, 2.68,
       'Allied offensive launched to relieve pressure on Verdun. Gained little ground at the cost of over one million casualties; the first day remains the bloodiest in British military history.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Gallipoli Campaign', '1915-04-25', 'Gallipoli Peninsula, Ottoman Empire',
       'Steep cliffs, narrow beaches, and dense scrubland',
       'Ottoman victory', 40.25, 26.28,
       'Allied attempt to open a sea route to Russia and knock the Ottomans out of the war. Poor planning and stiff resistance ended in a full Allied withdrawal after eight months.'
FROM conflicts WHERE name = 'World War I';

-- Theaters (mirrors V10__seed_theaters.sql)
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

-- Leaders (mirrors V12__seed_leaders.sql)
INSERT INTO leaders (nation_id, name, role, title, birth_year, death_year, description)
SELECT id, 'Woodrow Wilson', 'HEAD_OF_STATE', 'President of the United States', 1856, 1924,
       'Led the United States into the war in 1917 and championed the Fourteen Points and the League of Nations at Versailles.'
FROM nations WHERE name = 'United States';

INSERT INTO leaders (nation_id, name, role, title, birth_year, death_year, description)
SELECT id, 'Ferdinand Foch', 'GENERAL', 'Marshal of France', 1851, 1929,
       'Supreme Allied Commander on the Western Front from April 1918; coordinated the offensives that broke the Hindenburg Line.'
FROM nations WHERE name = 'France';

INSERT INTO leaders (nation_id, name, role, title, birth_year, death_year, description)
SELECT id, 'Douglas Haig', 'GENERAL', 'Field Marshal', 1861, 1928,
       'Commander of the British Expeditionary Force from 1915; presided over the Somme, Passchendaele, and the Hundred Days Offensive.'
FROM nations WHERE name = 'United Kingdom';

INSERT INTO leaders (nation_id, name, role, title, birth_year, death_year, description)
SELECT id, 'Paul von Hindenburg', 'GENERAL', 'Field Marshal', 1847, 1934,
       'Hero of Tannenberg; together with Ludendorff effectively ran the German war effort from 1916 onward.'
FROM nations WHERE name = 'German Empire';

INSERT INTO leaders (nation_id, name, role, title, birth_year, death_year, description)
SELECT id, 'Franz Joseph I', 'MONARCH', 'Emperor of Austria, King of Hungary', 1830, 1916,
       'Long-reigning Habsburg monarch whose ultimatum to Serbia in July 1914 helped trigger the war; died mid-conflict in 1916.'
FROM nations WHERE name = 'Austria-Hungary';

INSERT INTO leaders (nation_id, name, role, title, birth_year, death_year, description)
SELECT id, 'Nicholas II', 'MONARCH', 'Tsar of Russia', 1868, 1918,
       'Last Russian emperor; took personal command of the army in 1915 and was overthrown in the February Revolution of 1917.'
FROM nations WHERE name = 'Russian Empire';

INSERT INTO leaders (nation_id, name, role, title, birth_year, death_year, description)
SELECT id, 'Mehmed V', 'MONARCH', 'Sultan of the Ottoman Empire', 1844, 1918,
       'Reigning Sultan during WWI; largely a ceremonial figure as the Three Pashas led the empire into the war on the side of the Central Powers.'
FROM nations WHERE name = 'Ottoman Empire';

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

-- ============================================================
-- Russo-Japanese War (mirrors V14__seed_russo_japanese_war.sql).
-- The 'Empire of Japan' nation is seeded in the nations block above;
-- 'Russian Empire' is shared with the WWI seed.
-- ============================================================
INSERT INTO conflicts (name, conflict_type, start_date, end_date, outcome, description) VALUES
    ('Russo-Japanese War', 'WAR', '1904-02-08', '1905-09-05', 'Japanese victory',
     'Fought over rival imperial ambitions in Manchuria and Korea. Japan''s victories on land at Mukden and at sea at Tsushima forced Russia to terms in the U.S.-mediated Treaty of Portsmouth.');

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Battle of Port Arthur', '1904-02-09', 'Port Arthur (Lüshun), Manchuria',
       'Fortified naval anchorage', 'Inconclusive', 38.81, 121.26,
       'A surprise night torpedo attack opened the war, bottling up the Russian Pacific Squadron in its harbour.'
FROM conflicts WHERE name = 'Russo-Japanese War';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Battle of the Yalu River', '1904-05-01', 'Yalu River, Korea–Manchuria border',
       'River crossing and hills', 'Japanese victory', 40.10, 124.39,
       'The first major land clash; Japanese forces crossed the Yalu and drove the Russians back into Manchuria, shattering the myth of European invincibility.'
FROM conflicts WHERE name = 'Russo-Japanese War';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Siege of Port Arthur', '1904-08-01', 'Port Arthur (Lüshun), Liaodong Peninsula',
       'Fortified ridgelines and forts', 'Japanese victory', 38.85, 121.26,
       'A five-month siege of Russia''s fortified warm-water port. Massed infantry assaults on entrenched positions foreshadowed the trench warfare of WWI; the port fell in January 1905.'
FROM conflicts WHERE name = 'Russo-Japanese War';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Battle of the Yellow Sea', '1904-08-10', 'Yellow Sea, off the Liaodong Peninsula',
       'Open sea', 'Inconclusive', 38.20, 122.00,
       'A naval action in which the Russian Pacific Squadron failed to break out to Vladivostok and was forced back to Port Arthur, sealing its fate.'
FROM conflicts WHERE name = 'Russo-Japanese War';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Battle of Liaoyang', '1904-08-25', 'Liaoyang, Manchuria',
       'Plains and fortified hills', 'Japanese victory', 41.27, 123.17,
       'A large but indecisive land battle; the Russians withdrew in good order despite holding strong positions, ceding the initiative to Japan.'
FROM conflicts WHERE name = 'Russo-Japanese War';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Battle of Mukden', '1905-02-20', 'Mukden (Shenyang), Manchuria',
       'Open plains', 'Japanese victory', 41.80, 123.43,
       'The largest land battle in history to that point — roughly 600,000 men engaged over weeks. A costly Japanese victory that ended major land operations.'
FROM conflicts WHERE name = 'Russo-Japanese War';

INSERT INTO battles (conflict_id, name, date, location, terrain, outcome, latitude, longitude, description)
SELECT id, 'Battle of Tsushima', '1905-05-27', 'Tsushima Strait',
       'Open sea', 'Decisive Japanese victory', 34.58, 129.48,
       'Admiral Tōgō annihilated the Russian Baltic Fleet after its 18,000-mile voyage. One of the most decisive naval battles in history; it forced Russia to the negotiating table.'
FROM conflicts WHERE name = 'Russo-Japanese War';

INSERT INTO theaters (conflict_id, name, region, start_date, end_date, outcome, description)
SELECT id, 'Manchurian Front', 'Manchuria & Liaodong', '1904-04-30', '1905-03-10', 'Japanese victory',
       'The land campaign across Manchuria and the Liaodong Peninsula, decided by the massive battle of Mukden.'
FROM conflicts WHERE name = 'Russo-Japanese War';

INSERT INTO theaters (conflict_id, name, region, start_date, end_date, outcome, description)
SELECT id, 'Naval Theater', 'Yellow Sea & Korea Strait', '1904-02-08', '1905-05-28', 'Japanese victory',
       'The struggle for command of the sea, culminating in the destruction of the Russian Baltic Fleet at Tsushima.'
FROM conflicts WHERE name = 'Russo-Japanese War';

UPDATE battles SET theater_id = (
    SELECT t.id FROM theaters t JOIN conflicts c ON t.conflict_id = c.id
    WHERE t.name = 'Manchurian Front' AND c.name = 'Russo-Japanese War')
WHERE conflict_id = (SELECT id FROM conflicts WHERE name = 'Russo-Japanese War')
  AND name IN ('Battle of the Yalu River', 'Siege of Port Arthur', 'Battle of Liaoyang', 'Battle of Mukden');

UPDATE battles SET theater_id = (
    SELECT t.id FROM theaters t JOIN conflicts c ON t.conflict_id = c.id
    WHERE t.name = 'Naval Theater' AND c.name = 'Russo-Japanese War')
WHERE conflict_id = (SELECT id FROM conflicts WHERE name = 'Russo-Japanese War')
  AND name IN ('Battle of Port Arthur', 'Battle of the Yellow Sea', 'Battle of Tsushima');

INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'ATTACKER', 1200000, 240000, 'Victory'
FROM conflicts c, nations n
WHERE c.name = 'Russo-Japanese War' AND n.name = 'Empire of Japan';

INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'DEFENDER', 1365000, 170000, 'Defeat'
FROM conflicts c, nations n
WHERE c.name = 'Russo-Japanese War' AND n.name = 'Russian Empire';

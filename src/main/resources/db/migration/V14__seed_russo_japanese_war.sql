-- ============================================================
-- Seed data: Russo-Japanese War (1904-05) — the second map-ready conflict (slice B3).
-- FKs resolved by name-subquery (no hardcoded IDs), per the V6/V10 convention.
-- Battles carry latitude/longitude (the V13 columns) so /atlas has pins immediately.
-- Mirrored in db/demo/demo_reset.sql — keep the two in sync.
-- ============================================================

-- Nation (Russian Empire was already seeded in V6; only Japan is new here).
INSERT INTO nations (name, region, founded_year, dissolved_year, description) VALUES
    ('Empire of Japan', 'East Asia', 1868, 1947,
     'Rapidly industrialised Meiji-era power; its victory here was the first modern defeat of a European great power by an Asian state.');

-- Conflict
INSERT INTO conflicts (name, conflict_type, start_date, end_date, outcome, description) VALUES
    ('Russo-Japanese War', 'WAR', '1904-02-08', '1905-09-05', 'Japanese victory',
     'Fought over rival imperial ambitions in Manchuria and Korea. Japan''s victories on land at Mukden and at sea at Tsushima forced Russia to terms in the U.S.-mediated Treaty of Portsmouth.');

-- Battles (with coordinates), authored in chronological order.
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

-- Theaters
INSERT INTO theaters (conflict_id, name, region, start_date, end_date, outcome, description)
SELECT id, 'Manchurian Front', 'Manchuria & Liaodong', '1904-04-30', '1905-03-10', 'Japanese victory',
       'The land campaign across Manchuria and the Liaodong Peninsula, decided by the massive battle of Mukden.'
FROM conflicts WHERE name = 'Russo-Japanese War';

INSERT INTO theaters (conflict_id, name, region, start_date, end_date, outcome, description)
SELECT id, 'Naval Theater', 'Yellow Sea & Korea Strait', '1904-02-08', '1905-05-28', 'Japanese victory',
       'The struggle for command of the sea, culminating in the destruction of the Russian Baltic Fleet at Tsushima.'
FROM conflicts WHERE name = 'Russo-Japanese War';

-- Assign battles to theaters (scoped to this conflict so names can't collide with other conflicts).
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

-- Conflict participants
INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'ATTACKER', 1200000, 240000, 'Victory'
FROM conflicts c, nations n
WHERE c.name = 'Russo-Japanese War' AND n.name = 'Empire of Japan';

INSERT INTO conflict_participants (conflict_id, nation_id, role, troops_committed, casualties, outcome)
SELECT c.id, n.id, 'DEFENDER', 1365000, 170000, 'Defeat'
FROM conflicts c, nations n
WHERE c.name = 'Russo-Japanese War' AND n.name = 'Russian Empire';

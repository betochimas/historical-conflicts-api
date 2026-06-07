-- WWI alliances + members, extending the V6 seed. FK resolution is by name-subquery (no hardcoded
-- IDs), per V6's convention. Keep in sync with db/demo/demo_reset.sql.

INSERT INTO alliances (name, alliance_type, formed_date, dissolved_date, description) VALUES
    ('Triple Entente', 'MILITARY', '1907-08-31', '1917-11-07',
     'The understanding between Britain, France, and Russia that aligned the principal Allied powers entering WWI.'),
    ('Central Powers', 'COALITION', '1914-08-01', '1918-11-11',
     'The wartime coalition of the German Empire, Austria-Hungary, and the Ottoman Empire (later Bulgaria).'),
    ('Allied Powers', 'COALITION', '1914-09-05', NULL,
     'The broad WWI coalition opposing the Central Powers, growing to include the United States and Japan.');

-- Triple Entente members
INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1907-08-31', NULL
FROM alliances a, nations n
WHERE a.name = 'Triple Entente' AND n.name = 'United Kingdom';

INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1907-08-31', NULL
FROM alliances a, nations n
WHERE a.name = 'Triple Entente' AND n.name = 'France';

INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1907-08-31', '1917-11-07'
FROM alliances a, nations n
WHERE a.name = 'Triple Entente' AND n.name = 'Russian Empire';

-- Central Powers members
INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1914-08-01', '1918-11-11'
FROM alliances a, nations n
WHERE a.name = 'Central Powers' AND n.name = 'German Empire';

INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1914-08-01', '1918-11-03'
FROM alliances a, nations n
WHERE a.name = 'Central Powers' AND n.name = 'Austria-Hungary';

INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1914-10-29', '1918-10-30'
FROM alliances a, nations n
WHERE a.name = 'Central Powers' AND n.name = 'Ottoman Empire';

-- Allied Powers members
INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1914-09-05', NULL
FROM alliances a, nations n
WHERE a.name = 'Allied Powers' AND n.name = 'United Kingdom';

INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1914-09-05', NULL
FROM alliances a, nations n
WHERE a.name = 'Allied Powers' AND n.name = 'France';

INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1914-09-05', '1917-12-15'
FROM alliances a, nations n
WHERE a.name = 'Allied Powers' AND n.name = 'Russian Empire';

INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1917-04-06', NULL
FROM alliances a, nations n
WHERE a.name = 'Allied Powers' AND n.name = 'United States';

INSERT INTO alliance_members (alliance_id, nation_id, joined_date, left_date)
SELECT a.id, n.id, '1914-08-23', NULL
FROM alliances a, nations n
WHERE a.name = 'Allied Powers' AND n.name = 'Empire of Japan';

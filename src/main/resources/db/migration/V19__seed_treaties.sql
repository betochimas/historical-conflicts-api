-- WWI treaties + signatories, extending the V6 seed. FK resolution is by name-subquery (no
-- hardcoded IDs), per V6's convention. Keep in sync with db/demo/demo_reset.sql.

INSERT INTO treaties (conflict_id, name, treaty_type, signed_date, location, description)
SELECT id, 'Treaty of Versailles', 'PEACE', '1919-06-28', 'Versailles, France',
       'The principal peace treaty ending WWI; imposed territorial losses, disarmament, and reparations on Germany and established the League of Nations.'
FROM conflicts WHERE name = 'World War I';

INSERT INTO treaties (conflict_id, name, treaty_type, signed_date, location, description)
SELECT id, 'Armistice of 11 November 1918', 'ARMISTICE', '1918-11-11', 'Compiègne, France',
       'The armistice signed in Marshal Foch''s railway carriage that ended the fighting on the Western Front.'
FROM conflicts WHERE name = 'World War I';

-- Treaty of Versailles signatories
INSERT INTO treaty_signatories (treaty_id, nation_id, role, ratified_date)
SELECT t.id, n.id, 'SIGNATORY', NULL
FROM treaties t, nations n
WHERE t.name = 'Treaty of Versailles' AND n.name = 'German Empire';

INSERT INTO treaty_signatories (treaty_id, nation_id, role, ratified_date)
SELECT t.id, n.id, 'SIGNATORY', '1919-10-13'
FROM treaties t, nations n
WHERE t.name = 'Treaty of Versailles' AND n.name = 'France';

INSERT INTO treaty_signatories (treaty_id, nation_id, role, ratified_date)
SELECT t.id, n.id, 'SIGNATORY', '1919-07-31'
FROM treaties t, nations n
WHERE t.name = 'Treaty of Versailles' AND n.name = 'United Kingdom';

-- The United States signed but the Senate never ratified — ratified_date left NULL.
INSERT INTO treaty_signatories (treaty_id, nation_id, role, ratified_date)
SELECT t.id, n.id, 'SIGNATORY', NULL
FROM treaties t, nations n
WHERE t.name = 'Treaty of Versailles' AND n.name = 'United States';

-- Armistice of 11 November 1918 signatories
INSERT INTO treaty_signatories (treaty_id, nation_id, role, ratified_date)
SELECT t.id, n.id, 'SIGNATORY', NULL
FROM treaties t, nations n
WHERE t.name = 'Armistice of 11 November 1918' AND n.name = 'France';

INSERT INTO treaty_signatories (treaty_id, nation_id, role, ratified_date)
SELECT t.id, n.id, 'SIGNATORY', NULL
FROM treaties t, nations n
WHERE t.name = 'Armistice of 11 November 1918' AND n.name = 'United Kingdom';

INSERT INTO treaty_signatories (treaty_id, nation_id, role, ratified_date)
SELECT t.id, n.id, 'SIGNATORY', NULL
FROM treaties t, nations n
WHERE t.name = 'Armistice of 11 November 1918' AND n.name = 'German Empire';

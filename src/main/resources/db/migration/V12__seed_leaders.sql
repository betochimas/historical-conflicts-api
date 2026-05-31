-- ============================================================
-- Seed data: World War I leaders, one or two per seeded nation.
-- FKs resolved by name-subquery (no hardcoded IDs), per V6's convention.
-- Mirrored in db/demo/demo_reset.sql — keep the two in sync.
-- ============================================================

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

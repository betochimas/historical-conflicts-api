-- ============================================================
-- Add `side` (coalition / belligerent group) to conflict_participants.
-- Requested by the frontend for map coalition-coloring (G4) — see
-- Shared/conflicts-integration.md. Free text, nullable so existing rows
-- (and future inserts that don't set it) are unaffected.
-- Backfill scoped by conflict + nation name (no hardcoded IDs), per the
-- V6/V14 seed convention. Russian Empire fights in BOTH conflicts on
-- different sides, so the conflict_id scoping is essential here.
-- Mirrored in db/demo/demo_reset.sql — keep the two in sync.
-- ============================================================

ALTER TABLE conflict_participants ADD COLUMN side VARCHAR(100);

-- World War I — Allied Powers
UPDATE conflict_participants SET side = 'Allied Powers'
WHERE conflict_id = (SELECT id FROM conflicts WHERE name = 'World War I')
  AND nation_id IN (
      SELECT id FROM nations
      WHERE name IN ('United Kingdom', 'France', 'Russian Empire', 'United States'));

-- World War I — Central Powers
UPDATE conflict_participants SET side = 'Central Powers'
WHERE conflict_id = (SELECT id FROM conflicts WHERE name = 'World War I')
  AND nation_id IN (
      SELECT id FROM nations
      WHERE name IN ('German Empire', 'Austria-Hungary', 'Ottoman Empire'));

-- Russo-Japanese War — each belligerent is its own side (1-v-1)
UPDATE conflict_participants SET side = 'Empire of Japan'
WHERE conflict_id = (SELECT id FROM conflicts WHERE name = 'Russo-Japanese War')
  AND nation_id = (SELECT id FROM nations WHERE name = 'Empire of Japan');

UPDATE conflict_participants SET side = 'Russian Empire'
WHERE conflict_id = (SELECT id FROM conflicts WHERE name = 'Russo-Japanese War')
  AND nation_id = (SELECT id FROM nations WHERE name = 'Russian Empire');

# Discovery & Recording

> ⚠ **Revision pending.** Per [`DECISIONS.md`](DECISIONS.md): the mod is **no-GUI**
> (D1), so any "Codex screen"/UI language below will be recast as in-world +
> on-item, and sky reading is now a **v1** system (D4). The exact recording/review
> surface depends on open question **Q1** (knowledge storage model).

The front half of the loop: how glyphs hide in the world, how the player *finds*
and *records* them, and how a recorded shape becomes a translated meaning. This is
the part that makes Epigraphy feel like exploration rather than a skill tree.

Design north star: **the game never marks a glyph you haven't found.** The Codex
is a record of *your* observations, not a quest tracker.

---

## 1. Where glyphs hide

Three sources, tuned so that a player who only ever does one activity still makes
slow progress, but a player who does all three races ahead.

### 1.1 Carvings in worldgen (exploration)
Glyph carvings are attached to structures as decoration blocks:

- **Ruined shrines** — small overworld/nether structures seeded with 1–3 carved
  glyph blocks. The rarer the structure, the rarer the glyphs.
- **Opportunistic carvings** — individual `glyph_carving` blocks scattered on
  deepslate in caves, on blackstone in the Nether, on desert temple walls. These
  are the "Risk of Rain hidden symbol" beat: easy to walk past, rewarding to
  spot. A carving's glyph is chosen from a rarity-weighted table for its biome.

A carving block renders the glyph as a faint relief. It does nothing until you
**record** it (§2).

### 1.2 Constellation-glyphs in the sky (observation)
Some glyphs — the **celestial** category especially — are never carved; they are
*written in the stars*. Using an **Observatory** (a placed multiblock) or a
handheld **Astrolabe** at night, the player can resolve constellations whose star
patterns form a glyph. Cloud cover, moon phase, and dimension gate which are
visible, echoing Astral Sorcery's sky mechanic but with the glyph language as the
payload.

- Requires `NOX` (Night) conditions to attempt — you must be able to see the sky
  and it must be dark.
- Reading `CAELUM` from the sky, then later seeing it carved, counts as two
  independent sightings toward translation.

### 1.3 Inscribed tablets (combat)
Mobs and bosses drop **inscribed tablets** — items that carry a pre-recorded
glyph. Studying a tablet at a **Lectern of Study** grants a sighting instantly,
skipping the "spot it in the world" step.

- Common mobs rarely drop **common** glyph tablets.
- Minibosses / structure guardians drop **uncommon** tablets.
- Bosses (e.g. a modded ritual guardian, or piggybacking on vanilla boss loot via
  datapack) drop **rare** tablets like `CHAOS`.
- A special **Rosetta tablet** (rare boss/loot drop) instantly *translates* one
  already-sighted glyph, jumping it straight to Tier 2. Rosettas are the pity
  mechanic for glyphs a player keeps sighting but can't crack.

---

## 2. The Codex and the "record" action

The **Codex** is the player's book item and the UI home of the knowledge system.

- **Recording a carving/constellation:** look at it and use the Codex (or a
  **Charcoal Rubbing** consumable for carvings). This logs a *sighting* of that
  glyph to the player's knowledge capability (`KNOWLEDGE.md`). The Codex page for
  that glyph now shows its symbol and a sighting counter (`2 / 3`), but the
  meaning still reads `???`.
- **Studying a tablet:** right-click the tablet on a Lectern of Study; it is
  consumed and produces a sighting (or, for a Rosetta, a translation).
- Re-recording the *same* carving block does not count twice — sightings must be
  *independent* (different carving instances, or a sky reading vs. a carving).
  Each carving block stores whether this player has already recorded it.

---

## 3. From sighting to translation (Tier 1 → Tier 2)

A glyph translates — becomes readable everywhere — when either:

1. **Enough independent sightings** accumulate. The threshold is the glyph's
   `sightings_to_translate` (3 for `CHAOS`, 1 for a common glyph). The idea: seeing
   the same symbol in enough contexts lets you triangulate its meaning, exactly
   how real decipherment works. **or**
2. **A Rosetta tablet** is applied to it.

On translation:
- Every place the glyph appears (carvings, tablets, ritual pages) now renders the
  translated gloss/flavor text.
- Ritual pages that use the glyph gain a readable clause (`GLYPHS.md` §3).
- A subtle client toast/codex-unlock effect plays. No numeric reward.

---

## 4. Ritual pages: learning *what to attempt*

Knowing glyphs is necessary but not sufficient — the player also needs to know a
*combination* is meaningful. **Ritual pages** bridge this:

- A ritual page is discovered as loot (tablets, shrine chests) or auto-revealed
  once the player has *sighted* every glyph the ritual uses (a tunable rule; see
  `KNOWLEDGE.md` §4).
- A page at Tier 2 shows the translated *instruction sentence* (`GLYPHS.md` §3) —
  enough to reason out the build, never the exact bill of materials.
- Performing the ritual, or otherwise obtaining its result, promotes the page to
  Tier 3 and writes the exact recipe to JEI (`KNOWLEDGE.md` §5).

---

## 5. Blocks & items introduced by this system

| Registry object | Type | Role |
|-----------------|------|------|
| `epigraphy:glyph_carving` | Block (block-entity) | Holds a glyph id; sightable; worldgen decoration |
| `epigraphy:observatory` | Multiblock/Block | Resolve celestial glyphs at night |
| `epigraphy:astrolabe` | Item | Handheld, weaker constellation reader |
| `epigraphy:codex` | Item | Knowledge UI + record action |
| `epigraphy:charcoal_rubbing` | Item | Consumable that records a carving at range |
| `epigraphy:lectern_of_study` | Block | Study tablets into sightings |
| `epigraphy:inscribed_tablet` | Item | Carries a glyph id; boss/mob drop |
| `epigraphy:rosetta_tablet` | Item | Instantly translates one sighted glyph |

---

## 6. Worldgen & loot as data

- **Carvings** are placed via a `PlacedFeature` + `ConfiguredFeature` that decorate
  target structures/blocks, choosing glyphs from a rarity-weighted loot-table-like
  list keyed by biome/dimension. All data-driven under `data/epigraphy/worldgen/`.
- **Tablet drops** are added via **loot table modifiers** (Forge Global Loot
  Modifiers) so we can inject glyph tablets into vanilla and modded mob/boss loot
  without overwriting their tables. Weighted by glyph rarity.
- **Constellation visibility** rules (which glyphs, which moon phases/dimensions)
  live in `data/epigraphy/constellations/`.

Keeping all three data-driven means the *distribution* of discovery — the pacing
of the whole mod — is tunable without code changes, which is exactly where a
discovery mod lives or dies.

# Discovery & Recording

The front half of the loop: how glyphs hide in the world, how the player *finds*
and *records* them, and how a recorded shape becomes a *learned* glyph. This is the
mod's **research** (D5) — but the research is done by exploring and reading the
world, not by a minigame, and it involves **no gameplay GUI** (D1): recording and
studying are in-world/on-item actions.

Design north star: **the game never marks a glyph you haven't found.** Your record
is of *your* observations, not a quest tracker.

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

## 2. Recording — an in-world / on-item action (no GUI)

There is **no Codex screen** (D1). Recording is a physical interaction, and what
you know is surfaced through item tooltips and in-world visuals, not a menu.

- **Recording a carving:** use a **Charcoal Rubbing** consumable (or the Codex
  item) *on* the carving. The action produces an **on-item** result — an inscribed
  rubbing/tablet whose tooltip shows the glyph symbol — and logs an independent
  *sighting* to the player's knowledge capability (`KNOWLEDGE.md`). Until learned,
  the tooltip reads `???`.
- **Recording a constellation:** resolve it at the **Observatory** or with the
  **Astrolabe** at night; the sighting is logged the same way (§1.2).
- **Studying a tablet:** right-click the tablet on a **Lectern of Study**. It is
  consumed and yields a sighting (or, for a Rosetta, an outright *learn*). The
  lectern shows progress *in-world* — e.g. a floating relief of the glyph that
  sharpens from faint to solid as sightings accumulate — never a screen.
- **The Codex item** is a held journal used to *perform* the record action and to
  carry rubbings; it is not a UI. Reviewing what you've learned happens through
  your physical rubbings/tablets (tooltips) and the read-only **in-game
  documentation** reference layer (`KNOWLEDGE.md` §3/§6), which populates as you
  learn.
- Re-recording the *same* carving does not count twice — sightings must be
  *independent* (different carving instances, or a sky reading vs. a carving). Each
  carving block remembers whether this player already recorded it.

---

## 3. From sighting to translation (Tier 1 → Tier 2)

A glyph translates — becomes readable everywhere — when either:

1. **Enough independent sightings** accumulate. The threshold is the glyph's
   `sightings_to_translate` (3 for `CHAOS`, 1 for a common glyph). The idea: seeing
   the same symbol in enough contexts lets you triangulate its meaning, exactly
   how real decipherment works. **or**
2. **A Rosetta tablet** is applied to it.

On learning:
- Every place the glyph appears (carvings, tablet tooltips, ritual instructions)
  now renders the translated gloss/flavor text.
- Rituals that use the glyph gain a readable clause in their instructions
  (`GLYPHS.md` §3, `KNOWLEDGE.md` §4).
- Feedback is in-world/on-item: the recorded carvings that use this glyph visibly
  resolve to readable text, a soft particle/sound cue plays, and the item tooltip
  updates. No screen, no numeric reward.

---

## 4. Understanding *what to attempt*

Knowing glyphs is necessary but not sufficient — the player also needs to know a
*combination* is meaningful. Research bridges this:

- A ritual becomes **understood** once the player has *learned* every glyph it uses
  (its research is complete), or by looting a **ritual tablet** that seeds it
  (`KNOWLEDGE.md` §4). Its instructions then read clearly.
- Understood instructions are the translated *sentence* (`GLYPHS.md` §3) — enough
  to reason out the build, never the exact bill of materials. They're legible
  on-item (ritual tablets) and in the in-game documentation.
- Performing the ritual, or otherwise obtaining its result, **masters** it (Tier 3)
  and writes the exact recipe into the documentation and JEI (`KNOWLEDGE.md` §6).
- Attempting a ritual you *haven't* learned is possible but risks **backlash**
  (`KNOWLEDGE.md` §5) — research first, or pay for it.

---

## 5. Blocks & items introduced by this system

| Registry object | Type | Role |
|-----------------|------|------|
| `epigraphy:glyph_carving` | Block (block-entity) | Holds a glyph id; sightable; worldgen decoration |
| `epigraphy:observatory` | Multiblock/Block | Resolve celestial glyphs at night |
| `epigraphy:astrolabe` | Item | Handheld, weaker constellation reader |
| `epigraphy:codex` | Item | Held journal: performs the record action, carries rubbings — **no UI** |
| `epigraphy:charcoal_rubbing` | Item | Consumable used *on* a carving to record it (yields an inscribed rubbing) |
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

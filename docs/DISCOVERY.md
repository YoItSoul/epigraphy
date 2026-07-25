# Discovery & Recording

The front half of the loop: how glyphs hide in the world, how the player *finds*
and *records* them, how a recorded shape becomes a *learned* glyph, and how learned
glyphs get combined into decoded **rune words** (`RUNES.md` §3). This is the mod's
**research** (D5) — done by exploring and reading the world. All world interaction
is in-world; the **hand codex** is the one item-borne interface (D1/D7).

Design north star: **the game never tells you what a glyph means or marks one on a
map.** Seek mode (§3) points you toward *somewhere worth looking*, never at an
answer — a dowsing needle, not a quest tracker.

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

## 2. Recording — an in-world action

Recording is always a physical interaction with the world; no block ever opens a
menu (D1).

- **Recording a carving:** use a **Charcoal Rubbing** consumable (or the Codex
  itself) *on* the carving. The action produces an **on-item** result — an inscribed
  rubbing whose tooltip shows the glyph symbol — and logs an independent *sighting*
  to the player's knowledge capability (`KNOWLEDGE.md`). Until learned, the tooltip
  reads `???`.
- **Recording a constellation:** resolve it at the **Observatory** or with the
  **Astrolabe** at night; the sighting is logged the same way (§1.2).
- **Studying a tablet:** right-click the tablet on a **Lectern of Study**. It is
  consumed and yields a sighting (or, for a Rosetta, an outright *learn*). The
  lectern shows progress *in-world* — a floating relief of the glyph that sharpens
  from faint to solid as sightings accumulate — never a screen.
- Re-recording the *same* carving does not count twice — sightings must be
  *independent* (different carving instances, or a sky reading vs. a carving). Each
  carving block remembers whether this player already recorded it.

---

## 3. The hand codex (D7 / D9)

The **codex** is the player's research instrument and the mod's single interface
exception (D1). It is deliberately **not a menu**: a flat grid of **20 empty glyph
slots** and a submit action, nothing else. No inventory, no tabs, no crafting grid.

```
┌────┬────┬────┬────┬────┐   each slot cycles through
│ ▲  │ ▲  │    │ ▲  │ ▲  │   ONLY the glyphs you have
│FLAM│VIRG│    │INFE│META│   already discovered
│ ▼  │ ▼  │    │ ▼  │ ▼  │
├────┼────┼────┼────┼────┤   gaps separate rune words
│ ▲  │ ▲  │    │    │    │   (Q8), so this grid holds
│CAEL│CHAO│    │    │    │   three guesses at once
│ ▼  │ ▼  │    │    │    │
├────┴────┴────┴────┴────┤
│  … 10 more slots …     │        [ SUBMIT ]
└────────────────────────┘
```

### 3.1 Submit — guess a rune word
The player cycles slots to spell out a **rune word** of 2–3 **discovered** glyphs
and submits (`RUNES.md` §3.2). The codex answers whether that combination names
something real:

- **Match** → the rune word is **decoded** permanently. It now renders as its true
  referent ("Blaze Rod") everywhere instead of literal glosses ("Flaming · Rod"),
  and every ritual hint using it sharpens.
- **No match** → a soft negative cue. No penalty, no progress. (Cost/cooldown model
  open — `DECISIONS.md` Q7.)

Two properties make this work:
- **Only discovered glyphs are in the cycle**, so the codex physically cannot
  express a symbol you haven't found. The search space starts tiny and grows with
  exploration.
- **20 slots hold a whole ritual's worth of guesses**, so the player works the
  puzzle as one board rather than one word at a time.

This is the mod's active decode loop — the counterpart to passively learning
vocabulary (D2). Glyphs are *discovered*; rune words are *guessed*.

### 3.2 Seek — find somewhere worth looking
**Seek mode** points toward the **nearest structure containing a glyph the player
hasn't learned yet**. It is deliberately a *dowsing needle*, not a map marker:

- It gives **direction and rough distance** only — rendered in-world/on-item (a
  needle that swings, a warming/cooling cue, particles trailing off toward the
  bearing). No waypoint, no map overlay, no coordinates.
- It never reveals **which** glyph is there, nor what it means. You still have to
  travel, find the carving, and record it.
- It targets **structures**, so the payoff is a place to explore — which usually
  carries loot and multiple glyphs — rather than a single pinned symbol.
- If every glyph in range is already learned, the needle simply idles.

This is the anti-frustration valve: it guarantees a player is never stuck with
"I don't know where to go next," while preserving the north star that the game
never hands you a *meaning*.

### 3.3 Reference — what you already know
The codex also holds the read-only reference layer (`KNOWLEDGE.md` §6): learned
glyphs, decoded rune words, and mastered rituals, filling in as you earn them. One
item, one mental model — likely obviating a separate guide book
(`DECISIONS.md` Q5).

---

## 4. From sighting to learning (Tier 1 → Tier 2)

A glyph is learned — becomes readable everywhere — when either:

1. **Enough independent sightings** accumulate. The threshold is the glyph's
   `sightings_to_translate` (3 for `CHAOS`, 1 for a common glyph). The idea: seeing
   the same symbol in enough contexts lets you triangulate its meaning, exactly
   how real decipherment works. **or**
2. **A Rosetta tablet** is applied to it.

On learning:
- Every place the glyph appears (carvings, tablet tooltips, ritual hints) now
  renders its gloss instead of `???`.
- It becomes **submittable** in the codex, so it can start forming rune words (§3.1).
- Feedback is in-world/on-item: recorded carvings using this glyph visibly resolve
  to readable text, a soft particle/sound cue plays, and item tooltips update. No
  screen, no numeric reward.

---

## 5. Understanding *what to attempt*

Knowing glyphs is necessary but not sufficient — the player also needs to decode
what *combinations* name. That's the rune word layer:

- A ritual's hint is a set of **2–3 glyph rune words** (`RUNES.md` §3), each naming
  one component: its altar, a catalyst, a world condition, its output.
- Each rune word renders per what you know — unreadable symbols, then literal glosses
  ("Flaming · Rod"), then its true referent ("Blaze Rod") once decoded in the codex.
  A ritual you've partly decoded is a partly-solved puzzle, and the unsolved parts
  tell you exactly what to work on.
- A ritual becomes **understood** once its rune words are decoded (or by looting a
  **ritual tablet** that seeds it). Understanding gives you enough to reason out
  the build — never the exact bill of materials.
- Performing the ritual, or otherwise obtaining its result, **masters** it (Tier 3)
  and writes the exact recipe into the codex reference and JEI (`KNOWLEDGE.md` §6).
- Attempting a ritual you *haven't* learned is possible but risks **backlash**
  (`KNOWLEDGE.md` §5) — research first, or pay for it.

---

## 6. Blocks & items introduced by this system

| Registry object | Type | Role |
|-----------------|------|------|
| `epigraphy:glyph_carving` | Block (block-entity) | Holds a glyph id; sightable; worldgen decoration |
| `epigraphy:observatory` | Multiblock/Block | Resolve celestial glyphs at night |
| `epigraphy:astrolabe` | Item | Handheld, weaker constellation reader |
| `epigraphy:codex` | Item | The research instrument: submit rune words, seek mode, reference (§3) |
| `epigraphy:charcoal_rubbing` | Item | Consumable used *on* a carving to record it (yields an inscribed rubbing) |
| `epigraphy:lectern_of_study` | Block | Study tablets into sightings |
| `epigraphy:inscribed_tablet` | Item | Carries a glyph id; boss/mob drop |
| `epigraphy:rosetta_tablet` | Item | Instantly translates one sighted glyph |

---

## 7. Worldgen & loot as data

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

### 7.1 What seek mode needs from worldgen
Seek (§3.2) must answer "where is the nearest structure holding a glyph I haven't
learned?" without scanning the world. Two viable approaches, to settle in
implementation:

- **Structure-tagged lookup (preferred).** Tag structures with the glyph pool they
  can contain, then use the vanilla structure-locating path (`ChunkGenerator`
  structure search) filtered to structures whose pool includes an unlearned glyph.
  Cheap, uses existing machinery, and naturally points at *places*.
- **Carving index.** Maintain a per-dimension saved-data index of generated carving
  positions and their glyphs, queried by proximity. More precise, but adds
  persistent state and can point at a lone carving rather than a structure.

The structure-tagged approach is the better fit for the design intent: seek should
send you toward a *place worth exploring*, not a single pinned symbol.

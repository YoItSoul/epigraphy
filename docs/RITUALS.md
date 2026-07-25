# Rituals & Infusion

The back half of the loop: the altar, the pedestals, the infusion fluid, and the
environmental conditions that turn a set of decoded **rune words** into an actual
crafting event. This document specifies the multiblock, the full JSON recipe
schema, the backlash system, and worked examples.

Two decisions shape this doc: glyphs are a **research** layer, **not** physical
altar ingredients (D5) — pedestals only ever hold catalyst items, and the altar
reads the player's *learned* glyphs from their knowledge capability. And every
ritual runs entirely **in-world** with no menu (D1): you build it, trigger it, and
watch it transform in the world.

---

## 1. The altar multiblock

A ritual happens at an **Altar**, a tiered structure:

```
            (pedestal)         (pedestal)

  (pedestal)      ┌───────────┐       (pedestal)
                  │   ALTAR   │
                  │   CORE    │  ← infusion happens here
  (pedestal)      └───────────┘       (pedestal)

            (pedestal)         (pedestal)
```

- **Altar core** — the central block that runs the ritual. Its *tier* is set by
  the material it's built from and gates which rituals it can run.
  - **T1 — Stone Altar** (`epigraphy:stone_altar`): intro rituals.
  - **T2 — Blackstone Altar** (`epigraphy:blackstone_altar`): the Chaos Ingot tier
    and most nether-flavoured work.
  - **T3 — (future)**: a celestial/eldritch tier for late game.
- **Pedestals** (`epigraphy:pedestal`) — placed around the core in a pattern the
  recipe cares about only by *count and contents*, not exact position (v1). Each
  holds one catalyst itemstack.
- **The infusion pool** — the core sits over (or contains) the infusion fluid.
  The **input item** is dropped/placed into the pool. The fluid is consumed as the
  ritual runs.

The core is a block-entity that, when activated (right-click with the input, or a
redstone/"ignite" trigger), scans its pedestals + pool + environment and looks for
a matching ritual recipe the player is allowed to run.

---

## 2. The infusion fluid

Rituals consume a magical fluid held in the pool. v1 ships one:

- **`epigraphy:liquid_starlight`** (placeholder name; Astral homage) — gathered by
  leaving basins under an open night sky, or as a ritual byproduct. Different
  rituals may require different fluids in later tiers (e.g. a `liquid_umbra` for
  dark rituals), which is why `fluid` is a per-recipe field.

Fluids are standard Forge fluids so they interact with tanks/pipes from other mods.

---

## 3. Conditions

Conditions are the "world as ingredient" system. A ritual lists conditions that
must *all* hold when it fires (unless a condition is marked `optional`, in which
case it grants a bonus or alternate result rather than gating). Condition types in
v1:

| `type` | `value` examples | Meaning |
|--------|------------------|---------|
| `epigraphy:weather` | `clear`, `rain`, `thunder` | Current weather at the altar |
| `epigraphy:time` | `day`, `night`, or a tick range `[13000, 23000]` | Time of day |
| `epigraphy:moon_phase` | `0`–`7`, or `full`, `new` | Lunar phase |
| `epigraphy:dimension` | `minecraft:the_nether`, `minecraft:overworld` | Dimension of the altar |
| `epigraphy:sky_visible` | `true` / `false` | Whether the pool can see the sky |
| `epigraphy:biome` | biome id or tag | Biome at the altar |

Condition types are an extensible registry: adding a new one (say, "player is on
fire" or "nearby block") is a small code addition, but the *recipes* that use them
stay pure data. This is the one place the design deliberately reserves room for
code growth.

---

## 4. Recipe schema

Rituals are Minecraft recipes of type `epigraphy:infusion`, loaded from
`data/<namespace>/recipes/`. Full schema:

```jsonc
{
  "type": "epigraphy:infusion",

  // Which altar tier is required (by block id). Higher-tier altars can run
  // lower-tier recipes unless "exact_altar": true.
  "altar": "epigraphy:blackstone_altar",

  // The ritual's hint: a set of 2-3 glyph RUNE WORDS (D8, RUNES.md §3), each naming
  // one component. Drives (a) the hint text the player reads and (b) the research
  // gate (KNOWLEDGE.md §4). Unordered for mechanics; order is presentational.
  "rune_words": [
    "epigraphy:blackstone_altar",   // ALTARE · TENEBRAE
    "epigraphy:blaze_rod",          // FLAMMANS · VIRGA
    "epigraphy:thunderstorm",       // CAELUM  · CHAOS
    "epigraphy:netherite",          // INFERNUS· METALLUM
    "epigraphy:chaos_ingot"         // CHAOS   · METALLUM  (the output)
  ],

  // Catalyst items on pedestals. Matched as a multiset (order-independent).
  // Each entry is a vanilla Ingredient plus a count.
  "pedestals": [
    { "item": "minecraft:blaze_rod", "count": 4 }
    // or { "tag": "forge:rods/blaze", "count": 4 }
  ],

  // The item consumed in the pool and transformed. A single Ingredient.
  "input": { "item": "minecraft:netherite_ingot", "count": 1 },

  // Fluid consumed, with an amount in mB.
  "fluid": "epigraphy:liquid_starlight",
  "fluid_amount": 1000,

  // World conditions; all required unless "optional": true.
  "conditions": [
    { "type": "epigraphy:weather",   "value": "thunder" },
    { "type": "epigraphy:sky_visible","value": true },
    { "type": "epigraphy:dimension", "value": "minecraft:the_nether", "optional": true }
  ],

  // Output.
  "result": { "item": "epigraphy:chaos_ingot", "count": 1 },

  // How long the ritual animates before producing output.
  "duration_ticks": 200,

  // Optional: if true, only this exact altar tier works (no higher-tier substitute).
  "exact_altar": false
}
```

### Matching algorithm (what the altar core does)
1. On activation, read: altar tier, the multiset of pedestal items, the input in
   the pool, the fluid + amount available, and evaluate all condition types for
   the current world/position. (Glyphs/rune words are **not** read from the world —
   they live in the player's research, step 3.)
2. Filter recipes to those whose `altar`, `pedestals`, `input`, `fluid`,
   `fluid_amount`, and required `conditions` all match the physical setup.
3. Apply the **research check** (`KNOWLEDGE.md` §4b) against the triggering player:
   - **Decoded all** the recipe's rune words → **clean** run.
   - **Some rune words undecoded** → a **blind attempt**: it still fires (the build is
     physically correct) but triggers **backlash** (§4.1) scaled by how many
     rune words remain undecoded.
   - **No physical match at all** → the altar sputters (in-world particles/sound),
     nothing is consumed.
4. Run it: consume fluid/pedestal items over `duration_ticks` with in-world
   particles, then consume the input and produce `result` in the world.
5. On a successful, *understood* run, **master** the ritual (`KNOWLEDGE.md` §6):
   its exact recipe is written to the in-game documentation and JEI. Obtaining the
   result item by any means does the same via an inventory hook, so trading/looting
   the item also unlocks its reference entry.

### 4.1 Backlash (D3)
A **blind attempt** (step 3) does not fail silently — it *bites*. Severity is a
per-recipe base amplified by how many of its rune words the player hasn't decoded and
by the player's accrued `instability` (`KNOWLEDGE.md` §5). Escalating effects:

- **Minor:** the input and some pedestal items are consumed for nothing; a puff of
  corrupt particles.
- **Moderate:** an **anomaly/hostile** spawns at the altar (thematically tied to
  the ritual — a Chaos wisp for chaotic rites, a shade for dark ones).
- **Severe:** lingering **area corruption** the world remembers (a decay block /
  status field around the altar), plus a bump to the player's `instability` that
  makes the next reckless attempt worse.

Optional per-recipe tuning via a `backlash` field:

```jsonc
"backlash": {
  "base": "moderate",           // minor | moderate | severe
  "anomaly": "epigraphy:chaos_wisp",   // what spawns on moderate+
  "corruption": "epigraphy:chaos_scar" // block/field left on severe
}
```

If omitted, a sensible default is derived from the rarest glyph appearing in the
recipe's rune words (rare glyphs → harsher backlash). A pack can set
`require_learning_to_attempt` to make blind attempts simply fizzle instead
(`KNOWLEDGE.md` §4).

---

## 5. Worked examples

### 5.1 Chaos Ingot (flagship, T2)
```jsonc
// data/epigraphy/recipes/chaos_ingot.json
{
  "type": "epigraphy:infusion",
  "altar": "epigraphy:blackstone_altar",
  "rune_words": [
    "epigraphy:blackstone_altar",   // ALTARE  · TENEBRAE
    "epigraphy:blaze_rod",          // FLAMMANS· VIRGA
    "epigraphy:thunderstorm",       // CAELUM  · CHAOS
    "epigraphy:netherite",          // INFERNUS· METALLUM
    "epigraphy:chaos_ingot"         // CHAOS   · METALLUM
  ],
  "pedestals": [ { "item": "minecraft:blaze_rod", "count": 4 } ],
  "input": { "item": "minecraft:netherite_ingot", "count": 1 },
  "fluid": "epigraphy:liquid_starlight",
  "fluid_amount": 1000,
  "conditions": [
    { "type": "epigraphy:weather", "value": "thunder" },
    { "type": "epigraphy:sky_visible", "value": true }
  ],
  "result": { "item": "epigraphy:chaos_ingot", "count": 1 },
  "duration_ticks": 200
}
```

The five rune words are exactly the five things the player must work out, and each is
a 2-word batch:

| Rune word | Glyphs | Names |
|---|---|---|
| `blackstone_altar` | ALTARE · TENEBRAE | Blackstone Altar |
| `blaze_rod` | FLAMMANS · VIRGA | Blaze Rod |
| `thunderstorm` | CAELUM · CHAOS | Thunderstorm |
| `netherite` | INFERNUS · METALLUM | Netherite |
| `chaos_ingot` | CHAOS · METALLUM | Chaos Ingot |

A player who has learned the words but decoded nothing sees *"Altar · Darkness /
Flaming · Rod / Heavens · Chaos / Hell · Metal / Chaos · Metal"* — genuinely
solvable, and each hunch is confirmed by submitting it in the codex.

**Supporting rune word definitions** (`RUNES.md` §3.1):
```jsonc
// data/epigraphy/rune_words/thunderstorm.json
{
  "glyphs": ["epigraphy:caelum", "epigraphy:chaos"],
  "means": { "type": "condition", "value": { "type": "epigraphy:weather", "value": "thunder" } },
  "reading": "When the heavens turn to chaos.",
  "hint": "A raging sky."
}
```
```jsonc
// data/epigraphy/rune_words/netherite.json
{
  "glyphs": ["epigraphy:infernus", "epigraphy:metallum"],
  "means": { "type": "item", "value": "minecraft:netherite_ingot" },
  "reading": "Metal born of hell.",
  "hint": "Hell's own metal."
}
```

### 5.2 Illuminated Stone (intro, T1) — teaches the system
```jsonc
// data/epigraphy/recipes/illuminated_stone.json
{
  "type": "epigraphy:infusion",
  "altar": "epigraphy:stone_altar",
  "rune_words": [
    "epigraphy:stone_altar",       // ALTARE · LAPIS
    "epigraphy:starlit_night",     // CAELUM · NOX
    "epigraphy:illuminated_stone"  // LAPIS  · CAELUM
  ],
  "pedestals": [ { "item": "minecraft:glowstone_dust", "count": 2 } ],
  "input": { "item": "minecraft:stone", "count": 1 },
  "fluid": "epigraphy:liquid_starlight",
  "fluid_amount": 250,
  "conditions": [
    { "type": "epigraphy:time", "value": "night" },
    { "type": "epigraphy:sky_visible", "value": true }
  ],
  "result": { "item": "epigraphy:illuminated_stone", "count": 1 },
  "duration_ticks": 100
}
```
A gentle first ritual with only three rune words, all built from **common** glyphs.
It's the tutorial for the whole language: `ALTARE · LAPIS` (Stone Altar) is the
player's first likely codex submit, and its success teaches that 2-glyph batches
name things.

### 5.3 A `moon_phase` example (shows a 3-glyph rune word)
```jsonc
// data/epigraphy/recipes/umbral_shard.json (sketch)
{
  "type": "epigraphy:infusion",
  "altar": "epigraphy:blackstone_altar",
  "rune_words": [
    "epigraphy:blackstone_altar",  // ALTARE   · TENEBRAE
    "epigraphy:dark_moon",         // NOX · TENEBRAE · CAELUM  (3-glyph rune word)
    "epigraphy:umbral_shard"       // TENEBRAE · LAPIS
  ],
  "pedestals": [ { "tag": "forge:gems/quartz", "count": 4 } ],
  "input": { "item": "minecraft:echo_shard", "count": 1 },
  "fluid": "epigraphy:liquid_starlight",
  "fluid_amount": 500,
  "conditions": [
    { "type": "epigraphy:moon_phase", "value": "new" },
    { "type": "epigraphy:time", "value": "night" }
  ],
  "result": { "item": "epigraphy:umbral_shard", "count": 1 },
  "duration_ticks": 160
}
```
`NOX · TENEBRAE · CAELUM` ("Night · Darkness · Heavens") is the 3-glyph form —
used when two words are too ambiguous to name a thing uniquely. Here two words
would only get you "a dark sky"; the third pins it to the **new moon**.

---

## 6. Blocks, items & fluids introduced

| Registry object | Type | Role |
|-----------------|------|------|
| `epigraphy:stone_altar` / `epigraphy:blackstone_altar` | Block (block-entity) | Ritual cores, T1/T2 |
| `epigraphy:pedestal` | Block (block-entity) | Holds one catalyst item |
| `epigraphy:liquid_starlight` | Fluid (+ bucket, block) | Infusion medium |
| `epigraphy:chaos_ingot` | Item | Flagship ritual output; crafting reagent |
| `epigraphy:illuminated_stone` | Block | Intro ritual output (light source) |
| `epigraphy:umbral_shard` | Item | Example dark-ritual output |
| `epigraphy:chaos_wisp` | Entity | Example backlash anomaly (moderate) |
| `epigraphy:chaos_scar` | Block/field | Example lingering corruption (severe backlash) |

---

## 7. Design notes

- **Position-agnostic pedestals (v1).** Matching pedestals as a multiset keeps the
  first version tractable and forgiving. A later version can add *patterned*
  rituals where pedestal geometry matters (a `pattern` field), for players who
  want the Thaumcraft-infusion-altar precision.
- **One-recipe determinism.** If two recipes match the same setup, that's an
  authoring bug; the altar refuses to fire and logs it. Conditions exist precisely
  so similar rituals disambiguate by weather/time/etc.
- **Obtaining ≠ only crafting.** The Tier-3 (master) hook watches *inventory
  acquisition* of ritual results, so trading, loot, or creative-giving the item
  also unlocks its reference entry (in-game documentation + JEI) — "you have held
  the thing, now you may read how it's made."
- **Glyphs stay out of the altar.** Per D5, you never place glyph tablets on
  pedestals; pedestals are for catalyst items only. Whether you *understand* the
  ritual is a property of your research, checked at trigger time.

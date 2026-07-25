# Rituals & Infusion

The back half of the loop: how a decoded **inscription** becomes an actual crafting
event. This document specifies the rite types, the altar multiblock, conditions, the
backlash system, and worked examples.

> **For the full JSON schemas and a step-by-step guide to adding content, see
> [`AUTHORING.md`](AUTHORING.md).** This document covers the *design*; that one is
> the reference.

**Not every rite needs an altar (D12).** A rite type is a grammar template plus a
trigger, and each is announced by its own **invocation glyph** — just as `D.M.`
opens a Roman funerary inscription and `I.O.M.` a votive one:

| Invocation | Rite | How it's performed | Batched? |
|---|---|---|---|
| **`OPUS`** | *The Work* | altar multiblock + pedestals + pool | no |
| **`MERSIO`** | *The Steeping* | throw items into a fluid in-world | **yes — full stacks** |
| **`TACTUS`** | *The Touch* | use one item on another item/block | no |
| **`VIGILIA`** | *The Vigil* | observe the sky through an Observatory | no |

`OPUS` is the heavyweight — precise, structural, one output at a time. `MERSIO` is
the everyday one: no structure at all, just items thrown together into water under
the right sky, and it processes **whole stacks in one go**. That spread matters for
pacing: players meet the language through cheap, batched steeping rites long before
they can build an altar.

Every recipe carries an inscription (`RUNES.md` §4), and the inscription *is* the
recipe expressed in the language the player is learning — the JSON is simply that
sentence plus the exact quantities they only earn at Tier 3.

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
| `epigraphy:dimension` | `minecraft:the_nether`, `minecraft:overworld` | Dimension of the rite |
| `epigraphy:sky_visible` | `true` / `false` | Whether the rite can see the sky |
| `epigraphy:biome` | biome id or tag | Biome at the rite |
| `epigraphy:y_level` | `min` / `max` | Altitude or depth band |
| `epigraphy:constellation` | constellation id + `visible` | A named constellation overhead |
| `epigraphy:light_level` | `min` / `max` | Block/sky light |
| `epigraphy:fluid_present` | fluid id | The fluid the rite sits in |
| `epigraphy:nearby_block` | block id/tag + `radius` | A block within range |

Any condition may be marked `"optional": true`, turning it from a gate into a
**bonus** — the intended way to author "same ingredients, better yield under rarer
skies" without duplicating recipes. Full catalogue and syntax in
[`AUTHORING.md`](AUTHORING.md) §6–7.

Condition types are an extensible registry: adding a new *kind* (say, "player is on
fire") is a small code addition, but the *recipes* that use them stay pure data.
This is the one place the design deliberately reserves room for code growth.

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

  // The ritual's INSCRIPTION: rune words in strict formula order (D10, RUNES.md §4)
  //   VESSEL / OFFERING / HOUR / SUBJECT / ISSUE
  // Order is mechanical, not presentational — a wrong order is a wrong inscription.
  // Drives (a) the hint text the player reads and (b) the research gate
  // (KNOWLEDGE.md §4).
  "inscription": {
    "vessel":   "epigraphy:blackstone_altar",  // ALTARE   · TENEBRAE
    "offering": "epigraphy:blaze_rod",         // FLAMMANS · VIRGA
    "hour":     "epigraphy:thunderstorm",      // CHAOS    · CAELUM
    "subject":  "epigraphy:netherite",         // INFERNUS · METALLUM
    "issue":    "epigraphy:chaos_ingot"        // CHAOS    · METALLUM
  },

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
   - **Decoded every word of the inscription** → **clean** run.
   - **Some words undecoded** → a **blind attempt**: it still fires (the build is
     physically correct) but triggers **backlash** (§4.1) scaled by how many
     words remain undecoded.
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
  "inscription": {
    "vessel":   "epigraphy:blackstone_altar",  // ALTARE   · TENEBRAE
    "offering": "epigraphy:blaze_rod",         // FLAMMANS · VIRGA
    "hour":     "epigraphy:thunderstorm",      // CHAOS    · CAELUM
    "subject":  "epigraphy:netherite",         // INFERNUS · METALLUM
    "issue":    "epigraphy:chaos_ingot"        // CHAOS    · METALLUM
  },
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

Read as a single inscription, in formula order:

```
ALTARE·TENEBRAE   FLAMMANS·VIRGA   CHAOS·CAELUM   INFERNUS·METALLUM  →  CHAOS·METALLUM
────────┬───────  ───────┬──────   ──────┬─────   ────────┬────────     ───────┬──────
  VESSEL            OFFERING          HOUR           SUBJECT              ISSUE
Blackstone Altar    Blaze Rod      Thunderstorm      Netherite         Chaos Ingot
```

| Clause | Rune word | Glyphs | Names |
|---|---|---|---|
| VESSEL | `blackstone_altar` | ALTARE · TENEBRAE | Blackstone Altar |
| OFFERING | `blaze_rod` | FLAMMANS · VIRGA | Blaze Rod |
| HOUR | `thunderstorm` | CHAOS · CAELUM | Thunderstorm |
| SUBJECT | `netherite` | INFERNUS · METALLUM | Netherite |
| ISSUE | `chaos_ingot` | CHAOS · METALLUM | Chaos Ingot |

A player who has learned the words but decoded nothing sees *"Altar · Darkness /
Flaming · Rod / Chaos · Heavens / Hell · Metal / Chaos · Metal"* — genuinely
solvable, and made more so by the formula: they know the third word must be a
*condition* before they've decoded a single glyph of it.

**Supporting rune word definitions** (`RUNES.md` §3.1):
```jsonc
// data/epigraphy/rune_words/thunderstorm.json
{
  "glyphs": ["epigraphy:chaos", "epigraphy:caelum"],   // QUALIFIER · HEAD, ordered
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
  "inscription": {
    "vessel":   "epigraphy:stone_altar",         // ALTARE   · LAPIS
    "offering": "epigraphy:glowstone",           // FLAMMANS · LAPIS
    "hour":     "epigraphy:starlit_night",       // NOX      · CAELUM
    "subject":  "epigraphy:deepslate",           // TENEBRAE · LAPIS
    "issue":    "epigraphy:illuminated_stone"    // CAELUM   · LAPIS
  },
  "pedestals": [ { "item": "minecraft:glowstone_dust", "count": 2 } ],
  "input": { "item": "minecraft:deepslate", "count": 1 },
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

The tutorial ritual, and deliberately built to **teach the grammar**. Four of its
five words share the head `LAPIS`:

```
ALTARE·LAPIS    FLAMMANS·LAPIS   NOX·CAELUM    TENEBRAE·LAPIS  →  CAELUM·LAPIS
 Stone Altar      Glowstone      Starlit Night   Deepslate       Illuminated Stone
```

Once a player decodes any one of these, the pattern is visible: *"…· LAPIS names a
kind of stone."* Their next guesses aren't shots in the dark — they're informed by
the rule they just inferred. That's the whole language taught in one ritual,
without a tutorial popup.

### 5.3 A `moon_phase` example (shows a 3-glyph rune word)
```jsonc
// data/epigraphy/recipes/umbral_shard.json (sketch)
{
  "type": "epigraphy:infusion",
  "altar": "epigraphy:blackstone_altar",
  "inscription": {
    "vessel":   "epigraphy:blackstone_altar",  // ALTARE   · TENEBRAE
    "offering": "epigraphy:quartz",            // INFERNUS · LAPIS  ("hell's stone")
    "hour":     "epigraphy:dark_moon",         // NOX · TENEBRAE · CAELUM  (3-glyph)
    "subject":  "epigraphy:echo_shard",        // NOX      · LAPIS
    "issue":    "epigraphy:umbral_shard"       // CHAOS    · TENEBRAE
  },
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
used when two glyphs are too ambiguous to name a thing uniquely. Here two would only
get you "a dark sky"; the third pins it to the **new moon**.

> **Authoring constraint worth flagging early.** Every ingredient, condition, and
> output in a ritual needs its own rune word, and **no two rune words may share the
> same ordered glyph sequence** (submit validation must be deterministic). With a
> ~10-glyph starter lexicon this gets tight fast — writing these three examples
> already forced `INFERNUS · LAPIS` for quartz and `NOX · LAPIS` for echo shards to
> avoid colliding with `TENEBRAE · LAPIS` (deepslate). Two implications:
> 1. **Ingredients are chosen partly for nameability.** A recipe wanting an item
>    with no natural 2-glyph name is a signal to pick a different ingredient or add
>    a glyph.
> 2. **The lexicon must grow alongside the recipe list.** Budget roughly one new
>    glyph per handful of new rituals; the load-time validator (`ROADMAP.md` §5)
>    catches collisions, but the *design* pressure shows up before the validator does.

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

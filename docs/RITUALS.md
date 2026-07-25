# Rituals & Infusion

The back half of the loop: the altar, the pedestals, the infusion fluid, and the
environmental conditions that turn a glyph sentence into an actual crafting event.
This document specifies the multiblock, the full JSON recipe schema, and worked
examples.

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

  // The glyph "sentence." Drives (a) the Tier-2 instruction text and
  // (b) the knowledge gate (see KNOWLEDGE.md §4). Order is read left→right.
  "glyphs": ["epigraphy:altare", "epigraphy:flammans",
             "epigraphy:caelum", "epigraphy:metallum", "epigraphy:chaos"],

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
   the current world/position.
2. Filter recipes to those whose `altar`, `pedestals`, `input`, `fluid`,
   `fluid_amount`, and required `conditions` all match.
3. Apply the **knowledge gate** (`KNOWLEDGE.md` §4): does this player know the
   ritual's glyphs well enough to attempt it? If not, the altar sputters with a
   "the symbols mean nothing to you yet" message.
4. If exactly one recipe matches, run it: consume fluid/pedestal items over
   `duration_ticks` with particles, then consume the input and spawn `result`.
5. On success, promote the player's ritual page to **Tier 3** and record the
   result item's recipe to JEI (obtaining the item does the same via an inventory
   hook, so buying/trading the item also unlocks the recipe).

---

## 5. Worked examples

### 5.1 Chaos Ingot (flagship, T2)
```jsonc
// data/epigraphy/recipes/chaos_ingot.json
{
  "type": "epigraphy:infusion",
  "altar": "epigraphy:blackstone_altar",
  "glyphs": ["epigraphy:altare", "epigraphy:flammans",
             "epigraphy:caelum", "epigraphy:metallum", "epigraphy:chaos"],
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
Reads as: **Upon the Altar, that which Flames — beneath the raging Heavens —
quench Metal into Chaos.** The thunderstorm + sky-visible conditions are the
"Heavens' fury"; blaze rods are "that which flames"; netherite is "metal."

### 5.2 Illuminated Stone (intro, T1) — teaches the system
```jsonc
// data/epigraphy/recipes/illuminated_stone.json
{
  "type": "epigraphy:infusion",
  "altar": "epigraphy:stone_altar",
  "glyphs": ["epigraphy:altare", "epigraphy:lapis", "epigraphy:caelum"],
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
A gentle first ritual: any player who has liquid starlight and can read `ALTARE ·
LAPIS · CAELUM` can make a light-emitting stone at night. It exists to teach
pedestals + fluid + a single condition before Chaos Ingot demands a thunderstorm.

### 5.3 A `moon_phase` example (shows the condition breadth)
```jsonc
// data/epigraphy/recipes/umbral_shard.json (sketch)
{
  "type": "epigraphy:infusion",
  "altar": "epigraphy:blackstone_altar",
  "glyphs": ["epigraphy:altare", "epigraphy:tenebrae", "epigraphy:nox"],
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

---

## 7. Design notes

- **Position-agnostic pedestals (v1).** Matching pedestals as a multiset keeps the
  first version tractable and forgiving. A later version can add *patterned*
  rituals where pedestal geometry matters (a `pattern` field), for players who
  want the Thaumcraft-infusion-altar precision.
- **One-recipe determinism.** If two recipes match the same setup, that's an
  authoring bug; the altar refuses to fire and logs it. Conditions exist precisely
  so similar rituals disambiguate by weather/time/etc.
- **Obtaining ≠ only crafting.** The Tier-3 unlock hook watches *inventory
  acquisition* of ritual results, so trading, loot, or creative-giving the item
  also unlocks its JEI page — "you have held the thing, now you may read how it's
  made."

# Epigraphy — Design Document

> A Forge 1.20.1 magic mod about *reading the world*. Discover ancient glyphs
> hidden in the stars, ruins, and structures; translate a lost written language;
> and perform infusion rituals whose recipes read like sentences.

Inspired by **Astral Sorcery** (skyward discovery, altars, constellations) and
**Thaumcraft** (research, aspects, infusion), rebuilt around one central idea:

**You do not unlock progression by grinding — you unlock it by *learning to read*.**

---

## 1. Design pillars

1. **Discovery is the gameplay.** The mod's primary loop is *finding* glyphs the
   world does not point you toward. Symbols are hidden in worldgen and in the
   sky the way secret items are tucked into a Risk of Rain level — you have to
   actually look.

2. **A real, learnable language.** Glyphs are not abstract skill-tree nodes.
   Each glyph is a symbol that maps to a Latin word (`ALTARE`, `TENEBRAE`,
   `FLAMMANS`, `VIRGA`, `CHAOS`, `CAELUM`, `INFERNUS`, `METALLUM`, …). Rituals
   are *phrases* in that language. Once you can read, the world tells you what to
   do in its own words.

3. **Knowing ≠ having.** Learning a glyph teaches you the *instructions* (flavor
   text, translated hints). It does **not** hand you the recipe. The precise,
   JEI-browsable recipe only appears after you have actually *obtained the item*
   or *performed the ritual* once. Reading is the map; doing is the key.

4. **The world is the tech tree.** Weather, time of day, moon phase, sky access,
   dimension, and nearby blocks are ritual ingredients. Progression is gated by
   observation and timing, not by opaque XP bars.

5. **Modern, legible, data-driven.** Everything — glyphs, translations, rituals,
   loot — is defined in JSON datapacks so the systems are extensible by us and by
   pack makers, and so the design stays honest about what the code must support.

---

## 2. The core loop

```
        ┌────────────────────────────────────────────────────────────┐
        │                                                            │
        ▼                                                            │
  ┌───────────┐   record    ┌───────────┐   translate   ┌──────────────┐
  │  SIGHT a  │───────────▶ │  KNOW the │─────────────▶ │  READ the    │
  │  glyph in │   (Codex)   │  glyph's  │  (cross-ref / │  glyph as    │
  │ the world │             │  SHAPE    │   Rosetta)    │  flavor text │
  └───────────┘             └───────────┘               └──────┬───────┘
        ▲                                                       │
        │                                                       │ instructions
        │ new sightings                                         │ hint at a ritual
        │ point to new glyphs                                   ▼
  ┌───────────┐                                          ┌──────────────┐
  │ EXPLORE / │◀─────────────────────────────────────────│  PERFORM the │
  │ fight for │        reward: tablets, ingredients        │  ritual /    │
  │  tablets  │                                            │  OBTAIN item │
  └───────────┘                                            └──────┬───────┘
                                                                  │ unlocks
                                                                  ▼
                                                          ┌──────────────┐
                                                          │  RECIPE now  │
                                                          │  shown in    │
                                                          │  JEI         │
                                                          └──────────────┘
```

The loop is deliberately front-loaded on *looking*, and back-loaded on
*confirmation*. You spend the early game unable to read; the mid game learning
to read and guessing; the late game fluent and efficient (JEI-assisted).

---

## 3. The three tiers of knowing

This is the single most important mechanic and the one the code must model
precisely. For every glyph and every recipe, the player's knowledge is at one of
three tiers:

| Tier | Name | How you reach it | What it gives you |
|-----:|------|------------------|-------------------|
| 0 | **Unknown** | default | Nothing. The glyph is a meaningless shape. |
| 1 | **Sighted / Known** | Record a glyph you found in worldgen, the sky, or a dropped tablet | You know the glyph's *shape* exists and can copy it. Its Latin meaning is still `???`. |
| 2 | **Translated** | Cross-reference enough sightings, or apply a Rosetta tablet | The glyph is now *readable*. Wherever it appears, it renders as translated flavor text. Ritual pages that use it become legible **instructions**. |
| 3 | **Unlocked (JEI)** | *Obtain* the output item, or *perform* the ritual once | The exact recipe — pedestal items, fluid, conditions — is now stored and browsable in JEI. |

Key rules the systems enforce:

- **Tier 2 tells you *what and roughly how*, never the exact bill of materials.**
  A translated ritual page reads like: *"Upon the **Altar**, offer that which
  **Flames**; call down the **Heavens'** fury and quench **Metal** in **Chaos**."*
  That's enough for a player to reason out "Blackstone altar + blaze rods +
  thunderstorm + netherite," but the game never spells out counts or exact items
  until Tier 3.
- **Tier 3 is earned by doing, and it is per-recipe.** You can be fluent (Tier 2
  on every glyph) and still have an empty JEI, because JEI entries are trophies
  for things you have actually made.
- **Tiers are per-player**, persisted via a Forge capability attached to the
  player and synced to the client for rendering and the codex UI.

See [`KNOWLEDGE.md`](KNOWLEDGE.md) for the data model and progression details.

---

## 4. Systems overview

Each system has its own document; this section is the map.

### 4.1 Glyphs & the language — [`GLYPHS.md`](GLYPHS.md)
The lexicon. Every glyph is a datapack entry with an id, a Latin lemma, an
English gloss, a category (element / place / action / material / celestial), and
art. Rituals reference glyphs by id. This doc also defines how "sentences" are
formed and how translation flavor text is assembled.

### 4.2 Discovery & worldgen — [`DISCOVERY.md`](DISCOVERY.md)
Where glyphs hide and how you record them: carvings on ruined structures,
constellation-glyphs observed through a telescope/observatory at night, and
inscribed tablets dropped by mobs and bosses. Covers the Codex item, the
"record" action, and how translation is earned.

### 4.3 Rituals & infusion — [`RITUALS.md`](RITUALS.md)
The altar, the pedestals, the infusion fluid, and the environmental conditions
(weather, time, moon, sky, dimension). Full JSON recipe schema and worked
examples, including the **Chaos Ingot**.

### 4.4 Knowledge & JEI gating — [`KNOWLEDGE.md`](KNOWLEDGE.md)
The capability that stores per-player tiers, how it syncs, and exactly how the
JEI plugin hides recipes until Tier 3.

### 4.5 Roadmap & architecture — [`ROADMAP.md`](ROADMAP.md)
Package layout, registry plan, dependencies, and a phased build order from
"empty project that compiles" to "full loop."

---

## 5. A worked example: the Chaos Ingot

To ground everything, here is the flagship early-mid ritual, traced through all
three tiers. This is the example you gave, formalized.

**The ritual, as a glyph sentence:**

```
ALTARE · FLAMMANS · CAELUM · METALLUM · CHAOS
(Altar)  (Flaming)  (Heavens) (Metal)   (Chaos)
```

**Tier 0 → 1 (Sighted).** The player finds the `CHAOS` glyph carved into a
blackened ruin in the Nether, `CAELUM` as a constellation on a clear night, and
receives a `FLAMMANS` tablet from a Blaze. They record each into the Codex.

**Tier 1 → 2 (Translated).** After enough sightings and a Rosetta fragment, the
glyphs become readable. The ritual page — itself found on a tablet or unlocked by
knowing its component glyphs — now reads:

> *Raise a **Blackstone Altar**. Set upon its pedestals that which **flames** in
> the deep. When the **Heavens** rage with storm, cast **Metal** born of the
> nether into the pool, and it shall be remade as **Chaos**.*

The player now understands the shape of the task but has no JEI entry.

**Tier 2 → 3 (Unlocked).** The player builds a Blackstone Altar, places blaze
rods on the pedestals, submerges a netherite ingot in the infusion fluid, and
waits for a thunderstorm. The ritual fires; a **Chaos Ingot** is produced. The
moment it enters their inventory, the full recipe is written to their JEI.

**The JSON that defines it** (see [`RITUALS.md`](RITUALS.md) for the schema):

```jsonc
// data/epigraphy/rituals/chaos_ingot.json
{
  "type": "epigraphy:infusion",
  "altar": "epigraphy:blackstone_altar",
  "glyphs": ["epigraphy:altare", "epigraphy:flammans",
             "epigraphy:caelum", "epigraphy:metallum", "epigraphy:chaos"],
  "pedestals": [
    { "item": "minecraft:blaze_rod", "count": 4 }
  ],
  "input": { "item": "minecraft:netherite_ingot" },
  "fluid": "epigraphy:liquid_starlight",   // placeholder infusion fluid
  "conditions": [
    { "type": "epigraphy:weather", "value": "thunder" },
    { "type": "epigraphy:dimension", "value": "minecraft:the_nether", "optional": true }
  ],
  "result": { "item": "epigraphy:chaos_ingot", "count": 1 },
  "duration_ticks": 200
}
```

Notice the `glyphs` array doubles as the Tier-2 instruction generator *and* the
knowledge gate (you must have sighted, though not necessarily translated, the
listed glyphs for the ritual to be attemptable at all — a tunable rule described
in `KNOWLEDGE.md`).

---

## 6. Non-goals (for now)

- No custom dimension in v1. The overworld sky, the Nether, and vanilla weather
  give us enough "conditions" to build the whole loop.
- No skill points, mana bars, or numeric levels. Knowledge is categorical
  (the three tiers), never a number to grind.
- No hand-holding quest book that marks glyph locations. Discovery must stay
  discovery; the Codex records what *you* found, it never points at what you
  haven't.

---

## 7. Document index

| Doc | Contents |
|-----|----------|
| [`DESIGN.md`](DESIGN.md) | This file — vision, pillars, core loop, worked example |
| [`GLYPHS.md`](GLYPHS.md) | The glyph language: lexicon, categories, sentences, translation text |
| [`DISCOVERY.md`](DISCOVERY.md) | Finding & recording glyphs; worldgen, sky, tablets, the Codex |
| [`RITUALS.md`](RITUALS.md) | Altar, pedestals, fluid, conditions; JSON schema & examples |
| [`KNOWLEDGE.md`](KNOWLEDGE.md) | Per-player knowledge capability, tiers, sync, JEI gating |
| [`ROADMAP.md`](ROADMAP.md) | Architecture, package layout, dependencies, phased build order |

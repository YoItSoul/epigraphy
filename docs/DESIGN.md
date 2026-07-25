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

2. **A real language, with a real grammar.** Glyphs are not abstract skill-tree
   nodes. Each is a symbol mapping to a Latin word (`ALTARE`, `TENEBRAE`,
   `FLAMMANS`, `VIRGA`, `CHAOS`, `CAELUM`, `INFERNUS`, `METALLUM`, …), and **2–3
   glyphs *in order* form a rune word naming one concrete thing**: `FLAMMANS ·
   VIRGA` → Blaze Rod. Those words then fill a fixed **inscription formula** —
   `VESSEL / OFFERING / HOUR / SUBJECT / ISSUE` — so a ritual is one readable
   sentence. Like real epigraphy, the pattern is the point: once you know the
   formula, you can approach an inscription you've never seen and know what each
   part *is* before you know what it *says*.

3. **Knowing ≠ having.** Learning a glyph teaches you the *instructions* (flavor
   text, translated hints). It does **not** hand you the recipe. The precise,
   JEI-browsable recipe only appears after you have actually *obtained the item*
   or *performed the ritual* once. Reading is the map; doing is the key.

4. **The world is the tech tree.** Weather, time of day, moon phase, sky access,
   dimension, and nearby blocks are ritual ingredients. Progression is gated by
   observation and timing, not by opaque XP bars.

5. **No GUI — the world is the interface.** Every *action* happens in-world or
   on-item: you record a carving by using an item on it, you run a ritual by
   building it and watching it transform. The mod adds **no gameplay screens**. The
   only permitted screens are a **read-only reference layer** (an in-game
   documentation guide + optional JEI) that merely *remembers* what you've already
   done — it never stands between you and doing it. (See `DECISIONS.md` D1/D6.)

6. **Glyphs are research, not reagents.** Learning a glyph is Thaumcraft-1.7.10-style
   *research* — permanent, per-player knowledge that unlocks your *understanding* of
   the recipes and systems using it — earned by discovering and reading the world
   rather than by a minigame. You never place glyphs at the altar; your research is
   checked from your knowledge when a ritual fires. (`DECISIONS.md` D5.)

7. **Modern, legible, data-driven.** Everything — glyphs, translations, rituals,
   loot — is defined in JSON datapacks so the systems are extensible by us and by
   pack makers, and so the design stays honest about what the code must support.

---

## 2. The core loop

```
   ┌──────────────────────────────────────────────────────────────────┐
   │                                                                  │
   ▼                                                                  │
┌───────────┐  record   ┌───────────┐ triangulate ┌──────────────┐    │
│  SIGHT a  │─────────▶ │  KNOW the │───────────▶ │  LEARN the   │    │
│  glyph in │ (rubbing) │  glyph's  │ (sightings/ │  word it     │    │
│ the world │           │   SHAPE   │   Rosetta)  │  stands for  │    │
└───────────┘           └───────────┘             └──────┬───────┘    │
   ▲  ▲                                                  │            │
   │  │ seek mode points                                 │ words can  │
   │  │ toward unlearned                                 │ now be     │
   │  │                                                  ▼ combined   │
   │  │                                          ┌──────────────────┐ │
   │  │                                          │ SUBMIT a 2-3     │ │
   │  │                                          │ glyph RUNE WORD  │ │
   │  │                                          │ in the codex     │ │
   │  │                                          └──────┬───────────┘ │
   │  │                                                 │ decoded:    │
   │  │                                                 │ names a     │
   │  │                                                 ▼ thing       │
┌──┴──────────┐                                  ┌──────────────────┐ │
│  EXPLORE /  │◀─────────────────────────────────│  PERFORM the     │ │
│  fight for  │    reward: tablets, ingredients  │  ritual /        │─┘
│   tablets   │                                  │  OBTAIN the item │
└─────────────┘                                  └──────┬───────────┘
                                                        │ masters
                                                        ▼
                                                ┌──────────────────┐
                                                │  EXACT RECIPE    │
                                                │  in codex + JEI  │
                                                └──────────────────┘
```

The loop is front-loaded on *looking*, middled on *reasoning*, and back-loaded on
*confirmation*. Early game you can't read; mid game you're learning words and
guessing at what they build; late game you're fluent and efficient.

Note the two distinct learning verbs, which is what keeps the middle interesting:
**learning a word is passive** (sight it enough times), but **decoding what words
build is active** (hypothesise a rune word, submit it, find out).

---

## 3. The three tiers of knowing

This is the single most important mechanic and the one the code must model
precisely. For every glyph and every recipe, the player's knowledge is at one of
three tiers:

| Tier | Name | How you reach it | What it gives you |
|-----:|------|------------------|-------------------|
| 0 | **Unknown** | default | Nothing. The glyph is a meaningless shape. |
| 1 | **Sighted** | Record a glyph you found in worldgen, the sky, or a dropped tablet | You know the glyph's *shape* exists and can copy it. Its Latin meaning is still `???`. |
| 2 | **Learned** | Triangulate enough independent sightings, or apply a Rosetta tablet | Research complete: the glyph is *readable* everywhere, and you now *understand* the recipes/systems that use it — legible **instructions**, never exact amounts. |
| 3 | **Mastered** | *Obtain* the output item, or *perform* the ritual once | The exact recipe — pedestal items, fluid, conditions — is written into the in-game documentation and JEI. |

Key rules the systems enforce:

- **Tier 2 tells you *what and roughly how*, never the exact bill of materials.**
  A translated ritual page reads like: *"Upon the **Altar**, offer that which
  **Flames**; call down the **Heavens'** fury and quench **Metal** in **Chaos**."*
  That's enough for a player to reason out "Blackstone altar + blaze rods +
  thunderstorm + netherite," but the game never spells out counts or exact items
  until Tier 3.
- **Tier 3 is earned by doing, and it is per-recipe.** You can be fluent (Tier 2
  on every glyph) and still have an empty reference layer, because documentation/JEI
  entries are trophies for things you have actually made.
- **Attempting before you understand is possible — and dangerous.** You can copy a
  ritual's physical build without having *learned* its glyphs, but such **blind
  attempts** trigger **backlash** (anomalies, corruption, lost materials) scaled by
  how much you don't yet understand. The incentive is always: research first.
  (`KNOWLEDGE.md` §5.)
- **Tiers are per-player**, persisted via a Forge capability attached to the
  player and synced to the client. Knowledge is surfaced in-world (readable
  carvings), on-item (tooltips), and in the read-only reference layer — never a
  gameplay menu.

See [`KNOWLEDGE.md`](KNOWLEDGE.md) for the data model and progression details.

---

## 4. Systems overview

Each system has its own document; this section is the map.

### 4.1 Glyphs & the language — [`RUNES.md`](RUNES.md)
The lexicon. Every glyph is a datapack entry with an id, a Latin lemma, an
English gloss, a category (element / place / action / material / celestial), and
art. Rituals reference glyphs by id. This doc also defines how "sentences" are
formed and how translation flavor text is assembled.

### 4.2 Discovery & worldgen — [`DISCOVERY.md`](DISCOVERY.md)
Where glyphs hide and how you record them: carvings on ruined structures,
constellation-glyphs observed through an observatory at night, and inscribed
tablets dropped by mobs and bosses. Covers the in-world/on-item record action (no
screen) and how research (learning glyphs) is earned.

### 4.3 Rituals & infusion — [`RITUALS.md`](RITUALS.md)
The altar, the pedestals, the infusion fluid, and the environmental conditions
(weather, time, moon, sky, dimension). Full JSON recipe schema and worked
examples, including the **Chaos Ingot**.

### 4.4 Knowledge, research & gating — [`KNOWLEDGE.md`](KNOWLEDGE.md)
The capability that stores per-player research/tiers, how it syncs, the backlash
system for blind attempts, and how the reference layer (in-game documentation +
JEI) hides recipes until Tier 3.

### 4.5 Roadmap & architecture — [`ROADMAP.md`](ROADMAP.md)
Package layout, registry plan, dependencies, and a phased build order from
"empty project that compiles" to "full loop."

---

## 5. A worked example: the Chaos Ingot

To ground everything, here is the flagship early-mid ritual, traced through all
three tiers. This is the example you gave, formalized.

**The ritual, as one inscription in the formula:**

```
ALTARE·TENEBRAE   FLAMMANS·VIRGA   CHAOS·CAELUM   INFERNUS·METALLUM  →  CHAOS·METALLUM
────────┬───────  ───────┬──────   ──────┬─────   ────────┬────────     ───────┬──────
  VESSEL            OFFERING          HOUR           SUBJECT              ISSUE
Blackstone Altar    Blaze Rod      Thunderstorm      Netherite         Chaos Ingot
```

Five clauses, five things to work out — and the formula tells you what *kind* of
thing each one is before you can read any of them.

**Tier 0 → 1 (Sighted).** The player finds `CHAOS` carved into a blackened ruin in
the Nether, reads `CAELUM` as a constellation on a clear night, and gets a
`FLAMMANS` tablet off a Blaze. A charcoal rubbing on each carving (and studying the
tablet at a lectern) records an independent sighting — all in-world, no screen.
When they run dry, **seek mode** on the codex points them toward the nearest
structure holding a glyph they haven't learned.

**Tier 1 → 2 (Learned).** Enough independent sightings (or a Rosetta) and the words
become readable. The inscription now reads as literal glosses, still in formula
order:

```
  VESSEL           OFFERING         HOUR            SUBJECT           ISSUE
Altar·Darkness   Flaming·Rod    Chaos·Heavens    Hell·Metal       Chaos·Metal
```

This is the good part: that's **solvable**, and doubly so because of the grammar.
"Flaming Rod" is clearly a blaze rod. "Chaos Heavens" sits in the HOUR slot, so it
must be *weather* — a storm. And `· METALLUM` ending two different words tells the
player, unprompted, that `METALLUM` is the head for metals.

**Decoding (the active step).** They open the codex, inscribe `FLAMMANS · VIRGA` in
that order, and **submit**. It matches — the word permanently decodes to **Blaze
Rod** and reads that way everywhere. They repeat for the other four; with 20 slots
they can lay out the whole inscription and test every clause at once. Each correct
guess sharpens the ritual from poetry into a plan. (Wrong guesses cost nothing but
confirm nothing — and `VIRGA · FLAMMANS`, reversed, is simply not a word.)

They could *attempt* the ritual before decoding everything — but every undecoded
clause raises the **backlash** risk if they do.

**Tier 2 → 3 (Mastered).** The player builds a Blackstone Altar, rings it with
blaze rods, submerges a netherite ingot in the infusion fluid, and waits for a
thunderstorm. The ritual fires in-world; a **Chaos Ingot** is produced. The moment
it enters their inventory, the *exact* recipe — counts, amounts, conditions — is
written into the codex reference and JEI.

**The JSON that defines it** (see [`RITUALS.md`](RITUALS.md) for the schema):

```jsonc
// data/epigraphy/recipes/chaos_ingot.json
{
  "type": "epigraphy:infusion",
  "altar": "epigraphy:blackstone_altar",

  // The inscription — one rune word per clause, in formula order.
  "inscription": {
    "vessel":   "epigraphy:blackstone_altar",  // ALTARE   · TENEBRAE
    "offering": "epigraphy:blaze_rod",         // FLAMMANS · VIRGA
    "hour":     "epigraphy:thunderstorm",      // CHAOS    · CAELUM
    "subject":  "epigraphy:netherite",         // INFERNUS · METALLUM
    "issue":    "epigraphy:chaos_ingot"        // CHAOS    · METALLUM
  },

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

The `inscription` is the same recipe stated twice: once in the player's language,
once in exact quantities. The former is what they can read at Tier 2; the latter is
what they earn at Tier 3. It doubles as the research gate — decoding every clause
makes the run *clean*, while attempting it with clauses undecoded still fires (if
the physical build is right) but incurs backlash (`KNOWLEDGE.md` §4).

---

## 6. Non-goals (for now)

- No custom dimension in v1. The overworld sky, the Nether, and vanilla weather
  give us enough "conditions" to build the whole loop.
- No skill points, mana bars, or numeric levels. Knowledge is categorical
  (the three tiers), never a number to grind.
- No hand-holding quest book that marks glyph locations. Discovery must stay
  discovery; your record reflects what *you* found, it never points at what you
  haven't.
- No gameplay GUIs. Actions are in-world/on-item; the only screens are the
  read-only reference layer (`DECISIONS.md` D1/D6).

---

## 7. Document index

| Doc | Contents |
|-----|----------|
| [`DECISIONS.md`](DECISIONS.md) | The decisions log — settled choices (source of truth) & open questions |
| [`DESIGN.md`](DESIGN.md) | This file — vision, pillars, core loop, worked example |
| [`RUNES.md`](RUNES.md) | The glyph language: lexicon, categories, sentences, translation text |
| [`DISCOVERY.md`](DISCOVERY.md) | Finding & recording glyphs (in-world/on-item); worldgen, sky, tablets |
| [`RITUALS.md`](RITUALS.md) | Rite types, altar multiblock, fluid, conditions, backlash |
| [`AUTHORING.md`](AUTHORING.md) | Modder/datapack guide: schemas for glyphs, rune words, rite types, recipes |
| [`KNOWLEDGE.md`](KNOWLEDGE.md) | Per-player research/knowledge capability, tiers, backlash, reference gating |
| [`ROADMAP.md`](ROADMAP.md) | Architecture, package layout, dependencies, phased build order |

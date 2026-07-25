# Authoring Guide — Glyphs, Rune Words, Rites & Recipes

Everything in Epigraphy's language and content layer is **datapack-defined**. This
guide is for us and for modders/pack-makers: how to add a glyph, coin a rune word,
define a new *kind* of rite, and write a recipe.

**The modding contract in one line:** *glyphs, rune words, rite types, recipes, and
worldgen distribution are all JSON; only genuinely new **condition kinds** and new
**trigger mechanics** require Java.*

---

## 1. The four data layers

| Layer | Directory | What it declares | Needs code? |
|---|---|---|---|
| **Glyph** | `data/<ns>/glyphs/` | one symbol + its Latin word | No |
| **Rune word** | `data/<ns>/rune_words/` | an ordered 2–3 glyph sequence naming one thing | No |
| **Rite type** | `data/<ns>/rite_types/` | a grammar template + how a rite is triggered | No* |
| **Recipe** | `data/<ns>/recipes/` | one concrete rite: inscription + exact amounts | No |

\* A rite type composes *existing* trigger mechanics. Inventing a wholly new trigger
(e.g. "when struck by lightning") is a small Java addition; see §6.

Everything is namespaced, so `examplemod:` content coexists with `epigraphy:`
content and can reference it freely.

---

## 2. Adding a glyph

A glyph is one symbol. Keep them **broad** — glyphs are meant to recombine.

```jsonc
// data/examplemod/glyphs/pulvis.json
{
  "lemma": "PULVIS",              // the Latin word; what a fluent player reads
  "gloss": "Dust",                // one-word English meaning, shown once learned
  "category": "material",         // material | element | place | celestial | frame
  "rarity": "common",             // common | uncommon | rare — biases where it hides
  "sightings_to_learn": 1,        // independent sightings needed to learn it

  // Determinative role (RUNES.md §4.3). Omit for plain qualifier glyphs.
  // Also selects the FRAME the glyph is drawn in (§2.1).
  "determinative": {
    "class": "material",          // material | celestial | structure
    "position": "suffix"          // suffix | prefix
  },

  "description": "That which is ground down, and so made ready."

  // "texture": "examplemod:glyph/pulvis"   // OPTIONAL — see §2.1. Omit and the
  //                                        // art is generated for you.
}
```

### 2.1 Glyph art is generated — you usually write no texture

Glyphs are drawn as **two marks + frame** (`GLYPH_SPEC.md`), and both derive from fields
you have already written:

| Layer | Derived from | Result |
|---|---|---|
| **Marks** (interior) | `lemma` | its first two letters, drawn as two bold arms on a stem; each arm's width and shape identify one letter |
| **Frame** (border) | `determinative.class`, or `category` if absent | hexagon / circle / plinth / basin / open base / doubled ring |

So `"lemma": "PULVIS"` + `"determinative": {"class":"material"}` yields a
hex-framed `PV` mark with no art file at all. **This is the intended path** — add a
glyph in JSON, get usable art immediately.

Supply `texture` only to override generation for a glyph worth hand-drawing (a
boss-tier glyph, a mod's signature symbol). A supplied texture replaces the whole
composite, frame included, so hand-drawn glyphs must draw their own frame to stay
readable in a line of inscription.

**Frame selection rules:**

| `category` | Frame | May be a determinative? |
|---|---|---|
| `material` | hexagon | yes |
| `celestial` | circle | yes |
| `place` | plinth | yes |
| `fluid` | basin | yes |
| `element` | **open base** | **no** — qualifiers are never heads |
| `frame` | doubled ring | n/a — clause markers only |

The element restriction is enforced: declaring a `determinative` block on an
`element` glyph fails validation. That rule is why the element frame is drawn open
rather than enclosing — the shape *is* the rule.

> **Two glyphs in the same class may not share their first two letters.** That is the
> collision that actually fires — `VIRGA`/`VITA`/`VIGILIA` are fine because their
> classes differ, but a second `VI` *material* is not. Fix it with an explicit
> two-letter `mark` field: `{ "lemma": "VIRIDIS", "mark": "VR" }`.
>
> **Adding a new frame is not a free action** either. Frames must be vertically
> symmetric and keep straight sides across the mark band, and adding one **requires
> re-running the letter-distinctness audit** (`GLYPH_SPEC.md` §8).

### Choosing a good glyph

- **Broad beats specific.** `PULVIS` (Dust) is a great glyph because it can head
  *many* words — bone meal, sugar, glowstone dust, redstone. A glyph meaning
  "bone meal" specifically would be a bad glyph; that's a *rune word's* job.
- **Decide if it's a determinative.** If it names a *category of thing*
  (metal, stone, rod, powder, altar), give it a `determinative` block — it becomes a
  head. If it names a *quality* (flaming, dark, chaotic, sweet), leave it off — it's
  a qualifier.
- **Prefix vs suffix.** Follow the convention: **structures/places prefix**
  (`ALTARE · …`), **materials/objects suffix** (`… · PULVIS`). Both are attested in
  real determinative systems (`RUNES.md` §4.3).
- **Rarity drives discovery.** `rare` glyphs appear in dangerous structures and boss
  loot, and gate later-tier content.

---

## 3. Coining a rune word

A rune word is an **ordered** 2–3 glyph sequence naming exactly one concrete thing.

```jsonc
// data/examplemod/rune_words/bone_meal.json
{
  // ORDERED. Sequence is the word's identity — the reverse is not this word.
  "glyphs": ["examplemod:ossa", "examplemod:pulvis"],

  // What it names. One of: item | tag | block | fluid | condition
  "means": { "type": "item", "value": "minecraft:bone_meal" },

  "reading": "Dust ground from bone.",   // flavour, shown once decoded
  "hint": "A dust of bones."             // terse form for in-world carvings
}
```

### The three hard rules

1. **2 or 3 glyphs.** Never 1, never 4.
2. **Contains at least one determinative.** The **head** is the *last*
   determinative-capable glyph in the word; everything before it qualifies
   (`RUNES.md` §4.3.1). A word made only of element/quality glyphs has no head and
   fails validation.
3. **Ordered sequences must be globally unique.** No two rune words may share the
   same glyph sequence, in any namespace. This is what makes codex submission
   deterministic, and the load-time validator will reject collisions.

### Why rule 3 will bite you

With a small lexicon, collisions arrive fast. Authoring the v1 examples, three
different stones all wanted `TENEBRAE · LAPIS`, and had to be distinguished:

| Word | Names | Note |
|---|---|---|
| `TENEBRAE · LAPIS` | Deepslate | "dark stone" — got there first |
| `INFERNUS · LAPIS` | Netherrack | "hell's stone" |
| `NOX · LAPIS` | Echo Shard | "night stone" |
| `INFERNUS · GEMMA` | Nether Quartz | moved off `· LAPIS` once Netherrack needed it |

**Practical guidance:** budget roughly **one new glyph per handful of new rune
words**, and choose recipe ingredients partly for whether they're *nameable*. If an
item has no natural 2–3 glyph name, that's a signal to pick a different ingredient
or coin a new qualifier glyph.

### Teaching through shared determinatives

The best rune words are designed in **families**. When bone meal and sugar are
`OSSA · PULVIS` and `DULCIS · PULVIS`, a player who decodes either one infers
*"… · PULVIS names a powder"* and can guess the other. Deliberately reusing
determinatives across a recipe's ingredients is the single most effective teaching
tool in the mod — it turns one decode into a rule.

---

## 4. Rite types — not everything is an altar

A **rite type** is a grammar template plus a trigger. It answers: *how is this rite
performed, and what clauses does its inscription have?* Rite types are data, so a
modder can add "the Burial" or "the Forging" without touching Java.

Each rite type is announced by its own **invocation glyph**, exactly as Roman
inscriptions announce their type (`D.M.` opens a funerary text, `I.O.M.` a votive).
Seeing the opening glyph tells a player what kind of rite they're reading.

### Shipped rite types

| Invocation | Rite | Trigger | Batched? |
|---|---|---|---|
| **`OPUS`** | *The Work* | Altar multiblock + pedestals + pool | No — one output |
| **`MERSIO`** | *The Steeping* | Throw items into a fluid in-world | **Yes — full stacks** |
| **`TACTUS`** | *The Touch* | Use one item on another item/block | No |
| **`VIGILIA`** | *The Vigil* | Observe the sky through an Observatory | No |

### Defining one

```jsonc
// data/epigraphy/rite_types/mersio.json
{
  "invocation": "epigraphy:mersio",   // the opening glyph that names this rite

  // How the rite is performed. Trigger ids come from the code-side registry (§6).
  "trigger": {
    "type": "epigraphy:items_in_fluid",
    "batched": true                   // process whole stacks in one go
  },

  // The grammar template. Clause order here IS the inscription's word order.
  "clauses": [
    { "id": "invocation",   "binds": "fluid",      "required": true,  "max_words": 1 },
    { "id": "offering",     "binds": "ingredients","required": true,  "max_words": 3 },
    { "id": "hour",         "binds": "conditions", "required": false, "max_words": 2 },
    { "id": "locus",        "binds": "conditions", "required": false, "max_words": 1 },
    { "id": "consecration", "binds": "result",     "required": true,  "max_words": 1,
      "prefix": "epigraphy:fiat" }
  ]
}
```

- **`binds`** ties a clause to part of the recipe, so the validator can check that
  the HOUR clause really names a `condition`, the SUBJECT an `item`, and so on.
- **`max_words`** lets a clause hold several rune words (an offering of two
  ingredients is two words).
- **Optional clauses** simply don't appear in inscriptions that omit them.

---

## 5. Writing a recipe

A recipe is one concrete rite: an **inscription** (what the player reads) plus the
**exact amounts** (what they earn at Tier 3). The two describe the same thing at
different resolutions.

### 5.1 A `MERSIO` rite — Blue Bone Meal

*Bone meal and sugar thrown into water under a full moon, in whole stacks.*

```jsonc
// data/epigraphy/recipes/blue_bone_meal.json
{
  "type": "epigraphy:rite",
  "rite": "epigraphy:mersio",

  "inscription": {
    "invocation":   ["epigraphy:water"],                    // MERSIO · AQUA
    "offering":     ["epigraphy:bone_meal",                 // OSSA   · PULVIS
                     "epigraphy:sugar"],                    // DULCIS · PULVIS
    "hour":         ["epigraphy:full_moon"],                // PLENUS · LUNA
    "consecration": ["epigraphy:blue_bone_meal"]            // FIAT · VITA·OSSA·PULVIS
  },

  "fluid": "minecraft:water",
  "ingredients": [
    { "item": "minecraft:bone_meal", "count": 1 },
    { "item": "minecraft:sugar",     "count": 1 }
  ],
  "conditions": [
    { "type": "epigraphy:moon_phase", "value": "full" }
  ],
  "result": { "item": "epigraphy:blue_bone_meal", "count": 1 },

  // Because mersio is batched, the ratio above scales: throw in 64 bone meal and
  // 64 sugar and you get 64 blue bone meal in one rite.
  "batch": { "max_multiplier": 64 },
  "duration_ticks": 60
}
```

Read aloud:

> **MERSIO·AQUA — OSSA·PULVIS, DULCIS·PULVIS — PLENUS·LUNA — FIAT·VITA·OSSA·PULVIS**
>
> *"The steeping of Water — bone-dust and sweet-dust — at the Full Moon — let there
> be life-bone-dust."*

Note the teaching design: **both offerings share the `PULVIS` determinative**, and
the result is the subject word with `VITA` (Life) prefixed onto it. A player who
decodes `OSSA · PULVIS` gets a strong running start on the other two.

### 5.2 An `OPUS` rite — the Chaos Ingot

Altar rites use the full five-clause formula. Same schema, different rite type:

```jsonc
// data/epigraphy/recipes/chaos_ingot.json
{
  "type": "epigraphy:rite",
  "rite": "epigraphy:opus",

  "inscription": {
    "invocation":   ["epigraphy:blackstone_altar"],  // OPUS · ALTARE·TENEBRAE
    "offering":     ["epigraphy:blaze_rod"],         // FLAMMANS · VIRGA
    "hour":         ["epigraphy:thunderstorm"],      // CHAOS · CAELUM
    "subject":      ["epigraphy:netherite"],         // INFERNUS · METALLUM
    "consecration": ["epigraphy:chaos_ingot"]        // FIAT · CHAOS·METALLUM
  },

  "altar": "epigraphy:blackstone_altar",
  "pedestals": [ { "item": "minecraft:blaze_rod", "count": 4 } ],
  "input": { "item": "minecraft:netherite_ingot", "count": 1 },
  "fluid": "epigraphy:liquid_starlight",
  "fluid_amount": 1000,
  "conditions": [
    { "type": "epigraphy:weather",     "value": "thunder" },
    { "type": "epigraphy:sky_visible", "value": true }
  ],
  "result": { "item": "epigraphy:chaos_ingot", "count": 1 },
  "duration_ticks": 200
}
```

### 5.3 A `TACTUS` rite — no structure at all

*Use a torch on an iron ingot at midnight underground.* Pure instruction, no altar:

```jsonc
// data/epigraphy/recipes/ember_ingot.json
{
  "type": "epigraphy:rite",
  "rite": "epigraphy:tactus",

  "inscription": {
    "invocation":   ["epigraphy:flame"],        // TACTUS · FLAMMANS
    "subject":      ["epigraphy:iron"],         // TERRA  · METALLUM
    "hour":         ["epigraphy:deep_night"],   // TENEBRAE · NOX
    "locus":        ["epigraphy:the_depths"],   // TENEBRAE · TERRA
    "consecration": ["epigraphy:ember_ingot"]   // FIAT · FLAMMANS·METALLUM
  },

  "used_item":   { "item": "minecraft:torch" },
  "target_item": { "item": "minecraft:iron_ingot" },
  "conditions": [
    { "type": "epigraphy:time",   "value": "midnight" },
    { "type": "epigraphy:y_level","max": 0 }
  ],
  "result": { "item": "epigraphy:ember_ingot", "count": 1 },
  "consume_used_item": false
}
```

---

## 6. Condition catalogue

Conditions are the "world as ingredient" system. All are usable by any rite type,
and all are **data** — only inventing a genuinely new *kind* of condition needs Java.

| `type` | Fields | Matches |
|---|---|---|
| `epigraphy:weather` | `value`: `clear` / `rain` / `thunder` | weather at the rite |
| `epigraphy:time` | `value`: `day` / `night` / `midnight` / `dawn` / `dusk`, or `range: [t0,t1]` | time of day |
| `epigraphy:moon_phase` | `value`: `full` / `new` / `waxing` / `waning`, or `0`–`7` | lunar phase |
| `epigraphy:constellation` | `value`: constellation id; optional `visible: true` | a named constellation is overhead |
| `epigraphy:y_level` | `min` and/or `max` | altitude/depth band |
| `epigraphy:dimension` | `value`: dimension id | which dimension |
| `epigraphy:biome` | `value`: biome id or `#tag` | biome at the rite |
| `epigraphy:sky_visible` | `value`: `true` / `false` | open sky above |
| `epigraphy:light_level` | `min` and/or `max` | block/sky light |
| `epigraphy:fluid_present` | `value`: fluid id | the rite sits in/on this fluid |
| `epigraphy:nearby_block` | `value`: block id or `#tag`; `radius` | a block is within range |

Every condition accepts `"optional": true`, which turns it from a gate into a
**bonus** — see §7.

```jsonc
// Examples
{ "type": "epigraphy:y_level", "max": -40 }                       // deep underground
{ "type": "epigraphy:y_level", "min": 200 }                       // high altitude
{ "type": "epigraphy:constellation", "value": "epigraphy:the_forge", "visible": true }
{ "type": "epigraphy:biome", "value": "#minecraft:is_badlands" }
```

### Adding a new condition kind (the one Java case)

```java
// Register a condition type; recipes can then use it as pure data.
EpiConditions.register("examplemod:player_burning", PlayerBurningCondition::new);
```

Once registered, any datapack can write
`{ "type": "examplemod:player_burning", "value": true }`. The *use* stays data; only
the *kind* is code.

---

## 7. Optional conditions and variant results

Marking a condition `optional` makes it a modifier rather than a gate — the rite
still fires without it, but succeeds *better* with it:

```jsonc
"conditions": [
  { "type": "epigraphy:moon_phase", "value": "full" },
  { "type": "epigraphy:constellation", "value": "epigraphy:the_vine",
    "optional": true, "bonus": { "multiplier": 2 } }
],
"result": { "item": "epigraphy:blue_bone_meal", "count": 1 }
```

This is the intended way to build "same ingredients, better outcome under rarer
skies" content without duplicating recipes.

---

## 8. Validation — what the loader rejects

Run the game with the datapack loaded; failures are reported at load, not at use.

**Glyphs**
- Missing `lemma`, `gloss`, or `category`. (`texture` is optional — art generates.)
- `determinative.position` not `prefix` or `suffix`.
- A `determinative` block on a glyph whose `category` is `element` — quality glyphs
  can never be heads (`RUNES.md` §5.2).
- A `lemma` that isn't A–Z, which the monogram generator can't render.

**Rune words**
- Fewer than 2 or more than 3 glyphs.
- A glyph id that doesn't resolve.
- **No determinative-capable glyph** in the sequence — the word has no head
  (`RUNES.md` §4.3.1).
- A `means.type` that contradicts the head's determinative class (a word headed by
  `UNDA` must name a `fluid`, one headed by `LUNA` a `condition`, and so on).
- **Duplicate ordered sequence** with any existing rune word (the common one).

**Rite types**
- Unknown `trigger.type` (not in the code-side registry).
- A `consecration` clause without a `prefix` frame glyph.

**Recipes**
- An `inscription` clause not permitted by the rite type, or exceeding `max_words`.
- A missing **required** clause.
- A clause word whose `means.type` doesn't match the clause's `binds`
  (e.g. an `item` word in a clause bound to `conditions`).
- Two recipes of the same rite type matching an identical physical setup — rites
  must be deterministic.
- Referenced items/fluids/blocks that don't exist.

---

## 9. Distribution — where new glyphs are found

Adding a glyph is only half the job: players must be able to *discover* it.

| File | Controls |
|---|---|
| `data/<ns>/worldgen/glyph_pools/*.json` | which glyphs can be carved in which structures/biomes, weighted |
| `data/<ns>/loot_modifiers/*.json` | which mobs/bosses drop tablets for which glyphs |
| `data/<ns>/constellations/*.json` | which glyphs are readable from the sky, and when |

A glyph with no entry in any of these is unreachable — the validator warns, since
an undiscoverable glyph silently locks every rune word that uses it.

---

## 10. Checklist for adding new content

1. **Coin the glyphs** you need — broad, and marked as determinatives if they name a
   category. Check you actually need them; reuse beats invention. **Skip `texture`**
   unless you're deliberately hand-drawing one — art generates from `lemma` +
   `determinative.class` (§2.1).
2. **Add them to a discovery source** (§9) so they can be found.
3. **Write the rune words**, reusing determinatives across a recipe's ingredients so
   decoding one teaches the others.
4. **Pick or define a rite type** — altar, steeping, touch, vigil.
5. **Write the recipe**, keeping inscription and amounts in agreement.
6. **Load the pack** and fix validator errors — expect a sequence collision or two.
7. **Read the inscription aloud.** If it doesn't scan as a sentence, the word order
   or the glyph choice is wrong. This is the real test.

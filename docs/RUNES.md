# Runes — Glyphs & Rune Words

Epigraphy's magic is a written language. This document defines the vocabulary, the
starter lexicon, how glyphs combine into rune words, and how readable text is
generated at each tier of knowledge.

## 0. Terminology (use these words consistently)

| Term | Meaning |
|------|---------|
| **Glyph** | A single **symbol**. Maps to one Latin word (`FLAMMANS`, `VIRGA`, `CHAOS`). The atomic unit — what you find carved in the world and learn by sighting. |
| **Rune word** | A **set of 2–3 glyphs** that together name exactly one concrete thing. `FLAMMANS · VIRGA` → Blaze Rod. This is what you *guess* and submit in the codex. |
| **Runes** | The **whole system** — the full body of glyphs and rune words. "The Runes" is the language itself. |

The distinction matters mechanically: **glyphs are discovered** (found in the world,
learned passively), and **rune words are guessed** (composed from discovered glyphs
and validated in the codex). You cannot use a glyph in a guess until you have
discovered it.

---

## 1. What a glyph is

Every glyph is a datapack entry. Rituals, tablets, worldgen, and the codex all
reference glyphs by their id, so the lexicon is fully data-driven and extensible.

```jsonc
// data/epigraphy/glyphs/chaos.json
{
  "lemma": "CHAOS",            // the Latin word the symbol translates to
  "gloss": "Chaos",           // short English meaning shown once translated
  "category": "element",      // element | place | action | material | celestial
  "rarity": "rare",           // common | uncommon | rare — affects where it hides
  "texture": "epigraphy:glyph/chaos",   // 32x32 monochrome symbol
  "sightings_to_translate": 3,          // how many independent sightings to reach Tier 2
  "description": "The unmaking that precedes remaking; the churn beneath order."
}
```

Fields:

- **`lemma`** — the Latin word. This is what a fluent player "reads." Latin is
  chosen for the same reason Astral uses star-lore and Thaumcraft uses aspects:
  it feels ancient, it's terse, and it lets short symbol strings carry meaning.
- **`gloss`** — the plain-English meaning revealed at Tier 2. Kept to one or two
  words so translated ritual text stays readable.
- **`category`** — governs both *where the glyph tends to hide* (see
  `DISCOVERY.md`) and *how it reads in a sentence* (see §3 below).
- **`rarity`** — biases worldgen and mob-drop tables. Rare glyphs (like `CHAOS`)
  are the payoff for deep exploration or boss kills.
- **`sightings_to_translate`** — how many separate in-world sightings it takes to
  crack the meaning without a Rosetta tablet. Rarer glyphs take more.

---

## 2. Starter lexicon

The v1 vocabulary. Every glyph here can be sighted, translated, and used in at
least one ritual. Categories are colour-coded wherever glyphs render (in-world
carvings, tablet tooltips, the in-game documentation).

### Materials — `METALLUM`, and kin
| id | lemma | gloss | rarity | notes |
|----|-------|-------|--------|-------|
| `epigraphy:metallum` | METALLUM | Metal | common | any ingot/metal input |
| `epigraphy:lapis`    | LAPIS    | Stone | common | stone, blackstone, deepslate |
| `epigraphy:virga`    | VIRGA    | Rod   | uncommon | rod/stick-shaped catalysts (blaze rod) |

### Elements
| id | lemma | gloss | rarity | notes |
|----|-------|-------|--------|-------|
| `epigraphy:flammans` | FLAMMANS | Flaming | uncommon | fire/heat; blaze, lava, fire aspect |
| `epigraphy:tenebrae` | TENEBRAE | Darkness | uncommon | night, low light, the deep dark |
| `epigraphy:chaos`    | CHAOS    | Chaos | rare | transformation, instability, remaking |

### Places
| id | lemma | gloss | rarity | notes |
|----|-------|-------|--------|-------|
| `epigraphy:altare`   | ALTARE   | Altar | common | the ritual structure itself |
| `epigraphy:infernus` | INFERNUS | Hell  | rare | the Nether dimension / nether materials |

### Celestial
| id | lemma | gloss | rarity | notes |
|----|-------|-------|--------|-------|
| `epigraphy:caelum`   | CAELUM   | Heavens | uncommon | sky access, storms, day |
| `epigraphy:nox`      | NOX      | Night | common | requires darkness/night to read the sky |

> These eight-plus glyphs cover every condition and input the v1 rituals need
> (`RITUALS.md`), while leaving obvious room to grow (Water/`AQUA`, Life/`VITA`,
> Void/`VACUUM`, Order/`ORDO`, etc.).

---

## 3. Rune words: 2–3 glyphs that name one thing (D8)

The language is **compositional**, not sentence-based. A **rune word** is **2 or 3
glyphs** that together name exactly **one concrete thing** — an item, a block, a
world condition, or a ritual output. Rituals are described as a small set of
rune words, each hinting at one component. Hints always come in these 2–3 word
batches, never as one long sentence.

```
ALTARE · TENEBRAE      → Blackstone Altar   (Altar + Darkness)
FLAMMANS · VIRGA       → Blaze Rod          (Flaming + Rod)
CAELUM · CHAOS         → Thunderstorm       (Heavens + Chaos)
INFERNUS · METALLUM    → Netherite          (Hell + Metal)
CHAOS · METALLUM       → Chaos Ingot        (Chaos + Metal)
```

This is the heart of the mod's decode loop: the player *learns words passively*
(D2) and then *works out what those words build* by hypothesising rune words and
submitting them in the hand codex (D7).

### 3.1 Rune words as data

```jsonc
// data/epigraphy/rune_words/blaze_rod.json
{
  "glyphs": ["epigraphy:flammans", "epigraphy:virga"],  // 2 or 3, order-independent
  "means": { "type": "item", "value": "minecraft:blaze_rod" },
  "reading": "That which flames, in the shape of a rod.",  // flavor shown once decoded
  "hint": "A rod that burns."                              // terse form for in-world text
}
```

`means.type` is one of:
- `item` / `tag` — names an ingredient or output.
- `block` — names a structure component (e.g. the altar itself).
- `condition` — names a world condition, matching the condition types in
  `RITUALS.md` §3 (e.g. thunderstorm, night, new moon).

Because rune words are data, the whole language — and every hint the player ever
reads — is authorable and pack-extensible without code.

### 3.2 The codex submit loop (D7 / D9)

1. The player has discovered and learned, say, `FLAMMANS` and `VIRGA`.
2. They notice blaze rods keep appearing near fire-themed ruins and hypothesise a
   link. In the codex's 20-slot grid they cycle two slots to `FLAMMANS` and
   `VIRGA`, and hit **submit**.
3. The codex validates against the rune word registry:
   - **Match** → the rune word is *decoded*, permanently. It now reads as "Blaze Rod"
     wherever it appears, and any ritual hint using it becomes that much clearer.
   - **No match** → nothing happens beyond a soft negative cue. No penalty, no
     progress. (Cost model still open — `DECISIONS.md` Q7.)
4. Slots only cycle through **discovered** glyphs, so you cannot brute-force with
   symbols you haven't found. The grid holds several guesses at once, separated by
   empty slots (`DECISIONS.md` Q8).

### 3.3 How rune words render at each tier

A ritual hint is a list of rune words, and each rune word renders according to what the
player knows — which is what makes partial knowledge legible and directional:

| Player state | How the rune word renders |
|---|---|
| Hasn't learned one or more of its glyphs | `⟨glyph⟩ · ???` — the raw symbols, unreadable |
| Learned all its glyphs, rune word **not** decoded | *"Flaming · Rod"* — literal glosses, meaning unresolved |
| Rune word **decoded** via codex submit | *"Blaze Rod"* — the rune word's true referent |

The middle row is the good part: *"Flaming · Rod"* is a genuine, solvable clue. The
player can reason their way to "blaze rod" before the game confirms it — and the
codex submit is how they check that hunch.

Two rules keep hints hints:
1. Rune words never emit counts or exact amounts — `FLAMMANS · VIRGA` says *blaze rod*,
   never *4 blaze rods*. Exact quantities arrive only at Tier 3 (mastered).
2. A ritual's rune word list is unordered for mechanics; order is presentational only.

### 3.4 Combinatorial safety

With ~10 starter glyphs there are 45 possible pairs and 120 triples, so blind
brute-forcing is theoretically possible. Three things keep the decode loop honest:

- **You can only submit glyphs you've learned**, so the practical search space early
  on is tiny and grows only as you explore.
- **Valid rune words are sparse and thematic** — the correct pairs are guessable from
  fiction ("Hell + Metal"), so reasoning is strictly faster than enumerating.
- **A submission cooldown** (leaning, Q7) makes brute force tedious rather than
  optimal, without punishing genuine experimentation.

---

## 4. Translation flavor text

When a glyph reaches Tier 2, three text surfaces become available and are worth
authoring per-glyph:

1. **Gloss** — the one-word meaning (from the glyph JSON).
2. **Description** — a sentence of lore (from `description`), shown on the glyph's
   in-game documentation entry and on inscribed-tablet tooltips. This is where the
   *feel* of the language lives.
3. **Rune word readings** — each *rune word* carries its own `reading` and `hint` strings
   (§3.1), which is where the 2–3 word batches get their voice. The glyph gloss is
   the fallback used before a rune word is decoded.

All of it lives in datapack + lang files, so it localizes cleanly and pack makers
can reskin the whole language.

---

## 5. Design notes

- **Why Latin, not a conlang?** A real language the player can partially
  recognize ("infernus… inferno… hell") rewards attention without a decoder ring,
  and it sidesteps inventing (and localizing) a fake grammar. Symbols stay
  abstract; the *translation* is the familiar word.
- **Ambiguity is a feature.** `METALLUM` meaning "any metal" and `VIRGA` meaning
  "any rod" is what makes *composition* meaningful: neither word alone names a
  thing, but `INFERNUS · METALLUM` and `FLAMMANS · VIRGA` each name exactly one.
  Broad words + narrow rune words is the whole trick.
- **Reuse is the reward.** A learned glyph pays off across every rune word it appears
  in — `METALLUM` unlocks progress on netherite *and* chaos ingots *and* every
  future metal. Vocabulary compounds; that's what makes late-game fluency feel
  earned rather than granted.
- **Growth path.** New glyphs *and new rune words* are pure datapack additions; no
  code change is needed to expand the language, only to introduce genuinely new
  *condition types* or *categories*.

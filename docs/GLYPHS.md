# Glyphs & the Language

Epigraphy's magic is a written language. This document defines what a glyph *is*
as data, the starter lexicon, how glyphs combine into ritual "sentences," and how
translated flavor text is generated.

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

## 3. Sentences: how glyphs read as a ritual

A ritual's `glyphs` array is ordered, and the order is meaningful for *reading*
(not for mechanics). The translation system assembles readable text — shown on
ritual-tablet tooltips, in the in-game documentation, and on in-world readable
carvings — by slotting each glyph into a template chosen by its category:

- **place** → framing clause: *"Upon the **Altar** …"*
- **material** → object clause: *"… offer **Metal** …"*
- **action/element** → verb/manner clause: *"… that it may be made **Flaming** …"*
- **celestial** → condition clause: *"… beneath the raging **Heavens** …"*

So `ALTARE · FLAMMANS · CAELUM · METALLUM · CHAOS` renders roughly:

> *Upon the **Altar**, that which **flames** — beneath the **Heavens** — quench
> **Metal** into **Chaos**.*

The sentence is intentionally a *hint*, not a spec. Two design rules keep it that
way:

1. The template never emits item ids or counts — only glosses.
2. If any glyph in the sentence is only Tier 1 (sighted, untranslated), that word
   renders as its raw symbol/`???`, leaving a partially-legible sentence that
   still nudges the player toward what to translate next.

---

## 4. Translation flavor text

When a glyph reaches Tier 2, three text surfaces become available and are worth
authoring per-glyph:

1. **Gloss** — the one-word meaning (from the glyph JSON).
2. **Description** — a sentence of lore (from `description`), shown on the glyph's
   in-game documentation entry and on inscribed-tablet tooltips. This is where the
   *feel* of the language lives.
3. **Sentence fragment** — the clause template above uses the gloss, but a glyph
   may optionally override its clause with a hand-written fragment via a
   `clause` field for glyphs whose grammar is awkward.

All three live in datapack + lang files, so they localize cleanly and pack makers
can reskin the whole language.

---

## 5. Design notes

- **Why Latin, not a conlang?** A real language the player can partially
  recognize ("infernus… inferno… hell") rewards attention without a decoder ring,
  and it sidesteps inventing (and localizing) a fake grammar. Symbols stay
  abstract; the *translation* is the familiar word.
- **Ambiguity is a feature.** `METALLUM` meaning "any metal" and `VIRGA` meaning
  "any rod" keeps rituals readable as poetry rather than recipes, preserving the
  Tier-2/Tier-3 gap.
- **Growth path.** New glyphs are pure datapack additions; no code change is
  needed to expand the lexicon, only to introduce genuinely new *condition types*
  or *categories*.

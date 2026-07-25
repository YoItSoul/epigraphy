# Runes — Glyphs & Rune Words

Epigraphy's magic is a written language. This document defines the vocabulary, the
starter lexicon, how glyphs combine into rune words, and how readable text is
generated at each tier of knowledge.

## 0. Terminology (use these words consistently)

| Term | Meaning |
|------|---------|
| **Glyph** | A single **symbol**. Maps to one Latin word (`FLAMMANS`, `VIRGA`, `CHAOS`). The atomic unit — what you find carved in the world and learn by sighting. |
| **Rune word** | An **ordered sequence of 2–3 glyphs** that together name exactly one concrete thing. `FLAMMANS · VIRGA` → Blaze Rod. This is what you *guess* and inscribe in the codex. |
| **Runes** | The **whole system** — the glyphs, the rune words, and the grammar binding them. "The Runes" is the language itself. |

Two mechanical points follow:

1. **Glyphs are discovered; rune words are guessed.** You cannot use a glyph in a
   guess until you have found it in the world.
2. **Order is meaningful at both levels** (D10). Within a word, `FLAMMANS · VIRGA`
   and `VIRGA · FLAMMANS` are different expressions and only one is valid. Across a
   ritual, the words follow a fixed **inscription formula** (§4) — the pattern that
   makes the language readable, exactly as real epigraphic formulae do.

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
  `DISCOVERY.md`) and *what role it can play in a rune word* — broadly, `material`
  and `place` glyphs tend to be **heads**, while `element` and `celestial` glyphs
  tend to be **qualifiers** (§4.3).
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

### Frame — the invariant formula glyphs (§4.4)
| id | lemma | gloss | rarity | notes |
|----|-------|-------|--------|-------|
| `epigraphy:opus` | OPUS | Work / Rite | common | opens every inscription; the mod's most-seen glyph |
| `epigraphy:fiat` | FIAT | Let it be made | common | opens the final (result) clause |

> Frame glyphs never appear inside an ordinary rune word — only as clause markers.
> Because they're on every inscription, players learn them first, which is the point:
> the invariant frame teaches you where the variable parts are.

> These eight-plus glyphs cover every condition and input the v1 rituals need
> (`RITUALS.md`), while leaving obvious room to grow (Water/`AQUA`, Life/`VITA`,
> Void/`VACUUM`, Order/`ORDO`, etc.).

---

## 3. Rune words: 2–3 glyphs, **in order**, that name one thing (D8/D10)

The language is **compositional and ordered**. A **rune word** is **2 or 3 glyphs
in a specific sequence** that together name exactly **one concrete thing** — an
item, a block, a world condition, or a ritual output.

**Order is meaningful.** `FLAMMANS · VIRGA` is not the same expression as
`VIRGA · FLAMMANS`; only one of them is the word for a blaze rod. This is what
makes the Runes a *language* rather than a set of ingredient checkboxes, and it is
what the player is really learning.

```
ALTARE · TENEBRAE      → Blackstone Altar   (Altar + Darkness)
FLAMMANS · VIRGA       → Blaze Rod          (Flaming + Rod)
CHAOS · CAELUM         → Thunderstorm       (Chaos + Heavens)
INFERNUS · METALLUM    → Netherite          (Hell + Metal)
CHAOS · METALLUM       → Chaos Ingot        (Chaos + Metal)
```

This is the heart of the decode loop: the player *learns words passively* (D2) and
then *works out what those words build, and in what order,* by inscribing them in
the hand codex (D7).

### 3.1 Rune words as data

```jsonc
// data/epigraphy/rune_words/blaze_rod.json
{
  // ORDERED sequence of 2-3 glyphs. Sequence is part of the word's identity;
  // the reverse sequence is a different (usually invalid) expression.
  "glyphs": ["epigraphy:flammans", "epigraphy:virga"],
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
reads — is authorable and pack-extensible without code. The registry is indexed by
**ordered glyph sequence**, so submission validation is an exact-sequence lookup.

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
2. Order is **mechanical, not presentational** — both within a word and across the
   inscription (§4).

### 3.4 Combinatorial safety

Ordering roughly doubles the raw search space (an ordered pair from 10 glyphs has
90 possibilities, not 45), which sounds like it makes brute-forcing worse. It
doesn't, because the **grammar** (§4) collapses it: once a player knows the formula,
they know a rod-shaped catalyst word is `QUALIFIER · VIRGA`, and they only have to
guess the qualifier. Structure turns a combinatorial search into a small, reasoned
one — which is exactly the fantasy of decipherment.

- **You can only inscribe glyphs you've learned**, so the practical search space
  early on is tiny and grows only as you explore.
- **The formula constrains position**, so most slots are determined before you guess.
- **Valid rune words are sparse and thematic** — "Hell + Metal" is guessable from
  fiction, so reasoning is strictly faster than enumerating.
- **A submission cooldown** (leaning, Q7) makes brute force tedious rather than
  optimal, without punishing genuine experimentation.

---

## 4. Grammar: the inscription formula (D10)

Real epigraphy is readable because inscriptions follow **formulae**. A Roman votive
runs *deity (dative) → dedicant (nominative) → `V.S.L.M.`*, in that order, every
time; once you know the formula you can read a stone you've never seen. Epigraphy's
Runes work the same way, and this is the pattern the player is really learning.

### 4.1 The ritual formula

A ritual inscription is a fixed sequence of clauses, read left to right, framed by
invariant formulae at each end (§4.4):

```
  OPUS·[ VESSEL ]   [ OFFERING ]   [ HOUR ]   [ SUBJECT ]   FIAT·[ ISSUE ]
       the altar     what rings     when it    what is            what it
       it needs      the altar      must be    transformed        becomes
  └ INVOCATION ─┘                                            └ CONSECRATION ┘
```

| Clause | Answers | Maps to (recipe) |
|---|---|---|
| **INVOCATION** (`OPUS` + vessel) | *Where?* | the altar block/tier |
| **OFFERING** | *With what?* | the pedestal catalysts |
| **HOUR** | *When?* | the world conditions |
| **SUBJECT** | *Upon what?* | the input item |
| **CONSECRATION** (`FIAT` + issue) | *Yielding what?* | the result |

Each clause is filled by exactly one **rune word** (2–3 glyphs). The Chaos Ingot
inscription, read literally in order:

```
ALTARE·TENEBRAE   FLAMMANS·VIRGA   CHAOS·CAELUM   INFERNUS·METALLUM  →  CHAOS·METALLUM
────────┬───────  ───────┬──────   ──────┬─────   ────────┬────────     ───────┬──────
 VESSEL           OFFERING          HOUR            SUBJECT               ISSUE
Blackstone Altar   Blaze Rod      Thunderstorm      Netherite          Chaos Ingot
```

That is the whole recipe, written as one sentence in a language with rules. A
player who knows the formula can look at an unfamiliar inscription and immediately
say *"the third word is the condition"* — even before decoding it.

### 4.2 Why the formula matters mechanically

- **It makes partial knowledge productive.** An undecoded word in the HOUR position
  is still known to be *a condition*, so the player can reason about it from the
  category alone.
- **It makes guessing tractable.** Position tells you what kind of thing you're
  naming, turning a wild guess into a narrow one (§3.4).
- **It makes forgeries fail.** A grammatically wrong inscription — right words,
  wrong order — is not a valid ritual, which is what gives the language teeth.

### 4.3 Word-internal order: QUALIFIER · HEAD

Within a word, the convention is **qualifier first, head second** — the second
glyph names *what kind of thing it is*, the first *narrows which one*:

| Word | Qualifier | Head | Names |
|---|---|---|---|
| `FLAMMANS · VIRGA` | Flaming | Rod | Blaze Rod |
| `CHAOS · CAELUM` | Chaos | Heavens | Thunderstorm |
| `INFERNUS · METALLUM` | Hell | Metal | Netherite |
| `CHAOS · METALLUM` | Chaos | Metal | Chaos Ingot |

This is why the head glyph is so reusable: every rod-catalyst ends in `VIRGA`,
every metal ends in `METALLUM`. Learning a head glyph unlocks a whole *category* of
guessable words — vocabulary that compounds.

**One open point (`DECISIONS.md` Q9):** the VESSEL clause in the source example
reads `ALTARE · TENEBRAE` — *head first*, the mirror of the rule above. Two ways to
resolve it, both defensible:
- **(a) The vessel is a named exception.** An inscription opens by naming its
  subject — the altar — then qualifies it, exactly as Latin dedications name the
  dedicatee first (`ALTARE TENEBRARUM`, "altar of darkness"). Keeps the original
  example literal.
- **(b) Strict `QUALIFIER · HEAD` everywhere** → `TENEBRAE · ALTARE`. One simple
  rule with no exceptions, at the cost of flipping that one word.

### 4.4 The frame: invocation and consecration (recommended)

Real inscriptions are readable at a glance because they are **framed by invariant
formulae**. A Roman votive opens with the deity (`I.O.M.` — *Iovi Optimo Maximo*)
and closes with `V.S.L.M.` (*Votum Solvit Libens Merito*, "fulfilled his vow,
willingly and deservedly"). The middle varies; the frame never does. That's what
lets you read a stone you've never seen: you recognise the edges, so you know what
the inside must be.

**Recommendation: give Epigraphy the same frame.** Two glyphs that appear in
*every* ritual inscription and nowhere else:

| Glyph | Lemma | Role |
|---|---|---|
| `epigraphy:opus` | **OPUS** | *The work / the rite.* Opens every inscription. |
| `epigraphy:fiat` | **FIAT** | *Let it be made.* Opens the final clause. |

The full structure becomes:

```
OPUS·ALTARE·TENEBRAE   FLAMMANS·VIRGA   CHAOS·CAELUM   INFERNUS·METALLUM   FIAT·CHAOS·METALLUM
└───── INVOCATION ───┘  └─ OFFERING ─┘  └── HOUR ───┘  └──── SUBJECT ────┘  └── CONSECRATION ──┘
 "The rite of the           by Blaze        when the       upon Hell's          let there be
  Dark Altar…"                Rod          Heavens rage      Metal…              Chaos Metal."
```

Read aloud: *"The rite of the Dark Altar — by the Flaming Rod — when the Heavens
turn to Chaos — upon the Metal of Hell — let there be Chaos Metal."*

That is a sentence. It scans, it has an opening and a close, and it is the recipe.

**Why the frame earns its two glyphs:**

- **It teaches the formula for free.** `OPUS` is the most common glyph in the game —
  every inscription starts with it. A player will learn it almost immediately and,
  in doing so, learn *where inscriptions begin*. `FIAT` teaches them where the
  result lives. The invariant parts bootstrap comprehension of the variable parts,
  which is precisely how real decipherment works.
- **It makes the codex unambiguous.** In the 20-slot grid, `OPUS` and `FIAT` mark
  the boundaries, so there is never a question about where the inscription starts or
  which clause is the output.
- **It makes forgery legible.** An inscription missing its frame is obviously not a
  rite — a nice, readable failure state rather than a silent mismatch.
- **It gives the mod its `V.S.L.M.`** — a signature the player will come to
  recognise on sight, carved on every ruin. That's identity.

**Flavour worth stealing:** real inscriptions abbreviate the frame to initials. A
weathered carving can show the terse form — `O·A·T … F·C·M` — with the full reading
available once you're fluent. Same trick as `V.S.L.M.`, and it makes worn stones
feel genuinely worn.

**The lean alternative:** skip the frame and run the five bare clauses
(`VESSEL / OFFERING / HOUR / SUBJECT / ISSUE`). Two fewer glyphs, marginally less
typing, but the inscription reads as a list rather than a sentence and the codex
needs another rule to mark the output clause. *Recommended: take the frame.*

### 4.5 Three-glyph words

A third glyph is used when two are ambiguous. It inserts an **additional qualifier
before the head**, never after:

```
NOX · TENEBRAE · CAELUM   →  New Moon
(Night)(Darkness)(Heavens)    "the darkened night sky"
```

Two words would only get you "a dark sky"; the third pins it. The head (`CAELUM`)
still comes last, so the rule scales without a new pattern to learn.

---

## 5. Readable text at each tier

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

## 6. Design notes

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

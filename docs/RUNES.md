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

### Places & realms
| id | lemma | gloss | rarity | determinative | notes |
|----|-------|-------|--------|---------------|-------|
| `epigraphy:altare`   | ALTARE   | Altar | common | prefix (structure) | the ritual structure itself |
| `epigraphy:infernus` | INFERNUS | Hell  | rare | prefix (structure) | infernal things; qualifies nether matter |
| `epigraphy:regnum`   | REGNUM   | Realm | uncommon | suffix (realm) | heads every dimension word |
| `epigraphy:finis`    | FINIS    | End   | rare | — | qualifier: ending, outermost, the void |

### Celestial
| id | lemma | gloss | rarity | notes |
|----|-------|-------|--------|-------|
| `epigraphy:caelum`   | CAELUM   | Heavens | uncommon | sky access, storms, day |
| `epigraphy:nox`      | NOX      | Night | common | requires darkness/night to read the sky |

### Materials, continued — powders & organics
| id | lemma | gloss | rarity | determinative | notes |
|----|-------|-------|--------|---------------|-------|
| `epigraphy:pulvis` | PULVIS | Dust | common | suffix (powder) | bone meal, sugar, glowstone dust, redstone |
| `epigraphy:ossa`   | OSSA   | Bone | common | — | bones, undead materials |
| `epigraphy:dulcis` | DULCIS | Sweet | common | — | sugar, honey, sweet things |
| `epigraphy:terra`  | TERRA  | Earth | common | suffix (earthen) | dirt, iron, mundane ground |

### Fluids
| id | lemma | gloss | rarity | determinative | notes |
|----|-------|-------|--------|---------------|-------|
| `epigraphy:unda`   | UNDA   | Flow | common | suffix (fluid) | heads every liquid: water, lava, starlight |

### Objects & gems
| id | lemma | gloss | rarity | determinative | notes |
|----|-------|-------|--------|---------------|-------|
| `epigraphy:gladius` | GLADIUS | Blade | uncommon | suffix (blade) | swords, axes, edged tools |
| `epigraphy:gemma`   | GEMMA   | Gem   | uncommon | suffix (gem) | quartz, pearls, crystalline drops |

### Elements, continued
| id | lemma | gloss | rarity | notes |
|----|-------|-------|--------|-------|
| `epigraphy:vita`   | VITA   | Life | uncommon | growth, healing, fertility |
| `epigraphy:fundus` | FUNDUS | Foundation | uncommon | qualifier: the bottom, the root, bedrock |

### Celestial, continued
| id | lemma | gloss | rarity | notes |
|----|-------|-------|--------|-------|
| `epigraphy:luna`   | LUNA   | Moon | uncommon | suffix determinative for lunar states |
| `epigraphy:plenus` | PLENUS | Full | common | qualifier: full moon, brimming, complete |

### Creatures
| id | lemma | gloss | rarity | determinative | notes |
|----|-------|-------|--------|---------------|-------|
| `epigraphy:bestia` | BESTIA | Beast | common | suffix (creature) | heads almost every mob word |
| `epigraphy:draco`  | DRACO  | Dragon | rare | suffix (creature) | boss head — reads as a warning, not another beast |
| `epigraphy:custos` | CUSTOS | Warden | rare | suffix (creature) | boss head for guardians |

### Tool heads — implements
| id | lemma | gloss | determinative | names |
|----|-------|-------|---------------|-------|
| `epigraphy:gladius` | GLADIUS | Blade | suffix (material) | swords |
| `epigraphy:dolabra` | DOLABRA | Pick | suffix (material) | pickaxes |
| `epigraphy:securis` | SECURIS | Axe | suffix (material) | axes |
| `epigraphy:pala`    | PALA    | Spade | suffix (material) | shovels |
| `epigraphy:falx`    | FALX    | Sickle | suffix (material) | hoes |
| `epigraphy:arcus`   | ARCUS   | Bow | suffix (material) | bows, crossbows |
| `epigraphy:scutum`  | SCUTUM  | Shield | suffix (material) | shields |
| `epigraphy:lorica`  | LORICA  | Mail | suffix (material) | armour pieces |
| `epigraphy:hamus`   | HAMUS   | Hook | suffix (material) | fishing rods |
| `epigraphy:forfex`  | FORFEX  | Shears | suffix (material) | shears |

### Tool materials & organics
| id | lemma | gloss | mark | notes |
|----|-------|-------|------|-------|
| `epigraphy:lignum` | LIGNUM | Wood | `LI` | |
| `epigraphy:ferrum` | FERRUM | Iron | `FE` | |
| `epigraphy:adamas` | ADAMAS | Diamond | `AD` | |
| `epigraphy:aurum`  | AURUM  | Gold | `AV` | classical `AVRVM` |
| `epigraphy:lana`   | LANA   | Wool | **`LN`** | **mark override** — `LA` was taken by `LAPIS` |
| `epigraphy:pluma`  | PLUMA  | Feather | `PL` | |
| `epigraphy:mors`     | MORS     | Death | `MO` | element — qualifier only |
| `epigraphy:venenum`  | VENENUM  | Venom | `VE` | element — qualifier only |

### Frame — invocations & closing formula (§4.4)
These never appear inside an ordinary rune word — they are clause markers only.

| id | lemma | gloss | role |
|----|-------|-------|------|
| `epigraphy:opus`    | OPUS    | The Work | invocation — **altar rite** |
| `epigraphy:mersio`  | MERSIO  | The Steeping | invocation — **items into fluid** (batched) |
| `epigraphy:tactus`  | TACTUS  | The Touch | invocation — **item used on item** |
| `epigraphy:vigilia` | VIGILIA | The Vigil | invocation — **sky observation** |
| `epigraphy:fiat`    | FIAT    | Let it be made | closes every inscription, prefixing the result |

> The **invocation glyph declares the rite type**, exactly as `D.M.` opens a Roman
> funerary text and `I.O.M.` a votive one — you know what kind of inscription you're
> reading from its first sign. `FIAT` closes them all, so every inscription has a
> recognisable shape regardless of type. Because frame glyphs appear on *every*
> inscription, players learn them first — which is the point: the invariant frame
> teaches you where the variable parts are.

> The lexicon is expected to **grow alongside the recipe list** — budget roughly one
> new glyph per handful of new rune words (`AUTHORING.md` §3). Obvious room to grow:
> Void/`VACUUM`, Order/`ORDO`, Blood/`SANGUIS`, Wind/`VENTUS`.

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

### 3.1.1 Worked vocabulary

Six words showing the system across all three `means` types. New glyphs are marked †
— note the ratio: **three new glyphs bought six words**, which is the budget
`AUTHORING.md` §3 recommends.

| Names | Rune word | Reads as | `means.type` |
|---|---|---|---|
| **Lava** | `FLAMMANS · UNDA†` | Flaming Flow | `fluid` |
| **Water** | `VITA · UNDA†` | Living Flow | `fluid` |
| **Full Moon** | `PLENUS · LUNA` | Full Moon | `condition` — `moon_phase: full` |
| **Netherite Sword** | `INFERNUS · METALLUM · GLADIUS†` | Hell-Metal Blade | `item` |
| **Mountain Top** | `CAELUM · TERRA` | Sky-Earth | `condition` — `y_level: {min:190}` |
| **Near Bedrock** | `FUNDUS† · TERRA` | Foundation-Earth | `condition` — `y_level: {max:-50}` |

Three things this set demonstrates:

**Minimal pairs teach themselves.** Lava and water differ by exactly one glyph, and
that glyph is the difference between them: `FLAMMANS` vs `VITA` on a shared `UNDA`
head. A player who decodes either can guess the other, and has learned "`· UNDA`
names a liquid" in the process. Liquid starlight then falls out for free as
`CAELUM · UNDA`.

**Opposites share a head.** `CAELUM · TERRA` (high ground) and `FUNDUS · TERRA`
(bedrock) are a matched vertical pair — same head, opposed qualifiers. The lexicon
should be authored in these pairs wherever the fiction allows; it is the cheapest
teaching device available.

**Composition pays off.** `INFERNUS · METALLUM` already means netherite. Appending
`GLADIUS` gives the sword — so a player who knows the metal can *predict* the
weapon without ever having seen it. This is the moment the language stops being a
lookup table and starts being a language, and it is worth authoring toward
deliberately: prefer building new words out of known ones over coining fresh glyphs.

### 3.1.2 The realms — authoring a grid, not a list

Dimensions are headed by `REGNUM` (Realm), giving a matched set one qualifier apart:

| Names | Rune word | Reads as |
|---|---|---|
| **The Overworld** | `CAELUM · REGNUM` | Realm of Sky |
| **The Nether** | `INFERNUS · REGNUM` | Realm of Hell |
| **The End** | `FINIS · REGNUM` | Realm of the End |

`CAELUM · REGNUM` is worth noting as a piece of design: the Overworld is genuinely
*the dimension with an open sky*, so the word is both guessable from fiction and true
to the mechanics. Words that are right in both registers at once are the ones to
reach for.

The real payoff is that those qualifiers extend downward onto other heads. Author the
**grid**, not the list:

| Qualifier | `· REGNUM` (realm) | `· LAPIS` (stone) | `· GEMMA` (gem) |
|---|---|---|---|
| `INFERNUS` | the Nether | Netherrack | Nether Quartz |
| `FINIS` | the End | End Stone | Ender Pearl |
| `CAELUM` | the Overworld | — | — |

Two new glyphs (`REGNUM`, `FINIS`) plus one new head (`GEMMA`) buy **seven words**,
and a player who has decoded *the Nether* and *End Stone* can derive *Netherrack*
without ever meeting it. Filling a grid is strictly cheaper — in glyphs and in
player effort — than coining words one at a time.

> **A correction this forced.** Nether Quartz was previously `INFERNUS · LAPIS`, which
> squatted on the natural name for **Netherrack**. Reassigning quartz to
> `INFERNUS · GEMMA` frees it and reads better besides. Exactly the lexicon pressure
> `AUTHORING.md` §3 warns about — it surfaces when you author a family and see the
> gaps.

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

### 3.1.3 Tools — a grid of material × implement

Every tool is **`[material] · [implement]`**, implement always the head. The tool tree
is therefore a grid to fill, not a list to coin:

| | `· GLADIUS` | `· DOLABRA` | `· SECURIS` | `· PALA` | `· FALX` |
|---|---|---|---|---|---|
| `LIGNUM` | Wooden Sword | Wooden Pickaxe | Wooden Axe | Wooden Shovel | Wooden Hoe |
| `LAPIS` | Stone Sword | Stone Pickaxe | Stone Axe | Stone Shovel | Stone Hoe |
| `FERRUM` | Iron Sword | Iron Pickaxe | Iron Axe | Iron Shovel | Iron Hoe |
| `ADAMAS` | Diamond Sword | Diamond Pickaxe | Diamond Axe | Diamond Shovel | Diamond Hoe |
| `AURUM` | Golden Sword | … | … | … | … |
| `INFERNUS · METALLUM` | Netherite Sword | … | … | … | … |

Plus the non-tiered implements: `LIGNUM · ARCUS` (Bow), `LIGNUM · HAMUS` (Fishing
Rod), `LIGNUM · SCUTUM` (Shield), `FERRUM · FORFEX` (Shears), `FERRUM · LORICA`
(Iron Chestplate).

**Nine implement heads and five material qualifiers describe every vanilla tool** —
fourteen glyphs for roughly forty-five words, and a player who has decoded
`FERRUM · SECURIS` and `LAPIS · DOLABRA` reads `FERRUM · DOLABRA` on sight.

### 3.1.4 Mobs — one head, borrowed qualifiers

Mobs are almost all
**`[quality] · BESTIA`**. The striking thing is how little new vocabulary they need:

| Mob | Rune word | Qualifier borrowed from |
|---|---|---|
| Zombie | `MORS · BESTIA` | new |
| Skeleton | `OSSA · BESTIA` | bone meal |
| Creeper | `CHAOS · BESTIA` | the Chaos Ingot |
| Spider | `VENENUM · BESTIA` | new |
| Enderman | `FINIS · BESTIA` | the End |
| Blaze | `FLAMMANS · BESTIA` | blaze rods |
| Ghast | `CAELUM · BESTIA` | the Overworld / sky |
| Piglin | `INFERNUS · BESTIA` | netherite |
| Sheep | `LANA · BESTIA` | new (mark `LN`) |
| Chicken | `PLUMA · BESTIA` | new |
| Warden | `TENEBRAE · CUSTOS` | deepslate / darkness |
| Ender Dragon | `FINIS · DRACO` | the End |

**Eight of twelve reuse a qualifier coined for something else entirely.** That is the
compounding return on a well-chosen glyph: `CHAOS` was minted for an ingot and now
names a Creeper.

Bosses take a **distinct head** — `DRACO`, `CUSTOS` — so their words don't read as
just another beast. That matters when the word is a warning carved on a wall.

---

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

### 4.3 What real writing systems do (and which one we're building)

Three features of real scripts do all the work here. Epigraphy uses all three, and
the design is stronger for naming them explicitly.

#### (i) Compounding — two signs, one meaning

Every logographic script builds new words by **juxtaposing existing signs**. The
compound means something the parts don't:

| Language | Compound | Literally | Means |
|---|---|---|---|
| Chinese | 火山 | fire · mountain | **volcano** |
| Chinese | 电脑 | electric · brain | **computer** |
| Chinese | 手机 | hand · machine | **mobile phone** |
| Japanese | 手紙 | hand · paper | **letter** |
| German | Handschuh | hand · shoe | **glove** |
| English | firewood, blackbird, doorbell | — | — |

Notice every single one is **head-final**: the *last* element says what the thing
**is**, the earlier one narrows it. A blackbird is a *bird*. Firewood is *wood*. 火山
is a *mountain*. This is overwhelmingly the cross-linguistic norm for compounds, and
it is exactly `FLAMMANS · VIRGA` → a *rod*, that flames.

**This is the single most important pattern to hold.** It is why the language feels
natural to players who have never thought about linguistics: they already speak a
language that does this.

#### (ii) Determinatives — the feature you'd already half-invented

This is the big one. Sumerian and Egyptian both use **determinatives**: signs that
are *not read aloud* and carry no sound — they exist purely to tell the reader
**what category the word belongs to**.

| Script | Sign | Position | Marks |
|---|---|---|---|
| Sumerian | 𒀭 `DINGIR` | **prefix** | the word is a **god** |
| Sumerian | 𒆠 `KI` | **suffix** | the word is a **place** |
| Sumerian | 𒄑 `GIŠ` | **prefix** | the object is **wooden** |
| Sumerian | 𒐕 `DIŠ` | prefix | the word is a **man's name** |
| Egyptian | 𓀀 seated man | suffix | the word is a **person** |
| Egyptian | 𓂻 walking legs | suffix | the word is a **motion verb** |

So a Sumerian scribe writing "the city of Ur" writes `URI₅` followed by `KI` — and
the reader knows it's a place *before* knowing which place. Egyptian readers can
tell a person-word from a motion-word at a glance, purely from the trailing sign.

**Your head glyphs are determinatives.** `· VIRGA` means "this word names a
rod-class thing." `· LAPIS` means "a stone-class thing." `· METALLUM`, "a metal."
That's not an approximation of a real feature — it *is* the real feature, and it's
why the language is learnable: a player who has decoded one `· LAPIS` word can
correctly guess the *category* of every other one they meet.

It also resolves the `ALTARE · TENEBRAE` question (see below), because Sumerian
proves determinatives can go on **either** end.

#### (iii) Isolating grammar — why order must carry the meaning

Latin can scramble word order (*puella rosam amat* / *rosam puella amat* both mean
"the girl loves the rose") because **case endings** mark who does what. Chinese
cannot: it has no inflection, so **position is the grammar** — 我打你 and 你打我 are
different sentences made of identical signs.

A glyph script has no endings to inflect. So Epigraphy is necessarily an
**isolating/analytic** language, and that is precisely why the fixed clause formula
(§4.1) isn't an arbitrary game rule — it's the only way a script like this *can*
encode roles. Order is grammar because there's nothing else to be grammar.

#### Verdict: what we're building

> **A logographic, isolating script that forms head-final compounds marked by
> determinatives, framed by fixed formulae.**

In plain terms: **Chinese-style compound words + Sumerian-style category markers +
Roman-style inscription formulae.** Every one of those is a real, attested system,
and together they make a language that a player can genuinely *learn to read*
rather than memorise.

### 4.3.1 Word-internal order: the head rule

One rule governs every rune word, with no exceptions:

> ### The head is the **last determinative-capable glyph** in the word.
> Everything before it qualifies it.

Scan the word right to left; the first glyph you meet that *can* be a determinative
is the head, and it names the word's category. Glyphs that can never be
determinatives — the **element/quality** glyphs (`FLAMMANS`, `TENEBRAE`, `CHAOS`,
`VITA`, `PLENUS`) — are skipped over, because they can only ever modify.

| Word | Scanning right to left | Head | Names |
|---|---|---|---|
| `FLAMMANS · VIRGA` | `VIRGA` is a determinative → stop | **VIRGA** (rod) | Blaze Rod |
| `INFERNUS · METALLUM` | `METALLUM` is a determinative → stop | **METALLUM** (metal) | Netherite |
| `CHAOS · CAELUM` | `CAELUM` is a determinative → stop | **CAELUM** (heavens) | Thunderstorm |
| `ALTARE · TENEBRAE` | `TENEBRAE` is an element, skip → `ALTARE` | **ALTARE** (structure) | Blackstone Altar |
| `INFERNUS · METALLUM · GLADIUS` | `GLADIUS` is a determinative → stop | **GLADIUS** (blade) | Netherite Sword |

**This is why `ALTARE · TENEBRAE` looks "backwards" and isn't.** It reads head-first
only because the glyph after it is a quality that could never head a word. Nothing
special is happening — the same single rule produces both orders.

The earlier formulation ("places prefix, materials suffix") described the *symptom*
and broke on the first word containing two determinative-capable glyphs:
`INFERNUS · METALLUM` would have been ambiguous between "a hellish place" and "a
hell-metal". The head rule resolves it — `METALLUM` is last, so it wins, and the
word means a metal.

The Sumerian precedent still holds: determinatives genuinely do appear on either
side of a word (`DINGIR` prefixes, `KI` suffixes). What the head rule adds is a
deterministic way to know *which* sign is doing the work when more than one could.

Player-facing, it stays teachable in one line:
- **Read to the end. The last real "kind of thing" word is what it is.**

Which glyphs may serve as determinatives is declared per-glyph in data — see
`AUTHORING.md` — so modders extend the system without touching code.

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

## 5. The visual system: chiselled stone at 16 × 16 (D14/D15/D17/D18)

Glyph art is generated, never hand-drawn, at **Minecraft's own item resolution**.

```
       VIRGA -> "VI"                blank
    ╱▔▔▔▔▔▔▔▔▔▔╲            ╱▔▔▔▔▔▔▔▔▔▔╲
   ╱   ╱▔▔╲     ╲ ← V      ╱            ╲    bare stone:
   │   ╲__╱      │         │             │   unknown glyph,
   │   ╱▔▔╲      │ ← I     │             │   empty codex slot,
   ╲ ▁▁▁▁▁▁▁▁▁  ╱ ← foot   ╲            ╱    uninscribed tablet
    ╲▁▁▁▁▁▁▁▁▁▁╱            ╲▁▁▁▁▁▁▁▁▁▁╱
      = word length
```

- **One continuous figure.** Two letter-forms stack and link at the centre; the foot
  hangs off the lower one. Nothing floats — the whole glyph is a single unbroken shape.
- **Two letters + a tally.** The forms carry the lemma's first two letters; the foot
  tallies its length, which is what keeps `VITA`, `VIRGA` and `VIGILIA` distinct despite
  sharing `VI`.
- **An octagonal tile** — corners chamfered by 3, transparent outside — so the
  silhouette reads as a cut stone rather than a sprite. It is **universal**: the outline
  never varies per glyph (D17).
- **Chiselled, not painted.** One height field and one top-left light give the tile's
  bevelled rim, the shadowed upper-left wall of every groove and the lit lower-right
  wall (D18).
- **The groove is inlaid** with a pigment: an authored `pigment` hex where the glyph has
  one, otherwise hashed from the lemma. Either way it is renormalised so every hue cuts
  to the same depth, and it is redundant reinforcement only — see §5.3.
- **Nothing comes within 2 px of the stone's edge**, chamfered corners included, and the
  audit fails the build if it does.
- **A blank tile** covers unknown glyphs, empty slots and uninscribed tablets.

**Full construction rule: [`GLYPH_SPEC.md`](GLYPH_SPEC.md).**

### 5.1 Why not pictographs

**If the symbol is a picture, there is nothing to decipher.** Sumerian began
pictographic and abstracted within a few centuries — that drift is what turned drawing
into **writing**. Pictography stays on tablet frames, block textures and structure
motifs, never on glyphs.

### 5.2 What five iterations taught

Each attempt failed for a recorded reason, and the pattern is the lesson:

| Attempt | Why it failed |
|---|---|
| Lattice path | tangled diagonals, strokes leaving the frame |
| Five rungs on a stem | every glyph a variation on one comb |
| Two bold marks | `VIRGA`/`VITA`/`VIGILIA` rendered identically |
| Three marks by width × thickness | still one shared skeleton |
| Heavy structural strokes | distinct but muddy, and notches escaped the border |

**Varying parameters of a shared skeleton never produced distinct symbols; varying
structure did — but only once it was kept light and continuous.**

### 5.3 Knowledge tier is depth, not tint

**Colour never carries anything on its own.** The groove's pigment is authored per glyph
(falling back to a hash of the lemma), but every pigment is renormalised to a single
luminance, so hue never changes how strongly a cut reads. Desaturate the whole atlas and
all 49 glyphs stay distinct — verified in the audit. Two glyphs may even share a hue. Colour is a second, faster channel onto an identity that shape
already carries in full.

| Tier | Rendered | Reads as |
|---|---|---|
| **0 · Unknown** | the blank tile — uncut stone | "a stone, meaning nothing" |
| **1 · Sighted** | the figure cut **shallow and unfilled** — bare stone-grey, no pigment | "seen, not yet taken down" |
| **2 · Learned** | cut to **full depth and inlaid** | complete, legible |

The ladder is **uncut → shallow and empty → deep and filled** — a *value* difference
before it is a colour one, so it reads identically to a colourblind player (D19).

---

## 6. Readable text at each tier

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

## 7. Design notes

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

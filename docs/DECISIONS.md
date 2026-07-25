# Design Decisions Log

The running record of settled design choices and the open questions still in play.
This is the source of truth; where another doc conflicts with a **Decided** entry
here, this file wins and that doc is queued for revision (see "Docs to reconcile").

Legend: ✅ Decided · 🔵 Leaning · ❓ Open

---

## Decided

### D5 ✅ Glyphs are a research/knowledge layer (Thaumcraft 1.7.10 "research")
Glyphs are **not** ritual ingredients you place at the altar. They are the mod's
*research* system: learning a glyph is knowledge that permanently unlocks your
**understanding of recipes and systems** that use it. Knowledge is **per-player
and permanent**, held in a capability (this resolves **Q1** to the capability-backed
"Model A", reframed as research). Discovery in the world (carvings, sky, tablets)
is how you gather that research; triangulating enough sightings *learns* the glyph.
The altar reads your knowledge capability to decide what you understand/can
attempt — you never lay glyph tablets on pedestals.

### D6 ✅ Systems are no-GUI; the reference layer is an allowed convenience
The **gameplay systems** — rituals, discovery/recording, learning, sky reading —
are strictly no-GUI (D1): every *action* happens in-world or on-item. Separately,
a **reference layer** is explicitly permitted purely for *looking things up*:
- **In-game documentation** — a Thaumonomicon-style guide that *populates as you
  learn glyphs and unlock recipes*. Reference only; you can play entirely from
  in-world cues without ever opening it. (This resolves **Q2**.)
- **JEI** — mirrors unlocked recipes when installed, for convenience.

The rule of thumb: **no GUI ever stands between you and *doing* something; a GUI may
exist only to *remember* what you've already done.**

### D0 ✅ Terminology (canonical — use everywhere)
- **Glyph** — a single **symbol**, mapping to one Latin word (`FLAMMANS`, `CHAOS`).
  The atomic unit; **discovered** in the world.
- **Rune word** — an **ordered sequence of 2–3 glyphs** naming exactly one concrete
  thing (`FLAMMANS · VIRGA` → Blaze Rod); **guessed** by the player and validated.
- **Inscription** — a full ritual written in the formula (D10/D11): several rune
  words in fixed clause order.
- **Runes** — the **whole system**: glyphs, rune words, and the grammar binding them.

Mechanically: *glyphs are discovered, rune words are guessed, inscriptions are
written.* A glyph must be discovered before it can be used in a guess, and **order
is meaningful at every level** (D10).

### D1 ✅ No GUI — in-world or on-item, with one minimal exception
No **block or machine GUIs**. Every world interaction and every piece of feedback
is either:
- **in-world** — block interactions, multiblocks, block state/visual changes,
  floating in-world text/holograms above blocks, particles, items dropping,
  transformations that happen physically in the world; or
- **on-item** — item tooltips, item name/NBT, item model/texture changes, and the
  single item-borne interface described in D9.

The rule bars *block/machine* screens outright — you never open a chest-like menu
on an altar, pedestal, lectern, or observatory; those are worked with physically,
in the world. All rituals and transformations remain strictly in-world.

### D9 ✅ The codex interface — 20 glyph slots, minimally invasive
The hand codex has an interface, but it is deliberately **not a menu**: it is a
single flat grid of **20 empty glyph slots**, and nothing else.

- Each slot **cycles through the glyphs the player has discovered** — you click or
  scroll a slot to step through your known symbols. Undiscovered glyphs never
  appear in the cycle, so the codex is physically incapable of expressing a glyph
  you haven't found.
- The player fills slots to write an **inscription** — rune words in formula order,
  separated by empty slots — and hits **submit** to test it (D7/D11).
- No inventory, no tabs, no item slots, no crafting grid, no scrollable tree — just
  the 20 slots and a submit action. It reads as an instrument, not a UI.
- 20 slots is sized to hold a **whole inscription** at once (five clauses of up to
  3 glyphs, plus separators), so the player works the full puzzle in one view rather
  than testing one word at a time. Segmentation is gap-delimited, left to right (Q8).

### D2 ✅ Glyph meaning is learned passively; rune word meaning is discovered actively
Two layers, and they resolve the "active vs. passive learning" question together:
- **Glyphs (vocabulary)** are learned **passively**: a glyph becomes readable once
  you accumulate enough *independent* sightings (`sightings_to_translate`), or via
  a Rosetta tablet. No minigame to learn a word.
- **Rune words (meaning)** are discovered **actively**: you hypothesise a 2–3 glyph
  combination in the hand codex and **submit** it to test whether it names a real
  thing (D7/D8). This is the mod's decode loop.

### D7 ✅ The hand codex: submit-to-test + seek mode
A held **codex** item is the player's research instrument (interface spec: **D9**).
Two functions:
- **Submit / validate.** The player composes a **rune word** — 2 or 3 glyphs they
  have **discovered** — and submits it. The codex answers whether that combination
  is a real, meaningful rune word, and if so what it names (D8). This is how players
  *test hypotheses* as they discover things in the world: wrong guesses cost nothing
  but are not confirmed; right guesses decode a piece of the language permanently.
  Only discovered glyphs can be entered at all (D9).
- **Seek mode.** The codex points toward the **nearest structure containing a glyph
  the player hasn't yet learned** — a dowsing needle, not a map marker. It gives a
  *direction* (and rough distance) to somewhere worth exploring; it never reveals
  *which* glyph is there or places a waypoint. This is the anti-frustration valve
  that keeps discovery from stalling without turning the mod into a quest tracker.

### D10 ✅ Order is meaningful — the Runes have a grammar
The Runes are a **language with structure**, not a bag of ingredient tokens. Order
carries meaning at both levels, and the pattern is learnable and followable the way
real epigraphic formulae are:

**Within a rune word** — the glyph sequence is part of the word's identity.
`FLAMMANS · VIRGA` names a blaze rod; `VIRGA · FLAMMANS` is not the same expression
and is not valid. Convention: **`QUALIFIER · HEAD`** — the last glyph names the kind
of thing, earlier glyphs narrow it. A third glyph inserts another qualifier *before*
the head, never after.

**Across an inscription** — a ritual is a fixed clause sequence, read left to right:

```
[ VESSEL ]  [ OFFERING ]  [ HOUR ]  [ SUBJECT ]  →  [ ISSUE ]
 the altar   catalysts     when      the input       the result
```

The Chaos Ingot inscription, literally:
```
ALTARE·TENEBRAE  FLAMMANS·VIRGA  CHAOS·CAELUM  INFERNUS·METALLUM → CHAOS·METALLUM
Blackstone Altar    Blaze Rod     Thunderstorm      Netherite        Chaos Ingot
```

Consequences: the rune word registry is indexed by **ordered sequence** (exact-match
lookup on submit); a grammatically wrong inscription is not a valid ritual; and
position tells the player what *category* of thing an undecoded word names, which is
what keeps guessing tractable rather than combinatorial. Full treatment in
`RUNES.md` §4.

### D11 🔵 The inscription frame: `OPUS` … `FIAT` (recommended, awaiting confirmation)
Real inscriptions are readable because invariant **frame formulae** bracket the
variable content — a Roman votive opens `I.O.M.` and closes `V.S.L.M.`. Proposal:
give Epigraphy the same, with two glyphs that appear on *every* ritual inscription
and nowhere else:

- **`OPUS`** ("the work / the rite") — opens the inscription, prefixing the vessel.
- **`FIAT`** ("let it be made") — opens the final clause, prefixing the result.

```
OPUS·ALTARE·TENEBRAE  FLAMMANS·VIRGA  CHAOS·CAELUM  INFERNUS·METALLUM  FIAT·CHAOS·METALLUM
└──── INVOCATION ───┘ └─ OFFERING ──┘ └── HOUR ───┘ └──── SUBJECT ───┘ └── CONSECRATION ──┘
```

*"The rite of the Dark Altar — by the Flaming Rod — when the Heavens turn to Chaos —
upon the Metal of Hell — let there be Chaos Metal."*

Rationale: the frame teaches the formula for free (being the most-seen glyphs, they
are learned first, and learning them teaches where inscriptions begin and where the
result lives); it makes the codex grid unambiguous about clause boundaries; and it
gives the mod a recognisable signature carved on every ruin — its own `V.S.L.M.`
Costs two lexicon slots. Lean alternative is five bare clauses with no frame.
Full treatment in `RUNES.md` §4.4. **Recommended; confirm to promote to ✅.**

### D15 ✅ Glyph construction — a two-letter mark in a class frame
**A glyph is a two-letter abbreviation of its word**, drawn as two bold marks on a
stem inside a frame naming its class. That is the whole rule. Full spec:
[`GLYPH_SPEC.md`](GLYPH_SPEC.md).

- **Two marks.** The lemma normalises to classical orthography (`PULVIS` → `PVLVIS`)
  and its **first two letters** become the upper and lower mark. Each mark's **width**
  (5 steps) and **shape** (bar, chevron up, chevron down, double bar, broken bar)
  identify one letter — 5 × 5 = **25 slots for the 23 letters**. Marks are **2 px
  thick**. **The letter→(width, shape) map is frozen at v1.**
- **Identity is frame × upper mark × lower mark** — three features to parse, not five
  faint rungs. The frame *disambiguates*: `VIRGA`, `VITA` and `VIGILIA` all abbreviate
  to `VI` and stay distinct as material, element and formula. Only a same-class
  same-abbreviation pair collides; the validator rejects it and the author supplies an
  explicit `mark`.
- **Symmetry is structural.** Marks draw outward from the stem in both directions, and
  frames pass through a `symmetrise` step, so a glyph *cannot* come out asymmetric.
- **Arms never leave the frame.** Each stops one pixel inside the frame's inner edge.

**Supersedes full-lemma transcription** (five rungs, one per letter, later with a
profile outline). That optimised the wrong thing: **players read glyphs constantly**,
since every rune-word guess means scanning the codex for the one they want. That is a
*recognition* task, and recognition wants **few, bold, specific** shapes — five faint
rungs made every glyph a variation on one comb. Two letters is ample (23 × 23 pairs ×
6 frames), and dropping to two marks frees the room to draw each boldly.

Roman inscriptions abbreviate exactly this way (`D.M.`, `I.O.M.`, `COS`). Once a
player knows the 23 letterforms they don't memorise glyphs, they **read** them — `CA`
is `CAELVM`.

**The trade, stated plainly:** a glyph now *names* its lemma rather than transcribing
it. That is the right way round — decipherment difficulty belongs in the **rune
words**, not in reading a symbol the player must pick out of a grid hundreds of times.

**Two constraints are load-bearing**, each found by an audit that failed first: both
mark rows must sit in the frame's straight band (frames taper only outside rows 8–24),
and widths must be two pixels apart with arms stopping one pixel inside the frame.
Adding a frame **requires re-running the distinctness audit** — every collision found
while developing this came from a frame intruding on the marks, never from the letter
map.

**Audited:** all 23 letters distinct in both mark positions in all six frames, every
lexicon glyph unique, every output vertically symmetric.

### D12 ✅ Rite types — not everything is an altar
A **rite type** is a grammar template plus a trigger, declared in data. Each is
announced by its own **invocation glyph**, exactly as Roman inscriptions announce
their type (`D.M.` funerary, `I.O.M.` votive) — the first sign tells you what kind
of rite you're reading.

| Invocation | Rite | Trigger | Batched |
|---|---|---|---|
| `OPUS` | The Work | altar + pedestals + pool | no |
| `MERSIO` | The Steeping | throw items into a fluid | **yes, full stacks** |
| `TACTUS` | The Touch | use item on item/block | no |
| `VIGILIA` | The Vigil | observe the sky | no |

Canonical example of a non-altar rite: *bone meal and sugar thrown into water under
a full moon yields Blue Bone Meal* — no structure, and whole stacks at once.

```
MERSIO·AQUA   OSSA·PULVIS, DULCIS·PULVIS   PLENUS·LUNA   FIAT·VITA·OSSA·PULVIS
```
*"The steeping of Water — bone-dust and sweet-dust — at the Full Moon — let there be
life-bone-dust."*

Rationale: batched, structureless rites give players a cheap, high-volume way to
meet the language long before they can build an altar, and they make the grammar
feel like a *language* rather than an altar-recipe format. Schemas in
`AUTHORING.md` §4–5.

### D13 ✅ Everything is datapack-authorable
The modding contract: **glyphs, rune words, rite types, recipes, and discovery
distribution are all JSON.** Only two things need Java — a genuinely new *condition
kind* and a genuinely new *trigger mechanic* — and both are registry entries whose
*use* remains pure data. Namespaced throughout, so third-party content composes with
`epigraphy:` content freely. Full guide: `AUTHORING.md`.

### D8 ✅ Glyphs hint in rune words of 2–3 words (compositional language)
Glyphs are never used as a single long sentence. The language is **compositional**:
a **rune word** of **2 or 3 glyphs names exactly one concrete thing** — an item, a
block, a world condition, or an output. A ritual is described as a small set of
such rune words, each hinting at one component.

```
ALTARE · TENEBRAE      → Blackstone Altar   (Altar + Darkness)
FLAMMANS · VIRGA       → Blaze Rod          (Flaming + Rod)
CHAOS · CAELUM         → Thunderstorm       (Chaos + Heavens)
INFERNUS · METALLUM    → Netherite          (Hell + Metal)
CHAOS · METALLUM       → Chaos Ingot        (Chaos + Metal)
```

Rune words are first-class data (`data/epigraphy/rune_words/*.json`), are what the codex
validates on submit, and are what ritual recipes reference. See `RUNES.md` §3.

### D3 ✅ Thaumcraft-style backlash everywhere
Dabbling has consequences *across the board*, not just on dark rituals. Attempting
a ritual whose glyphs you haven't translated — or mis-assembling one — risks
**backlash**: area effects, hostile/anomaly spawns, item loss, or a lingering
"corruption" the world remembers. Reading first isn't just efficient, it's safer.
This adds a **backlash system** to the ritual engine (`RITUALS.md`, pending revision).

### D4 ✅ Sky/constellation reading ships in v1
The "meets the stars" pillar is present at launch. The **Observatory** multiblock
and **Astrolabe** item let players read celestial glyphs from constellations at
night; celestial glyphs (`CAELUM`, `NOX`, …) are gated behind sky reading. This
pulls sky work forward from the roadmap's later phase into the core loop. Being a
no-GUI mod, the Observatory presents constellations **in-world** (projected/holo
above the structure), not on a screen.

---

## Open questions (current dialogue)

> **Q1 and Q2 are now resolved** — see **D5** and **D6** above. Remaining open items:

### Q3 🔵 Pedestal matching — multiset now, patterned geometry later
Leaning: v1 matches pedestal contents as an order-independent multiset (forgiving);
a later version adds position-sensitive patterns. Since glyphs are *not* placed at
the altar (D5), pedestals only ever hold catalyst items, which keeps this simple.

### Q4 🔵 One infusion fluid now, themed fluids later
Leaning: ship `liquid_starlight` as the sole infusion medium in v1; add themed
fluids (umbra, etc.) as tiers grow. No conflict with other decisions.

### Q5 🔵 Form of the in-game documentation (D6) — likely the codex itself
D7 gives the hand codex an item-borne interface, so the natural answer is that the
**codex is also the documentation**: the same held item you submit rune words into has
a reference section that fills in with learned glyphs, decoded rune words, and mastered
rituals. One item, one mental model, no separate guide book. *Leaning strongly this
way; confirm when convenient.*

### Q6 ❓ How is a ritual's backlash severity determined? (D3)
Options to weigh later: fixed per-recipe `backlash` field; scaled by how *unknown*
the attempted glyphs are (blind attempts hurt more); or a global "instability"
stat the world accumulates. *Leaning: per-recipe base severity, amplified by the
number of untranslated glyphs in the attempt.*

---

### Q8 ✅ Resolved by D10 — the grid is an inscription, read left to right
The 20 slots hold a **full ritual inscription** in formula order, gap-delimited:
contiguous filled slots form one rune word, an empty slot ends it, and the words
are read left to right as `VESSEL / OFFERING / HOUR / SUBJECT / ISSUE`.

```
[ALTARE][TENEBRAE][ ][FLAMMANS][VIRGA][ ][CHAOS][CAELUM][ ][INFERNUS][METALLUM][ ]…
└──── VESSEL ────┘   └──── OFFERING ──┘   └──── HOUR ───┘   └──── SUBJECT ─────┘
```

20 slots is sized for exactly this: five clauses of up to 3 glyphs plus separators.
Each word is validated independently, so a player can inscribe a whole ritual and
see which clauses land — and the inscription as a whole is only a valid ritual if
the clause order is right.

### Q9 ✅ Resolved — the head rule
**The head of a rune word is the last determinative-capable glyph; everything before
it qualifies.** Scan right to left, and the first glyph that *can* be a determinative
is the head. Element/quality glyphs (`FLAMMANS`, `TENEBRAE`, `CHAOS`, `VITA`,
`PLENUS`) can never be determinatives, so they are skipped.

This handles both orders with a single rule and no exceptions:
- `FLAMMANS · VIRGA` → `VIRGA` is a determinative → head. A rod.
- `ALTARE · TENEBRAE` → `TENEBRAE` is an element, skip → `ALTARE` → head. An altar.

So the original example was right, and reads head-first only because the glyph after
it could never head a word.

**Supersedes the earlier "places prefix, materials suffix" formulation**, which
described the symptom and broke on the first word containing two
determinative-capable glyphs: `INFERNUS · METALLUM` would have been ambiguous
between "a hellish place" and "a hell-metal". The head rule picks `METALLUM` and the
word means a metal. Found while authoring the worked vocabulary in `RUNES.md` §3.1.1.

The Sumerian precedent still stands — determinatives genuinely appear on both sides
(`DINGIR` prefixes, `KI` suffixes). The head rule adds a deterministic way to know
*which* sign is doing the work when more than one could.

### Q7 ❓ What does a *wrong* codex submission cost?
D7 says wrong guesses "cost nothing but aren't confirmed." Alternatives worth
weighing: consume a charge/ink resource per submission, add a cooldown, or feed
failed submissions into `instability` (D3). *Leaning: free but with a short
cooldown, so brute-forcing every glyph pair is tedious rather than optimal — see
`RUNES.md` §3.4 on combinatorial safety.*

---

## Docs reconciled with these decisions
All bodies now reflect D1–D8; the "revision pending" banners have been removed.
- `DESIGN.md` — no-GUI pillar (D1/D6), glyphs-as-research (D5), backlash in the loop (D3).
- `DISCOVERY.md` — recording/review is in-world + on-item (D1); glyphs framed as
  research (D5); sky reading is a v1 system (D4).
- `KNOWLEDGE.md` — capability = research store (D5); reference layer = in-game
  documentation + JEI (D6); backlash gate (D3).
- `RITUALS.md` — backlash system (D3); pedestals hold only catalysts, no glyphs (D5).
- `ROADMAP.md` — sky in v1 (D4), backlash phase, no custom *system* screens (D1),
  guide+JEI as the reference layer (D6).

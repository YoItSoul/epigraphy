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

### D15 ✅ Glyph construction — 16 × 16, one continuous figure chiselled in stone
**A glyph is one continuous figure** — two stacked letter-forms linking at the centre,
on a foot that tallies the word's letters — **chiselled into an octagonal 16 × 16 stone
tile** lit from the top-left. Full spec: [`GLYPH_SPEC.md`](GLYPH_SPEC.md).

- **16 × 16** matches Minecraft's item resolution.
- **Continuity is structural.** Every form touches the centre column at its top and
  bottom row, so stacking two links them automatically. The geometry *cannot* produce
  a loose piece — no floating pips, split bars or posts, and nothing to validate after
  the fact.
- **No frame.** At 16 × 16 a border costs a quarter of the usable area and was never
  carrying identity; removing it is what made 16 × 16 viable.
- **Two letters, not three.** Three six-row zones do not fit. Uniqueness survives
  because **the foot tallies word length**: `VITA` (4), `VIRGA` (5) and `VIGILIA` (7)
  all abbreviate to `VI` and remain three different tiles. The bar widens one step per
  letter up to three, then its ends turn up into a serif and the width restarts, so
  `letters = 2 + (width − 1) + 3 × serif`.
- **A 3 px margin, enforced.** No part of a glyph comes within three pixels of the
  stone's edge, chamfered corners included — measured in the audit, not eyeballed. It is
  **the tightest constraint in the system**: it sets the chamfer depth, sets the tally's
  ceiling at three, and is why the two letter-forms share a row. One form was redrawn for
  it — `H` (wedge), the only form that flares on its *first* row.
- **Stone, lit top-left.** Gradient plus deterministic grain. **The gradient is
  material, not information** — flatten it to one grey and nothing is lost, so the
  colour rule (D16) still holds.
- **A blank tile** — bare stone, no cuts, still an octagon — serves as unknown glyph,
  empty codex slot, and uninscribed tablet.

**Audited:** all 23 forms distinct; **49/49 lexicon glyphs unique, and still 49/49 with
colour stripped**; tightest margin to the stone's edge 3 px; all 23 forms distinct from each
other and clearing the margin in both slots they can occupy. The audit executes the *live*
renderer rather than a transcription of it, so those numbers cannot drift from the art.

### D20 ✅ 21 letters, 21 figures — no shared skeleton
Each letter is **its own figure** — a gable, a thorn, a grate, a diamond, a coffer, a
saltire — built from five shared carved **parts**: the stave (1 px upright), the bar
(lintel), the diagonal, the chevron, and the stone (2 × 2 block). What varies is which
parts a letter uses, how they join, and which side carries the weight. **Never size, and
never a one-row offset.**

Every pair difference is sayable aloud: *the diamond* against *the coffer*, *roof on the
post* against *floor under the post*, *two bars* against *three*, *strokes* against
*stones*. That is the whole test.

#### The rule that finally worked
Six earlier alphabets shared one skeleton across all 21 letters, and each rebuild made
the skeleton's decorations more elaborate. In the last of them — a full-height stave plus
a single mark — **about 12 of 17 pixels were identical in every letter**, leaving ~4 to
carry identity. Measured: **28 of 210 pairs differed by 4 px**; the whole alphabet spanned
only 4–16 px. That is why they looked the same.

> **A large fraction of each letter's pixels must be doing distinguishing work.**

Now: no mandatory shared skeleton. Shared *parts*, not a shared *frame* — which is also
what keeps the set reading as one script rather than 21 unrelated doodles.

#### Why strokes, not outlines
Several tables drew closed outlines, and pictures must be intricate to differ. Writing is
not made of outlines; it is made of strokes. Elder Futhark, Ogham, Tifinagh and Old Turkic
all use uprights, bars, diagonals and dots, and nothing curved — the same answer to the
same engineering problem.

#### Two instruments that were driving the design instead of judging it
The pixel threshold was wrong **twice, in opposite directions**. At **10 px** (calibrated
for dense outlines) every light form was excluded, which forced the heavy shapes. At
**4 px** it rubber-stamped an alphabet where 28 pairs differed by a single arm. It now
sits at **12 px** — about one whole carved part, the smallest reliably nameable difference
at 16 px.

Two further instrument bugs each wasted a review: **row width could not see which side a
mark was on** (the audit records row *extents* now), and **the contact sheet kept
mirroring asymmetric forms** after symmetry had been dropped, so a whole visual pass was
worthless.

> **A metric that agrees with you is worth re-deriving.**

#### Dropped along the way
**Mirror symmetry** and **forced continuity** are no longer rules — that is what lets a
letter lean, corner, or carry its weight on one side, and lets `I` (the horns) and `M`
(three stones) be drawn in separate pieces. Symmetry now belongs to the *stone*: the
octagonal tile and the tally foot, which keeps a line of glyphs reading as a course of
cut blocks.

**Audited:** 21 forms, tightest pair **12 px** (was 4), median ~21, nothing below 12; mean
ink **15 px** (was 17 — distinctness was *not* bought with density); no shared footprints;
every form clears the 3 px margin in both slots; the 49-lemma lexicon renders 49 distinct
tiles, and 49 distinct desaturated.

**Known rough edges:** stacked word tiles are inherently busy at 16 px — two figures plus
a tally — though far more legible now that the two forms no longer share a spine.
`REGNVM` (R over E) stacks five horizontal bars: unique, but the least elegant composition
in the lexicon, and a language-level quirk rather than a letterform flaw. `P` (the hammer)
is the least self-evident single form.

### D17 ✅ The tile is an octagon, and the silhouette is universal
The tile is the 16 × 16 square with its **four corners chamfered by 2** — 244 of 256
pixels, transparent outside. **The margin chose the depth, not taste:** a deeper octagon
and a 3 px margin eat the same corners, and at chamfer 3 the top and bottom rows shrink
to six usable columns — too few for the foot. Chamfer 2 leaves eight, and is the deepest
cut the figure still clears. Going deeper means redrawing the letter-forms shorter. The silhouette reads as a cut stone rather than a sprite,
and its bevelled rim comes free from the same lighting rule as the grooves (D18).

**The silhouette does not vary per glyph.** Tying the side count to word length was
considered and rejected on three grounds, in order of how much they hurt:

1. **Length is already spoken for** by the foot tally. Spending the silhouette — the
   most visible feature at inventory scale — on an already-encoded variable buys
   nothing.
2. **16 px cannot hold the alphabet of shapes.** Square, octagon and heavy-chamfer
   octagon are the three that survive; pentagons and heptagons are mush at this size,
   and a true hexagon points top and bottom, exactly where the forms and foot live.
3. **Latin lengths cluster** at 4–7, so most glyphs would land on the same two or three
   shapes and the encoding would read as random rather than systematic.

**Deferred, not dead (Q10):** the variable actually worth the silhouette is
**grammatical class** — thing / place / condition — because that is what a player parses
first when guessing a rune word. Three or four silhouettes would put the grammar on the
outline where it can be read across a wall of carvings. It waits until the lexicon's
classes are settled, since freezing it into art is hard to undo.

### D18 ✅ Depth from one light; pigment hashed from the name
**One height field and one light produce all of the depth.** Void sits at −1, the
incised stroke at 0, the stone surface at 1; each pixel is compared against its three
up-left and three down-right neighbours, diagonals double-weighted. Higher than up-left
turns into the light; higher than down-right turns away. That single rule gives the
tile's bevelled rim, the chamfered corners, the shadowed upper-left wall of every groove
and the lit lower-right wall — nothing special-cased, and a two-pixel stem falls out as
a true V-groove.

**A glyph is inked in three layers**, each with its own pigment: the first letter's colour,
the second letter's colour, and — on the foot — the word's. So a word is **two-toned by
construction**, words sharing a letter share a band of colour, and the base still says
which family the word belongs to.

**Letter colour is a safer use of colour than the word-hash it replaced**, not a riskier
one: it reinforces something the shape *already says* — which letter this is — rather than
asserting anything new. That is exactly the redundant-reinforcement footing D16 permits.

`render()` returns a **layer map** rather than a bitmask (0 uncut, 1 first letter, 2
second, 3 foot). Non-zero still means "cut", so every consumer testing truthiness is
unchanged while the painter can ink each layer separately.

**The word's pigment comes from one of two sources in order.** *Static* — an authored
`pigment` hex on the glyph, chosen by hand. *Dynamic* — FNV-1a over the lemma → hue, with
a little saturation jitter. Anything missing, malformed or out of range falls through to
the hash **silently**; a colour typo must never be able to fail a datapack load. Blood
Magic's runes are the reference: a symbol sunk into stone whose inlay tells one rune from
another across a room.

**The fallback is the point, not the safety net.** It lets a modder add fifty glyphs in
an afternoon and get stable, per-word colour with no art decisions at all — nothing is
stored, so the same lemma is the same colour in every world, forever. The static layer
exists so the words that deserve a colour get the right one. All 49 shipped glyphs are
authored, grouped into families (`GLYPH_SPEC.md` §6.2): every fire word in one band of
orange, every implement in one band of worn metal, so words that belong together look
like they belong together.

**Both paths are renormalised to one fixed relative luminance** (62 of 255, against stone
running 110–195), so a yellow groove and a blue groove cut exactly as deep. **The author
picks the hue; the renderer keeps the depth** — which is what makes the static layer
safe, since a hand-picked palette cannot brighten one glyph into prominence or sink
another into invisibility. Measured across the lexicon the spread is 61.6–62.4, under one
percent, authored and hashed alike. A consequence worth stating: `#88AA88` and `#AACCAA`
are the same pigment.

**This does not weaken D16.** Colour here is *redundant reinforcement*: a second, faster
channel onto an identity that shape already carries in full. Desaturate the whole atlas
and all 49 glyphs stay distinct — verified in the audit against the authored palette, not
just the hash. Two glyphs are even allowed to *share* a hue, and two implements nearly
do; nothing breaks, because nothing was resting on it. If a future change ever makes two
glyphs tell apart *only* by hue, that change is wrong.

### D19 ✅ Knowledge tier is depth, not tint
This closes the gap D15 left open when the frame was removed.

| Tier | Rendered | Reads as |
|---|---|---|
| **0 · Unknown** | the blank tile — uncut stone | "a stone, meaning nothing" |
| **1 · Sighted** | the full figure cut **shallow and unfilled** — weak bevel, groove holds bare stone-grey instead of pigment | "seen, not yet taken down" |
| **2 · Learned** | cut to **full depth and inlaid** with the word's pigment | complete, legible |

The ladder runs **uncut → shallow and empty → deep and filled**. It is a *value*
difference before it is a colour one, so it survives desaturation and reads identically
to a colourblind player — and "you have seen the carving but not yet taken the rubbing"
is exactly what partial decipherment should look like.

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

### Q10 🔵 Should the tile silhouette encode grammatical class? (D17)
One universal octagon ships in v1. The open proposal is to let the outline carry
**thing / place / condition** — three or four silhouettes, readable at a glance across a
wall of carvings, reinforcing the head rule (Q9) that players must already parse.

Not blocking: it wants the lexicon's classes settled first, because freezing a class
encoding into art is hard to undo. Revisit before Phase 3.

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

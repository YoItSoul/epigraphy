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
- **Rune word** — a **set of 2–3 glyphs** naming exactly one concrete thing
  (`FLAMMANS · VIRGA` → Blaze Rod); **guessed** by the player and validated.
- **Runes** — the **whole system**: the full body of glyphs and rune words.

Mechanically: *glyphs are discovered, rune words are guessed.* A glyph must be
discovered before it can be used in a guess.

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
- The player fills slots to spell out **rune words** (2–3 glyphs each) and hits
  **submit** to test validity (D7).
- No inventory, no tabs, no item slots, no crafting grid, no scrollable tree — just
  the 20 slots and a submit action. It reads as an instrument, not a UI.
- 20 slots is sized to lay out a **whole ritual's worth** of rune words at once
  (~5–7 words × 2–3 glyphs), so the player can work a full puzzle in one view
  rather than testing one word at a time. **See Q8** for exactly how submit
  segments the grid into words.

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

### D8 ✅ Glyphs hint in rune words of 2–3 words (compositional language)
Glyphs are never used as a single long sentence. The language is **compositional**:
a **rune word** of **2 or 3 glyphs names exactly one concrete thing** — an item, a
block, a world condition, or an output. A ritual is described as a small set of
such rune words, each hinting at one component.

```
ALTARE · TENEBRAE      → Blackstone Altar   (Altar + Darkness)
FLAMMANS · VIRGA       → Blaze Rod          (Flaming + Rod)
CAELUM · CHAOS         → Thunderstorm       (Heavens + Chaos)
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

### Q8 ❓ How does submit segment the 20 slots into rune words? (D9)
The grid holds ~5–7 rune words at once, so submit needs to know where one word ends
and the next begins. Options:
- **Gap-delimited (leaning).** Contiguous filled slots form a word; an empty slot
  ends it. `[FLAMMANS][VIRGA][ ][INFERNUS][METALLUM]` = two words. Zero extra UI,
  reads naturally left-to-right.
- **Fixed rows.** The 20 slots are 5 rows of 4; each row is one word (2–3 used,
  rest empty). Unambiguous, but wastes slots and feels more form-like.
- **One word at a time.** Only the first contiguous group is evaluated per submit.
  Simplest to build, but throws away the point of having 20 slots.

*Leaning gap-delimited*, with each word validated independently so a player can
submit five guesses and see which ones land.

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

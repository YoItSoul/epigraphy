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

### D1 ✅ No GUI — in-world or on-item only
The mod ships **no custom screens/menus of its own**. Every interaction and every
piece of feedback is either:
- **in-world** — block interactions, multiblocks, block state/visual changes,
  floating in-world text/holograms above blocks, particles, items dropping,
  transformations that happen physically in the world; or
- **on-item** — item tooltips, item name/NBT, item model/texture changes.

This explicitly includes the **glyph discovery & learning system**: there is no
Codex *screen*. The "codex" becomes a physical journal item and/or an in-world
lectern that renders glyphs as floating relief, with tier/meaning conveyed through
item tooltips and in-world visual state. See open question **Q1** for exactly how
knowledge is surfaced and stored.

### D2 ✅ Translation is passive triangulation (Tier 1 → Tier 2)
A glyph translates automatically once the player accumulates enough *independent*
sightings (its `sightings_to_translate`). No decode mini-game. Rosetta tablets
remain a shortcut. Keeps friction low and ships fast. (`GLYPHS.md` §1, `DISCOVERY.md` §3.)

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

### Q5 ❓ Form of the in-game documentation (D6)
The reference guide populates as you learn — but what *is* it? A held **guide book**
(a screen, acceptable since it's reference-only), or an in-world **lectern that
projects** entries as holograms (purer no-GUI)? *Leaning: a lightweight guide book,
since it's read-only reference and mirrors what JEI shows. Confirm when convenient.*

### Q6 ❓ How is a ritual's backlash severity determined? (D3)
Options to weigh later: fixed per-recipe `backlash` field; scaled by how *unknown*
the attempted glyphs are (blind attempts hurt more); or a global "instability"
stat the world accumulates. *Leaning: per-recipe base severity, amplified by the
number of untranslated glyphs in the attempt.*

---

## Docs reconciled with these decisions
All bodies now reflect D1–D6; the "revision pending" banners have been removed.
- `DESIGN.md` — no-GUI pillar (D1/D6), glyphs-as-research (D5), backlash in the loop (D3).
- `DISCOVERY.md` — recording/review is in-world + on-item (D1); glyphs framed as
  research (D5); sky reading is a v1 system (D4).
- `KNOWLEDGE.md` — capability = research store (D5); reference layer = in-game
  documentation + JEI (D6); backlash gate (D3).
- `RITUALS.md` — backlash system (D3); pedestals hold only catalysts, no glyphs (D5).
- `ROADMAP.md` — sky in v1 (D4), backlash phase, no custom *system* screens (D1),
  guide+JEI as the reference layer (D6).

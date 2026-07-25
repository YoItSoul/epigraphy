# Design Decisions Log

The running record of settled design choices and the open questions still in play.
This is the source of truth; where another doc conflicts with a **Decided** entry
here, this file wins and that doc is queued for revision (see "Docs to reconcile").

Legend: ✅ Decided · 🔵 Leaning · ❓ Open

---

## Decided

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

### Q1 ❓ How is player knowledge stored & surfaced? (forced by D1)
Two coherent models; this choice cascades into discovery, rituals, and JEI:

- **A — Capability-backed, item/world-surfaced.** Knowledge is per-player data
  (survives death/item loss). Physical items & blocks are how you *earn* and *view*
  it: recording a carving mints a rubbing-tablet whose tooltip shows the glyph;
  a lectern renders known glyphs in-world. The altar reads the player's knowledge
  capability directly. *Safer, permanent, standard.*
- **B — Item-embodied knowledge.** Knowledge literally lives on **physical glyph
  tablets** you collect; there is no hidden per-player progress. Your library is a
  physical chest of tablets. Rituals are gated by **placing the required glyph
  tablets on/around the altar** — the glyph "sentence" becomes something you
  physically lay out. *Maximally immersive and on-theme for no-GUI, but losing
  tablets loses progress, and multiplayer/teaching get tricky.*

A hybrid is possible (capability tracks what you've *ever* learned for safety, but
rituals still require the physical tablets present). **Needs your call.**

### Q2 ❓ JEI — keep it, given the no-GUI rule? (conflict with D1)
Your original brainstorm wanted recipes to "unlock in JEI for easy remembering,"
but JEI *is* a GUI. Options:
- **Keep JEI as an optional external convenience.** The mod itself ships no GUI;
  JEI is a third-party browser the player opted into, and unlocks still gate it.
  The no-GUI rule governs *our* content, JEI is a lens on top. *Honors your
  original ask; mild philosophical asterisk.*
- **Drop JEI entirely.** Fully committed to no-GUI. "Remembering" happens through
  your physical tablet/lectern collection and in-world references instead of a
  recipe browser. *Purest, but loses the convenience you originally wanted.*
- **In-world "recipe" reference.** No JEI; instead a discovered ritual can be
  "remembered" by inscribing a physical **ritual tablet** that, placed at a
  lectern, projects the required layout in-world. *No-GUI-native remembering.*

### Q3 🔵 Pedestal matching — multiset now, patterned geometry later
Leaning: v1 matches pedestal contents as an order-independent multiset (forgiving);
a later version adds position-sensitive patterns. If Q1 resolves to **B**, glyph
tablets may share the pedestal ring or get their own **glyph-ring**, which nudges
this toward caring about geometry sooner. Revisit after Q1.

### Q4 🔵 One infusion fluid now, themed fluids later
Leaning: ship `liquid_starlight` as the sole infusion medium in v1; add themed
fluids (umbra, etc.) as tiers grow. No conflict with other decisions.

---

## Docs to reconcile after Q1/Q2 resolve
- `DISCOVERY.md` — remove Codex-*screen* language; recast recording/review as
  in-world + on-item per D1 and the Q1 outcome. Sky reading is now v1 (D4).
- `KNOWLEDGE.md` — recast the "Codex UI" surface; add the D3 backlash gate; settle
  storage model per Q1; update the JEI section per Q2.
- `RITUALS.md` — add the D3 backlash system; adjust pedestal/glyph handling per Q1.
- `DESIGN.md` — fold D1 (no-GUI) into the pillars; note D3 backlash in the loop.
- `ROADMAP.md` — move sky to v1 (D4), add a backlash phase, drop any custom-screen
  work, mark §6 decisions resolved.

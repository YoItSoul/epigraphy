# Knowledge, Research & Gating

The connective tissue: the per-player data that remembers what each player has
sighted, learned, and unlocked, and the rules that turn that data into gated
understanding, gated rituals, and a gated reference layer. This is where the
"knowing ≠ having" pillar is enforced.

**Framing (D5):** glyphs are the mod's **research** system — think Thaumcraft
1.7.10, but the "research" is done by *discovering and reading the world*, not by a
minigame. Learning a glyph is permanent per-player knowledge that unlocks your
*understanding* of the recipes and systems that use it. Nothing here is a screen
you fight through to *play* (D1); the only screens that exist are the read-only
reference layer (D6).

---

## 1. The knowledge capability

Per-player state lives in a Forge **Capability** attached to the player, persisted
in player NBT, and synced to the client. It is permanent — death and item loss
never erase it.

```
PlayerKnowledge {
  // Per-glyph research progress
  Map<GlyphId, GlyphProgress> glyphs

  // Per-ritual progress
  Set<RitualId> understood        // you've learned enough glyphs to read its instructions
  Set<RitualId> mastered          // Tier 3: you've performed it / obtained its result

  // Per-result-item unlocks (obtaining an item counts, not only crafting it)
  Set<ItemId> itemsObtained

  // Backlash bookkeeping (D3)
  int  instability                // rises with reckless attempts, decays over time
}

GlyphProgress {
  int  independentSightings
  Set<SightingSource> sources     // dedupe: which carvings/sky/tablets counted
  boolean learned                 // Tier 2 reached (triangulated OR rosetta)
}
```

Derived tiers (not stored redundantly):

- **Glyph Tier 0 — Unknown** — no `GlyphProgress` entry.
- **Glyph Tier 1 — Sighted** — entry exists, `learned == false`.
- **Glyph Tier 2 — Learned** — `learned == true`. The glyph is now readable
  everywhere and its knowledge unlocks understanding of recipes/systems using it.
- **Ritual Tier 3 — Mastered** — in `mastered`, *or* its result is in
  `itemsObtained`.

---

## 2. Sighting sources & dedupe

A "sighting" counts only if *independent*. `SightingSource` identifies its origin
so the same carving can't be farmed:

- `carving:<blockpos+dimension>` — a specific carving block instance.
- `sky:<glyphId>` — a constellation reading (the sky is one source per glyph).
- `tablet` — studying a tablet (each consumed tablet is one-shot).

`independentSightings == sources.size()`. A glyph becomes **Learned** when
`sources.size() >= glyph.sightings_to_translate`, or immediately when a Rosetta
tablet sets `learned = true`.

---

## 3. How knowledge is surfaced (no-GUI + reference layer)

Two distinct surfaces, per D1/D6:

**In-world / on-item (the systems — no GUI):**
- **Carvings** you've recorded render their glyph as relief in-world; once *learned*
  they display floating translated text when looked at (like a readable sign),
  otherwise a `???`/raw symbol.
- **Tablets** carry a glyph; their **tooltip** shows `???` while sighted and the
  translated lemma/gloss once learned.
- **The altar** communicates via in-world feedback (particles, the "sputter" on a
  gated attempt), never a menu.

**Reference layer (read-only convenience — a permitted GUI, D6):**
- **In-game documentation** — a Thaumonomicon-style guide that *populates as you
  learn glyphs and master rituals*. It is strictly a lookup of what you already
  did; you can play without ever opening it. (Form under discussion — see
  `DECISIONS.md` Q5.)
- **JEI** — mirrors *mastered* recipes when installed (§6).

**Sync:** server is authoritative; every mutation happens server-side and pushes a
delta packet to that player. The client copy drives translated-text rendering, the
documentation contents, and JEI filtering. Full sync on login/respawn/dimension
change.

---

## 4. The understanding gate (Tier 2 — reading & attempting)

Two gates use research, and keeping them distinct matters:

**(a) Ritual understanding** — *do you know this ritual exists and roughly how?*
A ritual becomes **understood** (its instructions readable in the documentation,
and its in-world hints legible) when the player has **learned** (Tier 2) the glyphs
that define it — that's the research paying off. Partially-learned rituals read
partially, with untranslated glyphs showing as `???`, nudging what to research next
(`GLYPHS.md` §3). Some rituals may also be seeded directly by looting a ritual
tablet.

**(b) Ritual attemptability** — *will the altar even try, and how dangerous is it?*
When the altar core searches for a match, it checks research:
- If the player has **learned all** the recipe's glyphs → the ritual runs cleanly.
- If some glyphs are **only sighted or unknown** → the player may still *attempt* a
  ritual whose physical setup happens to be correct, but it is a **blind attempt**
  and triggers **backlash** (D3, §5) scaled by how many glyphs are untranslated.
  Reckless experimentation is possible — it just bites back.

> Both behaviours are server-config tunable: `require_learning_to_attempt` (if a
> pack wants blind attempts to simply fizzle instead of backlash) and
> `reveal_instructions_on_sighting`.

---

## 5. Backlash (D3)

Blind or botched rituals have consequences everywhere, not just for dark magic:

- **Trigger:** an altar fires with a valid physical setup but the player hasn't
  *learned* all the ritual's glyphs, or a partial/ambiguous match resolves badly.
- **Severity** (leaning, `DECISIONS.md` Q6): a per-recipe base amplified by the
  count of untranslated glyphs in the attempt, and by the player's current
  `instability`.
- **Effects (escalating):** wasted ingredients → hostile/anomaly spawns at the
  altar → lingering area corruption the world remembers → a rise in the player's
  `instability`, which makes the *next* reckless attempt worse. `instability`
  decays slowly with time and with successful, *understood* rituals.
- **Design intent:** you *can* stumble onto a ritual by copying a build you saw,
  but doing so before you can *read* it is genuinely risky — so the incentive is
  always to research first.

---

## 6. The reference gate (Tier 3 — documentation & JEI)

Understanding a ritual (Tier 2) shows you *described instructions* — never the
exact bill of materials. The precise recipe (pedestal counts, exact input, fluid
amount, conditions) is written into the reference layer only at **Tier 3**:

- A recipe appears in the **in-game documentation's** "mastered" section, and is
  **browsable in JEI**, only once it is Tier 3 for the player — its id in
  `mastered`, or its result in `itemsObtained`.
- Locked recipes are **hidden** (not greyed), so both surfaces stay a *reward* that
  fills in as you accomplish things — mirroring how the world's carvings fill in as
  you explore.
- JEI's index is client-side; the plugin reads the synced knowledge copy and
  re-filters on every Tier-3 unlock packet. If JEI is absent, everything still
  works — the in-game documentation is the fallback reference.

**Tier-3 unlock triggers** (server marks → sync → documentation/JEI refresh):
1. **Performing** the ritual successfully (`RITUALS.md`).
2. **Obtaining** the result item by any means — an inventory/pickup hook adds it to
   `itemsObtained`. Honors "OBTAIN the item **or** perform the ritual."

---

## 7. Edge cases

- **Creative/commands** granting items unlock the reference by design; we don't
  police creative.
- **Multiplayer:** research is strictly per-player. A future "teaching" mechanic
  could let a learned player inscribe tablets to seed sightings for others.
- **Datapack drift:** unknown glyph/recipe ids in saved progress are tolerated and
  ignored on load; a mastered ritual whose recipe contents change stays mastered
  (you mastered *that ritual*; its documentation reflects current data).

---

## 8. What the code must provide (summary for ROADMAP)

- `PlayerKnowledge` capability + attach/persist/sync (action packets C2S; state
  packets S2C).
- Derivation helpers: `glyphTier(id)`, `isRitualUnderstood(recipe)`,
  `attemptResult(recipe, player)` → `{clean | blind(untranslatedCount) | no_match}`,
  `isRitualMastered(recipe)`.
- Mutations: `recordSighting(source, glyphId)`, `applyRosetta(glyphId)`,
  `markUnderstood(id)`, `masterRitual(id)`, `markItemObtained(itemId)`,
  `addInstability(n)` / decay tick.
- Hooks: inventory-acquisition listener (Tier-3 by obtaining), ritual-success and
  ritual-backlash callbacks, record/study interactions.
- The reference layer: an in-game documentation model populated from knowledge, and
  a JEI plugin filtering `epigraphy:infusion` recipes by `isRitualMastered`.

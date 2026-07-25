# Knowledge & JEI Gating

The connective tissue: the per-player data that remembers what each player has
sighted, translated, and unlocked, and the rules that turn that data into gated
instructions and gated JEI recipes. This is where the "knowing ≠ having" pillar is
enforced.

---

## 1. The knowledge capability

Per-player state is stored in a Forge **Capability** attached to the player entity,
persisted in player NBT, and synced to the client.

Conceptual shape:

```
PlayerKnowledge {
  // Per-glyph progress
  Map<GlyphId, GlyphProgress> glyphs

  // Per-ritual progress
  Set<RitualId> pagesRevealed     // player has the readable instruction page
  Set<RitualId> pagesUnlocked     // Tier 3: exact recipe in JEI

  // Per-result-item unlocks (obtaining an item unlocks its recipe even w/o page)
  Set<ItemId> itemsObtained
}

GlyphProgress {
  int  independentSightings       // counts toward translation
  Set<SightingSource> sources     // dedupe: which carvings/sky/tablets counted
  boolean translated              // Tier 2 reached (via sightings OR rosetta)
}
```

Tiers are derived, not stored redundantly:

- **Glyph Tier 0** — no `GlyphProgress` entry.
- **Glyph Tier 1 (Sighted)** — entry exists, `translated == false`.
- **Glyph Tier 2 (Translated)** — `translated == true`.
- **Ritual Tier 3 (Unlocked)** — `pagesUnlocked` contains it, *or* `itemsObtained`
  contains its result.

---

## 2. Sighting sources & dedupe

A "sighting" only counts if it is *independent*. `SightingSource` identifies where
a sighting came from so the same carving can't be farmed:

- `carving:<blockpos+dimension>` — a specific carving block instance.
- `sky:<glyphId>` — the constellation reading (counts once; the sky is one source).
- `tablet` — studying a tablet (each consumed tablet is inherently one-shot).

`independentSightings` is `sources.size()`. Translation fires when
`sources.size() >= glyph.sightings_to_translate`, or immediately when a Rosetta
sets `translated = true`.

---

## 3. Sync & rendering

- **Server is authoritative.** All mutations (record, study, translate, unlock)
  happen server-side in response to player actions/rituals.
- On change, the server sends a small **sync packet** (the delta) to that player.
- The client copy drives: the Codex UI, whether carvings/tablets/ritual pages
  render translated text vs. `???`, and the JEI plugin's recipe visibility.
- On login/respawn/dimension change, a full sync is sent.

---

## 4. The instruction gate (Tier 2 — attempting rituals)

Two independent gates use knowledge, and it's important to keep them distinct:

**(a) Page revelation** — *may the player read the instructions?*
A ritual page is revealed to a player when they have **sighted** (Tier 1+) every
glyph in the ritual's `glyphs` array, OR they loot/are-given the page item.
Rationale: once you've *seen* all the symbols a ritual uses, the game is willing to
show you they *combine into something* — but the page only reads clearly for the
glyphs you've actually *translated* (Tier 2). Partially-translated pages are a
deliberate nudge toward what to decipher next (`GLYPHS.md` §3).

**(b) Ritual attemptability** — *may the altar even try?*
When the altar core searches for a matching recipe, it applies a knowledge check.
The default (tunable) rule: the player must have **translated** (Tier 2) every
glyph in the recipe. Reading the world's instructions is a prerequisite to
commanding it. If unmet, the altar sputters and hints at the untranslated glyph.

> Both gates are configurable (a server config: `require_translation_to_attempt`,
> `reveal_pages_on_sighting`) so packs can make the mod harder or softer without
> touching recipes.

---

## 5. The JEI gate (Tier 3 — browsing recipes)

The mod ships a **JEI plugin** that registers an `epigraphy:infusion` recipe
category, but every infusion recipe is filtered through player knowledge:

- A recipe is **visible/browsable** in JEI only if it is Tier 3 for the player —
  i.e. its id is in `pagesUnlocked` **or** its result item is in `itemsObtained`.
- Locked recipes are hidden entirely (not greyed), so JEI stays a *reward*: it
  fills in as you accomplish things, mirroring how the Codex fills in as you
  explore. A player's JEI is a portrait of what they've actually done.
- Because JEI's index is built client-side, the plugin reads the synced client
  knowledge copy and re-filters whenever a Tier-3 unlock packet arrives.

Unlock triggers (server→ marks Tier 3 → sync → JEI refresh):
1. **Performing** the ritual successfully (`RITUALS.md` §4 step 5).
2. **Obtaining** the result item by any means — an inventory-tick / pickup hook
   adds the item id to `itemsObtained`. This honors "OBTAIN the item **or** perform
   the ritual."

> Compatibility note: if JEI is absent, all gating still functions — it simply has
> no recipe browser to gate. The Codex remains the in-game reference; JEI is the
> convenience layer on top.

---

## 6. Anti-cheese & edge cases

- **Creative/commands** can grant items; obtaining them unlocks JEI by design
  (creative is not something we police).
- **Multiplayer:** knowledge is strictly per-player. One player translating `CHAOS`
  does not translate it for the party — but a translated player can *carve* glyphs
  for others (a future "teaching" mechanic: inscribe a tablet from a known glyph).
- **Removing a mod-added glyph** (datapack change) leaves orphaned progress; the
  capability tolerates unknown ids (ignored on load).
- **Recipe changes:** if a recipe's id persists but its contents change, a prior
  Tier-3 unlock still stands (you unlocked *that ritual*, and its JEI page just
  reflects current data).

---

## 7. What the code must provide (summary for ROADMAP)

- A `PlayerKnowledge` capability + attach/persist/sync (packets both directions
  for actions; server→client for state).
- Derivation helpers: `glyphTier(id)`, `isRitualAttemptable(recipe)`,
  `isRitualUnlocked(recipe)`, `isPageRevealed(recipe)`.
- Mutation entry points: `recordSighting(source, glyphId)`,
  `applyRosetta(glyphId)`, `revealPage(id)`, `unlockRitual(id)`,
  `markItemObtained(itemId)`.
- Hooks: inventory-acquisition listener (Tier-3 by obtaining), ritual-success
  callback, record/study interactions.
- A JEI plugin that filters `epigraphy:infusion` recipes by `isRitualUnlocked`.

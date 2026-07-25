# Architecture & Roadmap

How the design in the other docs becomes a buildable Forge 1.20.1 mod. This is the
bridge from "what we want" to "what to write, in what order."

---

## 1. Target & dependencies

- **MC / Forge:** 1.20.1, latest stable Forge (47.x).
- **Mappings:** Official (Mojmap).
- **Java:** 17.
- **Required at runtime:** none beyond Forge.
- **Soft/optional deps:**
  - **JEI** — recipe browser + the knowledge-gated plugin (`KNOWLEDGE.md` §6). The
    mod functions fully without it.
  - **Patchouli** is a *candidate* backing for the read-only in-game documentation
    (D6, `DECISIONS.md` Q5). It is reference-only and never gates gameplay, so
    using it does not violate the no-GUI rule (D1); the alternative is an in-world
    lectern projection. Undecided — see Q5.

---

## 2. Package layout

```
com.epigraphy
├─ Epigraphy.java                 // @Mod entry, common setup, registry bootstrap
├─ registry/                      // DeferredRegisters
│   ├─ EpiBlocks · EpiItems · EpiFluids · EpiBlockEntities
│   ├─ EpiRecipes  (infusion recipe type/serializer)
│   ├─ EpiFeatures (carving worldgen)
│   └─ EpiGLMs     (global loot modifiers: tablet drops)
├─ rune/                          // the language layer
│   ├─ Glyph · GlyphManager       //   symbols; datapack loader for data/*/glyphs/
│   └─ RuneWord · RuneWordManager //   2-3 glyph sets; loader for data/*/rune_words/
│                                 //   + lookup by unordered glyph set (submit validation)
├─ knowledge/                     // PlayerKnowledge capability, tiers, sync packets
│   ├─ PlayerKnowledge · KnowledgeProvider · KnowledgeTier
│   └─ net/  (SyncKnowledgePacket, RecordSightingC2S, …)
├─ ritual/                        // InfusionRecipe, condition registry, altar logic
│   ├─ InfusionRecipe · InfusionRecipeSerializer
│   ├─ condition/  (Condition, WeatherCondition, TimeCondition, …)
│   └─ AltarBlockEntity · RitualRunner
├─ block/  · item/  · fluid/      // concrete registry objects & block-entities
├─ world/                         // GlyphCarvingBlock(+BE), feature, constellation/sky logic
├─ doc/                           // in-game documentation model, populated from knowledge
├─ client/                        // in-world renderers (readable carvings, sky projection,
│                                 //   ritual FX), translated-text resolver, doc reader
└─ compat/jei/                    // EpigraphyJeiPlugin (gated category)

// Note (D1): no container/menu screens for gameplay. `client/` holds in-world
// renderers and at most the read-only documentation reader (D6).
```

---

## 3. What each system needs from the code (cross-refs)

- **Runes** (`RUNES.md`): a `Glyph` record and a `RuneWord` record, each with a
  `SimpleJsonResourceReloadListener` (`data/*/glyphs/`, `data/*/rune_words/`). The
  rune word registry needs an **unordered-glyph-set index** so codex submissions
  validate in O(1). Plus a client-side resolver that renders a rune word at the
  right tier (unreadable / literal glosses / true referent).
- **Discovery** (`DISCOVERY.md`): `GlyphCarvingBlock` + BE; a `ConfiguredFeature`/
  `PlacedFeature` for carvings; Global Loot Modifiers for tablet drops; the Codex
  item + on-carving record interaction (no screen); Lectern of Study block;
  Observatory (in-world constellation projection) / Astrolabe.
- **Rituals** (`RITUALS.md`): a custom `RecipeType`/serializer for
  `epigraphy:infusion`; a `Condition` registry with the v1 condition types; altar
  + pedestal block-entities; `RitualRunner` (match → research check → animate →
  produce → master, or → backlash on a blind attempt).
- **Knowledge** (`KNOWLEDGE.md`): the capability + provider + NBT persistence;
  bidirectional packets; derivation + mutation helpers; inventory-acquisition hook;
  backlash resolution + `instability` decay tick.
- **Reference layer** (`KNOWLEDGE.md` §6, D6): an in-game documentation model
  populated from knowledge (read-only reader, no gameplay menu) + a `compat/jei`
  plugin registering the infusion category and filtering by mastered state.

---

## 4. Phased build order

Each phase ends at something runnable/testable, so the mod is never a big-bang.

**Phase 0 — It compiles & loads.**
Gradle + ForgeGradle, `mods.toml`, `Epigraphy.java`, empty DeferredRegisters, a
creative tab. Goal: `runClient` opens a world with the mod present.

**Phase 1 — Runes as data.**
`Glyph` + `RuneWord` objects and their datapack loaders; ship the starter lexicon
(`RUNES.md` §2) and the v1 rune words (`RUNES.md` §3); build the unordered-glyph-set
index for submit validation. `/epigraphy runes` debug command lists both. No
gameplay yet.

**Phase 2 — Knowledge capability (research spine).**
`PlayerKnowledge` + persistence + sync; both layers (glyph progress + decoded rune
words); derived tiers; debug commands to grant/inspect sightings, learns, and
decodes. On-item surfacing stub: tablet tooltips reflect tier. This is the spine
everything hangs on — build it early.

**Phase 3 — Discovery & the codex (incl. sky, D4).**
`GlyphCarvingBlock` + on-carving record action (charcoal rubbing → inscribed
rubbing item); Lectern of Study + inscribed tablets; carving worldgen feature;
tablet loot GLMs. **Observatory + Astrolabe sky reading ships here** (D4) — the
in-world constellation projection is v1 identity, so it's core, not deferred.

The **codex** lands here too (D9): the 20-slot grid whose slots cycle only
*learned* glyphs, plus server-authoritative `submitRuneWord` validation and **seek
mode** (structure-tagged locate, `DISCOVERY.md` §7.1). At the end of this phase the
full discover → learn → guess → decode loop is playable, before any ritual exists.

**Phase 4 — Rituals.**
Infusion `RecipeType` + serializer + condition registry; Stone/Blackstone altars +
pedestals (catalyst items only, D5) + `RitualRunner`; `liquid_starlight` fluid;
ship Illuminated Stone (T1) and Chaos Ingot (T2) recipes + the `chaos_ingot` item.
The full in-world craft loop works.

**Phase 5 — Gating, backlash & reference layer.**
Wire the understanding gate + attemptability; the **backlash** system (D3) for
blind attempts (anomaly spawn, corruption, `instability`); the Tier-3 master hooks
(perform + obtain); the in-game documentation model + JEI plugin, both filtered by
mastered state. Now "knowing ≠ having" and "research first or pay for it" are real
end-to-end.

**Phase 6 — Polish.**
Ritual particles/FX, backlash FX, codex art & lore text, sounds, advancement hooks,
config (`require_learning_to_attempt`, `reveal_instructions_on_sighting`), and
settling the reference form (`DECISIONS.md` Q5) and submit-segmentation (Q8) if not
already fixed in Phase 3.

---

## 5. Testing strategy

- **Datapack validation** — a load-time sanity pass: every rune word's glyphs exist
  and number 2–3; every ritual's `rune_words` reference existing rune words; **no
  two rune words share the same unordered glyph set** (submit must be
  deterministic); no two recipes match identical setups (`RITUALS.md` §7);
  referenced items/fluids exist.
- **Unit** — rune word lookup by unordered glyph set; slot-grid segmentation (Q8).
- **GameTest** (Forge) for the altar: build altar+pedestals in a test structure,
  force weather/time, fire the ritual, assert output + mastery. A second case
  asserts a blind attempt produces backlash instead.
- **Manual matrix** — the full walkthrough from `DESIGN.md` §5 as a QA script:
  sight → learn → guess in codex → decode → perform → mastered.

---

## 6. Decisions (see `DECISIONS.md` for the authoritative log)

Resolved since the first draft:

1. ✅ **Terminology (D0).** Glyph = symbol; rune word = 2–3 glyph set; Runes = the
   whole system. Glyphs are discovered, rune words are guessed.
2. ✅ **No block/machine GUI (D1)** — with one minimal exception, the codex (D9).
3. ✅ **Glyphs learned passively, rune words guessed actively (D2).**
4. ✅ **Backlash everywhere (D3).** Blind attempts bite back — Phase 5.
5. ✅ **Sky reading ships in v1 (D4).** Pulled into Phase 3.
6. ✅ **Glyphs are research, not reagents (D5).** Pedestals hold catalysts only.
7. ✅ **Reference layer = in-game documentation + JEI (D6).**
8. ✅ **Codex: submit + seek (D7); 20 cycling glyph slots (D9).**

Still open (don't block early phases):

- 🔵 **Pedestal matching** — multiset now, patterned geometry later.
- 🔵 **Fluid identity** — one `liquid_starlight` now, themed fluids later.
- 🔵 **Reference form (Q5)** — likely the codex itself rather than a separate book.
- ❓ **Backlash severity model (Q6)** — per-recipe base × undecoded-count × instability.
- ❓ **Wrong-submission cost (Q7)** — free, cooldown, or consumable.
- ❓ **Slot segmentation (Q8)** — how submit splits 20 slots into words; leaning gap-delimited.

See `DECISIONS.md` for the current standing of each.

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
│   ├─ RuneWord · RuneWordManager //   ordered 2-3 glyph sequences; data/*/rune_words/
│   │                             //   + lookup by ORDERED sequence (submit validation)
│   └─ Inscription · Clause       //   the ritual formula: VESSEL/OFFERING/HOUR/
│                                 //   SUBJECT/ISSUE; parses & validates clause order
├─ knowledge/                     // PlayerKnowledge capability, tiers, sync packets
│   ├─ PlayerKnowledge · KnowledgeProvider · KnowledgeTier
│   └─ net/  (SyncKnowledgePacket, RecordSightingC2S, …)
├─ ritual/                        // InfusionRecipe, condition registry, altar logic
│   ├─ InfusionRecipe · InfusionRecipeSerializer
│   ├─ condition/  (Condition, WeatherCondition, TimeCondition, …)
│   └─ AltarBlockEntity · RitualRunner
├─ block/  · item/  · fluid/      // concrete registry objects & block-entities
├─ world/                         // GlyphCarvingBlock(+BE), feature, constellation/sky logic
├─ art/                           // procedural glyph rendering (D14)
│   ├─ StrokeRenderer             //   lemma -> three structural strokes + notches
│   ├─ Tablet                     //   the one universal frame
│   └─ GlyphAtlas                 //   composite + cache; `texture` override wins
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
  rune word registry is keyed by **ordered glyph sequence** (e.g. a list-keyed map)
  so codex submissions validate in O(1) and reversed sequences correctly miss. Plus
  an `Inscription` type modelling the five-clause formula, and a client-side
  resolver that renders each word at the right tier (unreadable / literal glosses /
  true referent) while always showing its clause label.
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

**Phase 1 — Runes as data, and the glyph renderer.**
`Glyph` + `RuneWord` + `Inscription` objects and their datapack loaders; ship the
starter lexicon (`RUNES.md` §2) and the v1 rune words (`RUNES.md` §3); build the
**ordered-sequence** index for submit validation and the clause-order validator.

Also here: the **procedural glyph renderer** (D14/D15) — three structural strokes from
`lemma`, length notches, one universal frame, composited and atlased, with
`texture` as an override. Worth doing early: every later phase (carvings, tablets, codex, in-world
inscriptions) renders glyphs, and generated art means no phase is ever blocked
waiting on an artist. `/epigraphy runes` debug command lists loaded glyphs and dumps
the atlas for eyeballing.

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
  and number 2–3; **no two rune words share the same ordered glyph sequence**
  (submit must be deterministic — this bites early, see `RITUALS.md` §5.3); every
  recipe's `inscription` fills all five clauses with existing rune words, and each
  clause's `means.type` suits its slot (HOUR must be a `condition`, SUBJECT/ISSUE an
  `item`, …); no two recipes match identical setups (`RITUALS.md` §7); referenced
  items/fluids exist.
- **Unit** — rune word lookup by ordered sequence (and that reversed sequences
  miss); gap-delimited slot-grid segmentation; clause-order validation.
- **GameTest** (Forge) for the altar: build altar+pedestals in a test structure,
  force weather/time, fire the ritual, assert output + mastery. A second case
  asserts a blind attempt produces backlash instead.
- **Manual matrix** — the full walkthrough from `DESIGN.md` §5 as a QA script:
  sight → learn → guess in codex → decode → perform → mastered.

---

## 6. Decisions (see `DECISIONS.md` for the authoritative log)

Resolved since the first draft:

1. ✅ **Terminology (D0).** Glyph = symbol; rune word = ordered 2–3 glyph sequence;
   Runes = the whole system. Glyphs are discovered, rune words are guessed.
2. ✅ **Order is meaningful; the Runes have a grammar (D10).** `QUALIFIER · HEAD`
   within a word; `VESSEL / OFFERING / HOUR / SUBJECT / ISSUE` across an inscription.
3. ✅ **No block/machine GUI (D1)** — with one minimal exception, the codex (D9).
4. ✅ **Glyphs learned passively, rune words guessed actively (D2).**
5. ✅ **Backlash everywhere (D3).** Blind attempts bite back — Phase 5.
6. ✅ **Sky reading ships in v1 (D4).** Pulled into Phase 3.
7. ✅ **Glyphs are research, not reagents (D5).** Pedestals hold catalysts only.
8. ✅ **Reference layer = in-game documentation + JEI (D6).**
9. ✅ **Codex: submit + seek (D7); 20 cycling glyph slots (D9).**
10. ✅ **Slot segmentation (Q8).** The grid is one inscription, gap-delimited,
    read left to right in formula order.
11. ✅ **Determinatives may prefix or suffix (Q9).** Structures prefix, materials
    suffix — as Sumerian prefixes `DINGIR` but suffixes `KI`.
12. ✅ **Glyph art = three structural strokes + universal frame (D14/D15).** Generated
    from data, every glyph its own shape; pictographs rejected for undercutting
    decipherment.

Still open (don't block early phases):

- 🔵 **Pedestal matching** — multiset now, patterned geometry later.
- 🔵 **Fluid identity** — one `liquid_starlight` now, themed fluids later.
- 🔵 **Reference form (Q5)** — likely the codex itself rather than a separate book.
- ❓ **Backlash severity model (Q6)** — per-recipe base × undecoded-count × instability.
- ❓ **Wrong-submission cost (Q7)** — free, cooldown, or consumable.
- ❓ **Vessel clause order (Q9)** — `ALTARE · TENEBRAE` exception vs. strict
  `QUALIFIER · HEAD`. **Blocks lexicon authoring — settle before Phase 1.**

See `DECISIONS.md` for the current standing of each.

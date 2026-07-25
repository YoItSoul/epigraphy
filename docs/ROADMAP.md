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
├─ glyph/                         // Glyph = data object; GlyphManager = datapack loader
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

- **Glyphs** (`GLYPHS.md`): a `Glyph` record + a `SimpleJsonResourceReloadListener`
  loading `data/*/glyphs/*.json`; a client-side translated-text resolver.
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

**Phase 1 — Glyphs as data.**
`Glyph` object + datapack loader; ship the starter lexicon JSON (`GLYPHS.md` §2);
`/epigraphy glyphs` debug command lists loaded glyphs. No gameplay yet.

**Phase 2 — Knowledge capability (research spine).**
`PlayerKnowledge` + persistence + sync; derived tiers; debug commands to
grant/inspect sightings/learns. In-world/on-item surfacing stub: tablet tooltips
reflect tier. **No screen** (D1). This is the spine everything hangs on — build it
early.

**Phase 3 — Discovery (incl. sky, D4).**
`GlyphCarvingBlock` + on-carving record action (charcoal rubbing → inscribed
rubbing item); Lectern of Study + inscribed tablets; carving worldgen feature;
tablet loot GLMs. **Observatory + Astrolabe sky reading ships here** (D4) — the
in-world constellation projection is v1 identity, so it's core, not deferred. Now
research can be *earned* in-world.

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
Ritual particles/FX, backlash FX, documentation art & lore text, sounds,
advancement hooks, config (`require_learning_to_attempt`,
`reveal_instructions_on_sighting`), and settling the documentation form (guide book
vs. in-world lectern projection — `DECISIONS.md` Q5).

---

## 5. Testing strategy

- **Datapack validation** — a load-time sanity pass: every ritual's `glyphs`
  reference existing glyphs; no two recipes match identical setups (the
  determinism rule in `RITUALS.md` §7); referenced items/fluids exist.
- **GameTest** (Forge) for the altar: build altar+pedestals in a test structure,
  force weather/time, fire the ritual, assert output + Tier-3 unlock.
- **Manual matrix** — the three-tier walkthrough from `DESIGN.md` §5 as a QA script.

---

## 6. Decisions (see `DECISIONS.md` for the authoritative log)

Resolved since the first draft:

1. ✅ **No gameplay GUI (D1).** In-world/on-item actions only; the reference layer
   is the sole permitted screen surface.
2. ✅ **Translation is passive triangulation (D2).** No decode minigame.
3. ✅ **Backlash everywhere (D3).** Blind attempts bite back — implemented in Phase 5.
4. ✅ **Sky reading ships in v1 (D4).** Pulled into Phase 3.
5. ✅ **Glyphs are research, not reagents (D5).** Pedestals hold catalysts only;
   the altar reads the player's learned glyphs.
6. ✅ **Reference layer = in-game documentation + JEI (D6).**

Still open (don't block early phases):

- 🔵 **Pedestal matching** — multiset now, patterned geometry later (D5 keeps this simple).
- 🔵 **Fluid identity** — one `liquid_starlight` now, themed fluids later.
- ❓ **Documentation form (Q5)** — guide book vs. in-world lectern projection.
- ❓ **Backlash severity model (Q6)** — per-recipe base × untranslated-count × instability.

See `DECISIONS.md` for the current standing of each.

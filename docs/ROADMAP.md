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
  - **JEI** — recipe browser + the knowledge-gated plugin (`KNOWLEDGE.md` §5).
  - (later) **Patchouli** could back the Codex, but v1 uses a custom screen so the
    three-tier rendering is fully under our control.

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
├─ world/                         // GlyphCarvingBlock(+BE), feature, constellation logic
├─ client/                        // CodexScreen, renderers, translated-text resolver
└─ compat/jei/                    // EpigraphyJeiPlugin (gated category)
```

---

## 3. What each system needs from the code (cross-refs)

- **Glyphs** (`GLYPHS.md`): a `Glyph` record + a `SimpleJsonResourceReloadListener`
  loading `data/*/glyphs/*.json`; a client-side translated-text resolver.
- **Discovery** (`DISCOVERY.md`): `GlyphCarvingBlock` + BE; a `ConfiguredFeature`/
  `PlacedFeature` for carvings; Global Loot Modifiers for tablet drops; the Codex
  item + record interaction; Lectern of Study block; Observatory/Astrolabe.
- **Rituals** (`RITUALS.md`): a custom `RecipeType`/serializer for
  `epigraphy:infusion`; a `Condition` registry with the v1 condition types; altar
  + pedestal block-entities; `RitualRunner` (match → animate → produce → unlock).
- **Knowledge** (`KNOWLEDGE.md`): the capability + provider + NBT persistence;
  bidirectional packets; derivation + mutation helpers; inventory-acquisition hook.
- **JEI** (`KNOWLEDGE.md` §5): `compat/jei` plugin registering the infusion
  category and filtering by unlock state.

---

## 4. Phased build order

Each phase ends at something runnable/testable, so the mod is never a big-bang.

**Phase 0 — It compiles & loads.**
Gradle + ForgeGradle, `mods.toml`, `Epigraphy.java`, empty DeferredRegisters, a
creative tab. Goal: `runClient` opens a world with the mod present.

**Phase 1 — Glyphs as data.**
`Glyph` object + datapack loader; ship the starter lexicon JSON (`GLYPHS.md` §2);
`/epigraphy glyphs` debug command lists loaded glyphs. No gameplay yet.

**Phase 2 — Knowledge capability.**
`PlayerKnowledge` + persistence + sync; debug commands to grant/inspect
sightings/translations. Codex item opens a screen that lists glyphs at their tier.
This is the spine everything hangs on — build it early.

**Phase 3 — Discovery.**
`GlyphCarvingBlock` + record action; Lectern of Study + inscribed tablets; carving
worldgen feature; tablet loot GLMs. Now knowledge can be *earned* in-world.
(Constellations/Observatory can trail into a 3b — sky reading is the most novel and
riskiest UI, so it should not block the ritual loop.)

**Phase 4 — Rituals.**
Infusion `RecipeType` + serializer + condition registry; Stone/Blackstone altars +
pedestals + `RitualRunner`; `liquid_starlight` fluid; ship Illuminated Stone (T1)
and Chaos Ingot (T2) recipes + the `chaos_ingot` item. The full craft loop works.

**Phase 5 — Gating & JEI.**
Wire the instruction gate (page revelation + attemptability) and the Tier-3 unlock
hooks (perform + obtain); JEI plugin with the gated category. Now "knowing ≠
having" is real end-to-end.

**Phase 6 — Polish.**
Ritual particles/FX, Codex art & lore text, sounds, advancement hooks, config
(`require_translation_to_attempt`, `reveal_pages_on_sighting`), Observatory sky UI
if deferred.

---

## 5. Testing strategy

- **Datapack validation** — a load-time sanity pass: every ritual's `glyphs`
  reference existing glyphs; no two recipes match identical setups (the
  determinism rule in `RITUALS.md` §7); referenced items/fluids exist.
- **GameTest** (Forge) for the altar: build altar+pedestals in a test structure,
  force weather/time, fire the ritual, assert output + Tier-3 unlock.
- **Manual matrix** — the three-tier walkthrough from `DESIGN.md` §5 as a QA script.

---

## 6. Open technical decisions (tracked in the design dialogue)

These are choices we should settle together before/while coding — they don't
change the *vision*, but they shape the code:

1. **Codex backing** — custom screen (full control, more work) vs. Patchouli
   (faster, less control over three-tier rendering). *Leaning custom.*
2. **Constellation UI scope for v1** — ship sky-reading in v1, or defer to 6 and
   launch discovery on carvings+tablets only? *Leaning defer.*
3. **Pedestal matching** — multiset (v1, forgiving) vs. patterned geometry
   (Thaumcraft-precise). *Leaning multiset now, pattern later.*
4. **Attempt gate strictness** — require Tier-2 translation to attempt, or allow
   Tier-1 "blind" attempts that risk failure/backlash? *Open — see dialogue.*
5. **Fluid identity** — one universal infusion fluid vs. multiple themed fluids
   from the start. *Leaning one now.*

See the conversation for where each of these currently stands.

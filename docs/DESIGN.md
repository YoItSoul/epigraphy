# Epigraphy — Design Document

Status: draft v1 · Target: Minecraft 1.20.1 Forge · Pack: Souls of Avarice (fills the Astral Sorcery slot)

Epigraphy is a discovery-and-literacy magic mod. The player does not open a
GUI and click a recipe into existence — they find glyphs scattered through
the world (stars, ruins, boss drops), slowly learn what those glyphs *mean*,
and only then are they able to read the "sentences" carved into altars and
tablets that describe rituals. JEI stays dark for a recipe until the player
has actually performed it or holds the result, so the mod's own knowledge
system is the real discovery mechanic — JEI is just a memory aid layered on
top afterward.

Everything mechanical here is original: our own glyph shapes, our own Latin
gloss words, our own ritual structure. Nothing is copied or referenced from
Astral Sorcery beyond the shared genre ("look at the sky to progress",
"place items on pedestals for a ritual") — those are mechanics categories,
not implementations, and both are common across the Thaumcraft/Botania/Blood
Magic lineage this pack draws from.

---

## 1. Glyph Vocabulary (draft, 39 glyphs)

Glyphs are the mod's alphabet. Each glyph is a standalone game object with
an ID, a Latin gloss (the "translation" the player eventually reads), a
grammatical **role**, and a **symbol** (an original geometric mark used in
its texture — see notes below). Ritual recipes are written as ordered lists
of glyph IDs; the grammatical roles are what let a recipe read as a
sentence instead of a shopping list.

Symbol design language (for the artist): every glyph is built from a small
set of primitive strokes — straight radial lines, a circle, a triangle,
a crescent, a dot-cluster — combined inside a circular or hexagonal frame,
similar in *spirit* to how real epigraphic scripts (runic, cuneiform,
ogham) compose complex meaning from a few repeated strokes. Verbs get a
circular frame, materials a square/diamond frame, modifiers a triangular
frame, conditions a crescent/star frame. This is a rule for consistency,
not a specific image — no existing mod's glyph shapes are referenced or
reused.

### 1.1 Verbs — ritual actions (6)

| ID | Gloss | Meaning | Symbol notes |
|---|---|---|---|
| `verb_altare` | Altare | "craft/perform here" — the anchor verb every ritual sentence opens with | circle frame, single upright line through center |
| `verb_sacrificium` | Sacrificium | consume an item or entity as a cost, not an ingredient | circle frame, line broken at the middle |
| `verb_vocare` | Vocare | summon/call a mob or minor spirit to the altar | circle frame, radiating lines outward (a "call") |
| `verb_vincire` | Vincire | bind an effect or enchantment onto an item, or soulbind item↔player | circle frame, two arcs interlocked |
| `verb_mutare` | Mutare | transmute material A into material B | circle frame, arrow-like chevron through center |
| `verb_legere` | Legere | "read/learn" — never appears in a ritual sentence; it's the meta-glyph stamped on tablets and journal pages to mean "this teaches a glyph" | circle frame, open book/two-line motif |

### 1.2 Materials — nouns naming a substance (10)

| ID | Gloss | Maps to | Symbol notes |
|---|---|---|---|
| `mat_metallum` | Metallum | generic metal ingot (any vanilla/modded ingot tag) | diamond frame, single square |
| `mat_ferrum` | Ferrum | iron ingot | diamond frame, square with one notch |
| `mat_aurum` | Aurum | gold ingot | diamond frame, square with dot center |
| `mat_adamas` | Adamas | netherite ingot/scrap | diamond frame, square with cross-hatch |
| `mat_os` | Os | bone / bone meal | diamond frame, two parallel short lines |
| `mat_sanguis` | Sanguis | any "blood"-tagged fluid/item (redstone as vanilla stand-in) | diamond frame, single teardrop |
| `mat_lignum` | Lignum | wood (logs/planks tag) | diamond frame, triangle-topped square (tree glyph) |
| `mat_lapis` | Lapis | stone/blackstone/deepslate tag | diamond frame, stacked small squares |
| `mat_vitrum` | Vitrum | glass | diamond frame, square with diagonal line |
| `mat_cinis` | Cinis | ash / gunpowder (SOA's ash item if present, else vanilla fallback) | diamond frame, scatter of small dots |

### 1.3 Objects — nouns naming a tool or vessel (6)

| ID | Gloss | Maps to | Symbol notes |
|---|---|---|---|
| `obj_baculum` | Baculum | a rod (blaze rod / breeze rod / our own rod item) | square frame, single vertical line |
| `obj_vas` | Vas | a vessel/bowl (fluid container context) | square frame, U-shaped arc |
| `obj_catena` | Catena | a chain (binding/tether items) | square frame, linked circles |
| `obj_corona` | Corona | a crown (worn/equipment-slot items) | square frame, small triangle ring on top of frame |
| `obj_speculum` | Speculum | a mirror (used in star-viewing and some rituals) | square frame, circle with reflected line |
| `obj_tabula` | Tabula | a tablet (meta-object; refers to the glyph tablet item class itself) | square frame, rectangle outline |

### 1.4 Modifiers — adjectives naming a quality/element (8)

| ID | Gloss | Meaning | Symbol notes |
|---|---|---|---|
| `mod_flamma` | Flamma | fire/heat-related | triangle frame, wavy upward lines |
| `mod_glacies` | Glacies | frost/cold-related | triangle frame, angular zigzag |
| `mod_umbra` | Umbra | darkness/shadow | triangle frame, solid filled triangle |
| `mod_lux` | Lux | light/radiance | triangle frame, outline triangle with center dot |
| `mod_chaos` | Chaos | entropy, instability, "wrong" energy | triangle frame, asymmetric jagged crack pattern |
| `mod_ordo` | Ordo | structure, stability, "correct" energy | triangle frame, perfectly symmetric nested triangles |
| `mod_vita` | Vita | life/growth | triangle frame, sprouting-branch motif |
| `mod_mors` | Mors | death/decay | triangle frame, inverted wilting-branch motif |

### 1.5 Conditions — when/where a ritual may trigger (9)

| ID | Gloss | Condition check | Symbol notes |
|---|---|---|---|
| `cond_caelum` | Caelum | altar must see open sky (no blocks above) | crescent frame, sun-and-arc |
| `cond_infernus` | Infernus | altar must be in the Nether | crescent frame, spiky inverted arc |
| `cond_nox` | Nox | must be night (world time) | crescent frame, filled crescent |
| `cond_dies` | Dies | must be day (world time) | crescent frame, outline circle |
| `cond_fulgur` | Fulgur | must be thundering | crescent frame, jagged bolt |
| `cond_luna` | Luna | any moon must be visible (open sky + night) | crescent frame, plain crescent |
| `cond_plenilunium` | Plenilunium | full moon phase specifically | crescent frame, full circle inside crescent frame |
| `cond_novilunium` | Novilunium | new moon phase specifically | crescent frame, empty ring |
| `cond_aqua` | Aqua | altar must be adjacent to/submerged in water | crescent frame, wave line |

**Total: 39 glyphs.** This is a deliberately small, closed alphabet —
Phase-1 recipes should mostly recombine these 39 rather than requiring new
ones, so that "learning the alphabet" is a completable goal instead of an
ever-growing grind. New glyphs (verbs especially) are the highest-cost
addition since they extend the grammar itself; new materials/conditions are
cheap content additions later.

---

## 2. Knowledge System

### 2.1 Data model

Three tiers, tracked **per player**, **per glyph ID**:

```
enum KnowledgeTier { UNSEEN, SEEN, LEARNED, MASTERED }
```

- **UNSEEN** — default. The glyph has never been encountered. It doesn't
  appear in the journal at all.
- **SEEN** — the player has encountered the glyph (looked at a constellation
  through the viewer, rubbed a ruin wall, or read an unidentified tablet)
  but hasn't decoded it. It appears in the journal as an unidentified
  symbol with no gloss. Recipes containing this glyph render as raw symbols
  wherever the player would otherwise read the gloss.
- **LEARNED** — the player has decoded the glyph (see §3 for the specific
  unlock actions per source). The journal now shows the Latin gloss and a
  short flavor-text hint about its grammatical role. Any ritual sentence
  using only LEARNED-or-better glyphs becomes fully readable as flavor text
  ("Altar, with a rod, aflame, beneath the open sky...") — this is enough
  for a player to go attempt the ritual, but it is **not** a JEI unlock.
- **MASTERED** — the player has either (a) performed the ritual that
  outputs a given result at least once, or (b) obtained the resulting item
  through any means (crafting, loot, trade). Mastering a *recipe* is
  tracked separately from mastering a *glyph* — see below — but the
  underlying storage is uniform: mastering an item's recipe is what drives
  the JEI unlock in §6.

So there are really two parallel knowledge sets per player:

```
GlyphKnowledge   : Map<GlyphId, KnowledgeTier>       // SEEN / LEARNED only meaningful here
RecipeKnowledge  : Set<RitualRecipeId>               // "mastered" ritual IDs, boolean present/absent
```

A ritual recipe's sentence is renderable (readable flavor text) once every
glyph it references is at least LEARNED. A ritual recipe is *masterable*
(shows in JEI) once its ID is in `RecipeKnowledge`, independent of glyph
state — obtaining the output item via a trade or dungeon chest, for
instance, unlocks JEI even if the player never learned a single glyph in
the sentence, matching the brief's "unlocks once you obtain the item."

### 2.2 Storage & sync

- Implemented as a Forge **Capability** (`IEpigraphyKnowledge`) attached to
  `Player` via `AttachCapabilitiesEvent<Entity>`, backed by two fields
  above, serialized to NBT (`INBTSerializable`).
- Persistence follows the player: copied on death/respawn via
  `PlayerEvent.Clone` (`isWasDeath` — knowledge always carries over, this is
  never lost on death, unlike inventory).
- Sync: a single custom payload record, `SyncKnowledgePacket(Map<GlyphId,
  KnowledgeTier>, Set<RitualRecipeId>)`, sent player→client on login and on
  every mutation (`ServerPlayer#connection.send`). Deltas only — a mutation
  sends just the changed glyph/recipe, not the whole map, to keep network
  traffic trivial (this data changes rarely, a handful of times per play
  session).
- Client keeps a read-only mirrored copy (`ClientKnowledgeCache`) purely
  for rendering the journal GUI and driving the JEI runtime hide/unhide
  (§6) — the server capability is always the source of truth, client never
  mutates its own copy directly.
- SOA quest-system integration: expose two static helper methods,
  `EpigraphyKnowledge.isGlyphLearned(Player, GlyphId)` and
  `EpigraphyKnowledge.isRecipeMastered(Player, RitualRecipeId)`, plus a
  Forge event (`GlyphLearnedEvent`, `RitualMasteredEvent`) fired on tier
  transitions, so the modpack's quest book can gate/advance stages off
  either polling or event without needing new integration code per quest.

### 2.3 Journal UI concept

- Item: **Codex** (`item_codex`), a book-like item, right-click to open a
  full-screen GUI (not a container — pure client rendering + capability
  read, like a guidebook mod).
- Layout: left page lists all SEEN-or-better glyphs as a grid of symbol
  icons (grouped by grammatical role tab: Verbs / Materials / Objects /
  Modifiers / Conditions); clicking one opens the right page.
- Right page (per glyph):
  - SEEN: large rendering of the symbol only, "??? — you have seen this
    symbol but not yet learned its meaning," plus *where* it was last seen
    (source category, not exact coordinates — "seen among the stars" /
    "seen carved in ruins" / "seen on a tablet").
  - LEARNED: adds the Latin gloss, English meaning, grammatical role, and
    a flavor-text couplet (short, thematic, written once per glyph — this
    is the "translated everywhere" flavor text from the brief).
  - Recipe cross-references: any ritual sentence referencing this glyph
    that is at least partially readable is listed at the bottom, rendered
    with symbols standing in for any glyph the player hasn't LEARNED yet
    and glosses for the ones they have — so the reward for learning a new
    glyph is visibly more recipes "resolving" into readable sentences in
    real time.
- A third tab, "Rituals," lists ritual sentences directly (readable ones
  only) independent of which glyph page you came from, sorted by how many
  of their glyphs are already known — closest-to-readable first, so the
  UI naturally points the player at what to learn next.

---

## 3. Discovery Systems

Three independent sources feed the same knowledge model — a glyph learned
via one source is learned everywhere, there's no "you must find it in the
ruins specifically."

### 3.1 Star viewing

- Item: **Astrolabe** (`item_astrolabe`), a spyglass-alike. Right-click +
  hold while looking at the sky opens a dedicated client-only full-screen
  view, exactly like vanilla's spyglass zoom overlay, **not** a change to
  the world skybox — this is the hard technical constraint from the brief
  (shader/perf/compat: no custom sky renderer, no mixin into
  `LevelRenderer`).
- Implementation: the overlay is a 2D projection GUI. We compute a
  deterministic per-world "starfield seed" (world seed + a fixed salt),
  scatter procedural dots across a virtual dome, and overlay a fixed set of
  hand-authored constellation **line patterns** (one per discoverable
  glyph) at positions derived from the seed + the real day count (so
  constellations visibly rotate/rise/set with in-game time, but which
  glyph is *available* at all only depends on world seed — deterministic
  and shareable between players on the same server/seed).
  - Constellation visibility also respects real conditions where relevant:
    a constellation might only be traceable when it's actually night
    (`cond_nox`) and the sky is clear enough, echoing the mod's own
    condition glyphs — thematically "you can only see the glyph for
    Night... at night."
- Discovery interaction: while in the overlay, the player drags a
  crosshair across star-dots; if they connect the dots matching a hidden
  constellation pattern (order-independent, tolerance-based line matching,
  similar in *feel* to a connect-the-dots puzzle, not to any specific
  existing mod's minigame), that glyph flips to SEEN, and a short "trace
  correctly a second time" or "hold the pattern steady for N seconds"
  action promotes it directly to LEARNED (star-taught glyphs skip the
  "found but unidentified" middle step less often than ruin-taught ones,
  since correctly tracing the shape *is* reading it).
- This is the primary channel for **condition** and **modifier** glyphs
  (thematically: the sky teaches you about time, weather, light and dark).

### 3.2 Ruin glyphs + rubbing

- World generation: a handful of small original structures (**glyph
  ruins**) placed via standard Forge/Vintage structure JSON (structure
  template NBT + `structure_set`), each with 1–4 wall-mounted **carved
  glyph blocks** (`block_carved_glyph`, a thin decorative block with a
  baked-in glyph reference in its block entity, rendered via a simple
  quad/model — original stone-relief textures, distinct visual language
  from vanilla chiseled blocks).
- Discovery item: **Rubbing Kit** (`item_rubbing_kit`, chalk + paper,
  consumable per use like vanilla's map-cloning pattern). Right-click a
  carved glyph block with it:
  - Consumes one paper (+ chalk durability tick).
  - Produces a **Glyph Rubbing** (`item_glyph_rubbing`) — a paper item
    stamped with that specific glyph.
  - The act of successfully rubbing sets that glyph to SEEN immediately.
  - Right-clicking (studying) the rubbing item itself in hand for a short
    hold duration promotes SEEN→LEARNED (the "translation" moment) — this
    mirrors the brief's chalk-and-paper idea directly and gives a tangible
    inventory item, which also makes rubbings tradeable/giftable between
    players on a server (a rubbing carries only the glyph ID, no
    per-player state, so trading it is safe).
  - This is the primary channel for **material** and **object** glyphs
    (ruins are where you find "this is what Metal looks like carved").
- Duplicate protection (see §3.4) applies to which carved-glyph blocks a
  given structure spawns with, not to the rubbing item itself.

### 3.3 Tablets (mob/boss drops)

- **Glyph Tablet** (`item_glyph_tablet`, data-driven item via the
  `tablets.json` schema in §5) is a lootable/droppable item referencing one
  glyph. Two flavors:
  - *Unidentified Tablet*: dropped by common mobs at low rate — right-click
    to "read" it, which sets that glyph to SEEN only (same as a rubbing's
    first step).
  - *Boss/rare Tablet*: dropped by SOA boss-tier mobs, drop table entries
    are per-boss curated (data-driven — a boss can be configured to always
    drop one specific tablet, e.g. a Nether boss guarantees `mod_flamma`'s
    tablet) — right-click **directly LEARNS** the glyph, skipping SEEN
    entirely, framed as "the boss *understood* this glyph, and in
    defeating it you inherit that understanding."
  - This is the primary channel for **verb** glyphs — the six ritual verbs
    are deliberately gated behind combat/boss content since they're the
    highest-grammar-value glyphs (a new verb unlocks whole new categories
    of ritual, not just one recipe).
- Tablets are consumed on use (single-use, like the rubbing item).

### 3.4 Duplicate protection

- Per-player: once a glyph is LEARNED, all further sources of that same
  glyph (rubbing an already-learned wall carving, reading a duplicate
  tablet, re-tracing a known constellation) short-circuit to a small
  fallback reward instead of a no-op — configurable, default a few
  seconds of client-only "insight" reduced-hunger/XP-nudge, so players
  aren't punished for exploring thoroughly, but there's no reason to
  farm one boss for tablets once its glyph is known.
- World generation: ruin structures draw their 1–4 carved-glyph placements
  from a per-structure-template pool, and the structure's data (via a
  simple loot-table-style weighted pick baked at structure-processing
  time, not at world-gen-seed time) avoids placing the *same* glyph twice
  within one structure instance. This is a soft per-structure guarantee,
  not a global "you'll never see a duplicate across the whole world" one —
  the world should still contain redundancy so a late-joining or unlucky
  player isn't blocked from a glyph by bad luck on one ruin.
- Tablet drop tables follow normal Forge/vanilla loot-table weighting;
  redundancy across mob types is a content/balance decision made per SOA
  integration pass, not a hard mod rule.

---

## 4. Ritual Engine

### 4.1 Structures

- **Glyph Altar** (`block_glyph_altar`) — the anchor block. Single block,
  not a multiblock structure; it has a block entity that scans a fixed
  radius (default 3 blocks, configurable per-recipe) for **Glyph
  Pedestals** on ritual attempt.
- **Glyph Pedestal** (`block_glyph_pedestal`) — a small block entity that
  holds exactly one item stack (rendered floating above it, no inventory
  GUI — right-click with item to place, right-click empty-handed to
  retrieve, matching the genre convention this mod is intentionally not
  reinventing). Pedestals contribute their held item to the ritual's
  ingredient pool when scanned.
- **Ground items**: some recipes additionally require specific items
  dropped as item entities *on the ground* within the scan radius
  (distinct from pedestal-held items) — modeled as a separate ingredient
  list in the recipe JSON (`ground_items`), consumed by despawning the
  matched item entities directly rather than requiring a pedestal.
- **Fluids**: the altar block itself exposes a small internal fluid tank
  (via `IFluidHandler`, no visible GUI — filled by right-clicking with a
  filled fluid container, emptied automatically when a ritual consuming
  that fluid completes). Recipes reference a required `FluidStack` amount
  + tag.

### 4.2 Trigger & resolution flow

1. Player right-clicks the altar with an empty hand (or a dedicated
   **Ritual Wand**, TBD in playtesting — an empty-hand trigger is simpler
   and matches "the altar itself is the interface").
2. Altar block entity gathers: its own fluid tank contents, all pedestals'
   held items within radius, all qualifying ground item entities within
   radius, plus environmental state (dimension, local time-of-day,
   weather, moon phase, sky visibility above the altar — a simple
   `canSeeSky` block-position check).
3. Attempts to match against all registered `RitualRecipe`s (see schema in
   §5) whose **material glyphs** are satisfiable by the gathered
   ingredients and whose **condition glyphs** are all currently true.
   - If exactly one recipe matches: consume ingredients (pedestal items
     decrement by their required count, ground items despawn, fluid tank
     drains), run the output (spawn item/apply effect/summon entity per
     `verb`), play a ritual VFX/SFX cue, and:
     - flip every glyph referenced in that recipe's sentence to at least
       SEEN for the triggering player (attempting/witnessing a ritual is
       itself a discovery source — a lesser one than the three primary
       channels, since it only grants SEEN, never LEARNED, keeping the
       primary discovery loop meaningful),
     - flip the recipe's ID to MASTERED for the triggering player (driving
       the JEI unlock in §6) and fire `RitualMasteredEvent`.
   - If multiple recipes match (ambiguous ingredient superset): fail with
     an in-world hint effect (a "the working feels unstable" particle
     puff) rather than silently picking one — recipe authors are expected
     to keep material glyph sets disjoint enough to avoid this in
     practice; it's a content-authoring constraint, not a runtime bug.
   - If zero recipes match: no-op, nothing consumed. (Optionally, a future
     phase adds a "near miss" journal hint — see backlog in §7.)
4. A ritual attempt does **not** require the triggering player to have any
   glyph knowledge at all — a player who stumbles on a working altar setup
   (e.g., copied from a wiki, or from someone else's screenshot) can
   perform it blind. Knowledge gates *understanding*, never *ability*.
   This matches the brief: "when a player learns how to do something, they
   get the instructions" — the instructions are a convenience, not a lock.

### 4.3 Ritual-as-sentence encoding

A `RitualRecipe` stores an ordered `List<GlyphId>` — the "sentence" — which
is used **only** for journal/flavor-text rendering (§2.3), never for
matching logic (matching is pure ingredient/condition-set comparison,
independent of order, for determinism and simplicity). Sentence order
follows a fixed grammar template so authored recipes read naturally:

```
[Verb] [Object?] [Modifier*] [Material+] [Condition*]
```

e.g. Chaos Ingot's sentence (`verb_altare, mat_adamas, mod_chaos,
cond_fulgur`) renders, once every glyph is LEARNED, as flavor text along
the lines of:

> *"Altar. Netherite. Chaos. Storm."*
> — rendered by the flavor-text template into something like: *"At the
> altar, offer the unbreakable metal; let chaos take it, beneath a raging
> sky."*

The exact prose template per verb is authored flavor text (one template
per verb glyph, with modifier/material/condition glosses substituted in),
not procedurally generated — procedurally generated "translations" tend to
read as garbage; hand-authored templates per verb keep it readable while
still being data-driven per recipe.

---

## 5. JSON Schemas (datapack-driven)

All three live under `data/<namespace>/epigraphy/...` so packs (including
SOA itself) can add/override glyphs, tablets, and rituals without touching
mod code.

### 5.1 Glyph — `data/<ns>/epigraphy/glyphs/<id>.json`

```json
{
  "role": "verb | material | object | modifier | condition",
  "gloss": "Altare",
  "translation": "Altar",
  "symbol_texture": "epigraphy:textures/glyph/verb_altare.png",
  "flavor_text": "The first word. Everything begins at the altar.",
  "discovery_sources": ["star", "ruin", "tablet"],
  "constellation": {
    "comment": "optional — only present for star-discoverable glyphs",
    "points": [[0,0], [3,1], [5,-2], [2,-4]],
    "night_only": true
  }
}
```

- `id` is implicit from the file path/namespace (`epigraphy:verb_altare`).
- `discovery_sources` restricts which of §3's channels can ever grant this
  glyph (verbs default to `["tablet"]` only, matching the boss-gating
  design in §3.3); it's a whitelist, not a requirement that all listed
  sources always fire for every glyph instance in the world.
- `constellation` is optional; omitted entirely for glyphs not meant to be
  star-discoverable (materials/objects, per §3.2).

### 5.2 Tablet — `data/<ns>/epigraphy/tablets/<id>.json`

```json
{
  "glyph": "epigraphy:verb_vocare",
  "tier": "common | boss",
  "grants": "seen | learned",
  "item_model": "epigraphy:item/tablet_common",
  "loot": {
    "comment": "informational only — actual drop wiring stays in normal loot_tables so packs use vanilla tooling",
    "suggested_pools": ["entities/zombie", "entities/skeleton"]
  }
}
```

- `tier`/`grants` are typically paired (`common`→`seen`,
  `boss`→`learned`), but left independent so a pack can author a rare
  "master tablet" for a common mob as a deliberate exception.
- The tablet **item** itself is registered by the mod (one item, NBT/data
  component carries which `tablets/<id>.json` entry it is); loot table
  wiring to attach that item to specific entities remains ordinary
  Minecraft `loot_tables` JSON referencing `epigraphy:glyph_tablet` with
  the appropriate component — no custom loot condition type needed for
  Phase 1.

### 5.3 Ritual recipe — `data/<ns>/recipes/<id>.json`

Implemented as a normal Forge recipe type (`"type": "epigraphy:ritual"`) so
it's visible to `/reload`, datapack overrides, and JEI's recipe-manager
lookups for free.

```json
{
  "type": "epigraphy:ritual",
  "sentence": ["epigraphy:verb_altare", "epigraphy:mat_adamas",
               "epigraphy:mod_chaos", "epigraphy:cond_fulgur"],
  "altar": "epigraphy:glyph_altar",
  "pedestal_items": [
    { "ingredient": { "item": "minecraft:netherite_ingot" }, "count": 1 }
  ],
  "ground_items": [],
  "fluid": null,
  "conditions": [
    { "type": "weather", "value": "thunder" }
  ],
  "result": { "item": "epigraphy:chaos_ingot", "count": 1 },
  "scan_radius": 3
}
```

- `conditions` entries map 1:1 to the condition glyphs in §1.5
  (`weather: thunder|rain|clear`, `time: day|night`, `dimension:
  <dimension id>`, `moon_phase: full|new`, `sky: open`, `fluid_adjacent:
  water`) — the JSON `type`/`value` pair is intentionally decoupled from
  the glyph ID string so future conditions can be added without a glyph
  existing yet, and vice versa (a condition glyph can exist in the
  vocabulary/journal before code implements its check, for
  flavor-text-only appearances — though every Phase-1 shipped recipe
  should only reference implemented conditions).
- `sentence` is used purely for journal rendering (§4.3); the matcher
  fields (`pedestal_items`, `ground_items`, `fluid`, `conditions`) are the
  actual authority for whether a ritual fires. Keeping them separate
  means a typo'd sentence never breaks gameplay, only flavor text.
- `pedestal_items`/`ground_items` use vanilla `Ingredient` JSON so tags
  work out of the box (`mat_metallum` → `{"tag": "forge:ingots"}`, etc.).

---

## 6. JEI Integration

- Register one JEI recipe category, **`epigraphy:ritual`**, rendering the
  altar+pedestal arrangement with the sentence as a caption (glyph symbols
  only for un-LEARNED glyphs still shown to *other* viewers' state? — no:
  JEI only shows a recipe at all once MASTERED per §2.1, so by the time a
  recipe is JEI-visible its full sentence is guaranteed renderable in
  gloss form; no partial-symbol state ever needs to render inside JEI
  itself, that ambiguity is fully handled by the in-mod journal instead).
- On player login and on every `RitualMasteredEvent`
  /"obtained-item-triggers-mastery" check (see below), call
  `IJeiRuntime.getRecipeManager()`:
  - `addRecipes(RecipeType<RitualRecipe>, List<RitualRecipe> newlyMastered)`
    for recipes just crossing into MASTERED,
  - and on first login, a bulk pass: hide every `epigraphy:ritual` recipe
    by default (`removeRecipes` immediately after JEI registers them via
    the normal `IRecipeCategoryRegistration`/`IRecipeRegistration` hooks),
    then `addRecipes` back in only the player's currently-MASTERED set —
    i.e. Phase-1 approach is "start all hidden, unhide on demand," not
    "start all visible, hide most" — simpler to get right and avoids a
    frame where an unmastered recipe is briefly visible.
- **"Obtain the item" unlock path**: hook `ItemStack`-acquisition broadly
  via `PlayerEvent.ItemPickupEvent` (world pickups) + inventory-slot-change
  detection is unreliable/expensive; instead, the authoritative trigger is
  narrower and matches the brief's intent — **crafting or otherwise
  receiving a ritual's output through the mod's own systems** (ritual
  completion itself; trades or loot tables that explicitly reference a
  ritual-output item, tagged via a lightweight `RitualOutputItemEvent`
  fired from that item's `onCraftedBy`/loot-context hook) — plus one
  general fallback: **first pickup of that specific item ID ever**,
  checked cheaply via a single per-item boolean cache diffed against
  `RecipeKnowledge` rather than scanning inventories every tick. This
  keeps the "obtain it any way → JEI unlocks" promise from the brief
  without an expensive always-on inventory scan.
- JEI category only ever needs the **client-side mirrored** knowledge
  cache from §2.2 — no capability access needed from the JEI plugin
  itself, it just asks `ClientKnowledgeCache.isMastered(recipeId)`.

---

## 7. MVP Scoping

### Phase 1 — minimum shippable (targets: SOA integration slot filled, core loop playable start-to-finish)

| Area | Included | Effort note |
|---|---|---|
| Glyphs | All 39 from §1, data + textures (no constellation art needed beyond simple line patterns) | symbol texture art is the biggest content-hours sink; code-side glyph registry is small |
| Knowledge | Full 3-tier model, capability + sync, `GlyphLearnedEvent`/`RitualMasteredEvent` | medium — capability sync is boilerplate-heavy but well-trodden |
| Journal | Codex GUI: glyph grid + detail page + readable-rituals tab | medium-large — most bespoke UI work in the mod |
| Star viewing | Astrolabe item, 2D overlay, procedural starfield + fixed constellation line patterns, trace-to-discover minigame | large — the overlay renderer + trace-matching is the single biggest engineering item |
| Ruins | 2–3 structure templates, carved-glyph block + rubbing kit + rubbing item | medium — structure NBT authoring is repeatable once the block/item pipeline exists |
| Tablets | Tablet item (data-component-driven, single item covers all tablet configs), common+boss tiers | small — mostly loot-table wiring, which is pack-side |
| Ritual engine | Altar block, pedestal block, ground-item matching, fluid tank, all 9 condition checks, recipe type + JSON loader | large — this is the mechanical core, budget the most QA time here |
| JEI | Category + hide/unhide runtime wiring, obtain-triggers-unlock fallback | medium |
| Content | The 10–15 example rituals in §8, including Chaos Ingot | small once the engine exists — this is data authoring |

### Phase 2+ — later

- Additional verb glyphs (e.g. a dedicated `Sacrificium`-adjacent "curse"
  verb, or a multi-altar "greater ritual" verb) — deferred because new
  verbs are the highest-grammar-cost content type (§1 note).
- "Near miss" journal hints when a ritual attempt matches zero recipes but
  is close (per §4.2 step 3's noted backlog item).
- Server-shareable "known constellations" map view (a purely cosmetic
  convenience, not core to the loop).
- Additional structure biome variants / boss-specific tablet art beyond
  the shared common/boss textures.
- A dedicated Ritual Wand alternative trigger, if playtesting shows the
  empty-hand-click trigger causes accidental activation near altars.
- Cross-mod integration hooks beyond SOA's quest system (e.g. exposing
  glyph knowledge to a stats/achievement mod).

---

## 8. Example Ritual Recipes (glyph sentences)

Each entry: sentence (grammar-ordered glyph list) → rendered flavor text
(once fully LEARNED) → mechanical result. All are Phase-1 candidates.

1. **Chaos Ingot** — `verb_altare, mat_adamas, mod_chaos, cond_fulgur`
   *"At the altar, offer the unbreakable metal; let chaos take it, beneath
   a raging sky."*
   → Blackstone-context altar, netherite ingot on pedestal, requires
   thunderstorm → outputs `epigraphy:chaos_ingot`.

2. **Order Ingot** — `verb_altare, mat_adamas, mod_ordo, cond_dies,
   cond_caelum`
   *"At the altar, offer the unbreakable metal; let order take it, beneath
   the open day."*
   → mirror-image counterpart to Chaos Ingot; clear symmetry teaches the
   grammar pattern to players early.

3. **Sunsteel** (working name) — `verb_altare, mat_ferrum, mod_lux,
   cond_dies, cond_caelum`
   *"At the altar, offer iron; let light take it, beneath the open day."*
   → iron ingot + full sun exposure → a light-infused metal ingot, early
   material upgrade.

4. **Moonsilver** — `verb_altare, mat_aurum, mod_umbra, cond_plenilunium,
   cond_caelum`
   *"At the altar, offer gold; let darkness take it, beneath the full
   moon."*
   → gold + specifically full moon → parallel "night-tier" upgrade metal.

5. **Ember Rod** — `verb_altare, obj_baculum, mod_flamma, cond_infernus`
   *"At the altar, offer the rod; let flame take it, in Hell."*
   → blaze rod on pedestal, altar in the Nether → a wand/focus item used
   by later rituals as a catalyst tool.

6. **Frost Rod** — `verb_altare, obj_baculum, mod_glacies, cond_nox`
   *"At the altar, offer the rod; let frost take it, by night."*
   → parallel counterpart to Ember Rod, ground-item packed ice + night.

7. **Bound Compass** — `verb_altare, obj_speculum, verb_vincire,
   cond_caelum`
   *"At the altar, offer the mirror; bind it, beneath the open sky."*
   → compass + mirror on pedestal, open sky → produces a "soulbound"
   compass that always points to the altar it was bound at (introduces
   `verb_vincire` mechanically as location-binding).

8. **Blood Contract** — `verb_altare, verb_sacrificium, mat_sanguis,
   obj_catena, cond_nox`
   *"At the altar, sacrifice; offer blood, with the chain, by night."*
   → consumes redstone (blood-tag) + a chain item, plus a sacrificed
   (dropped) weak hostile mob nearby, at night → produces a bound
   trinket that grants a minor buff, first recipe combining two verbs.

9. **Called Familiar** — `verb_altare, verb_vocare, mat_os, mod_vita,
   cond_dies`
   *"At the altar, call; offer bone, let life take it, by day."*
   → bone + day → summons a small passive familiar entity (Phase-1 scope:
   cosmetic/utility only, e.g. lights up glyphs it walks near).

10. **Withering Effigy** — `verb_altare, verb_vocare, mat_os, mod_mors,
    cond_nox, cond_infernus`
    *"At the altar, call; offer bone, let death take it, by night, in
    Hell."*
    → high-condition-count recipe (both `cond_nox` and `cond_infernus`
    can't literally both be "true" under vanilla's Nether always-dark
    rule, which is intentional — this recipe is Nether-only, `cond_nox`
    here doubles as "ambient darkness" rather than a strict overworld
    day/night check, a documented condition-glyph nuance) → summons a
    hostile-but-tamed effigy mob.

11. **Stormglass Lens** — `verb_altare, obj_speculum, mat_vitrum,
    cond_fulgur, cond_aqua`
    *"At the altar, offer the mirror and glass; beneath a raging sky, by
    water."*
    → glass + mirror, near water, during thunderstorm → crafts the lens
    component used by the Astrolabe's tier-2 upgrade (ties ritual engine
    back into the star-viewing tool, per the brief's cross-system intent).

12. **Ashen Binding** — `verb_altare, verb_vincire, mat_cinis, mod_ordo`
    *"At the altar, bind; offer ash, let order take it."*
    → no condition glyphs at all — a deliberately "always available"
    recipe, so Phase-1 has at least one ritual reachable with zero
    environmental gating, useful for tutorializing the altar mechanic
    itself before teaching conditions.

13. **Glasswrought Vessel** — `verb_altare, obj_vas, mat_vitrum, mod_lux`
    *"At the altar, offer the vessel and glass; let light take it."*
    → crafts an upgraded fluid vessel item with larger capacity, used to
    feed the altar's own fluid tank faster — a quality-of-life ritual
    output, showing rituals aren't only about exotic materials.

14. **Crownwrought Regalia** — `verb_altare, obj_corona, mat_aurum,
    mod_ordo, cond_dies, cond_caelum`
    *"At the altar, offer the crown and gold; let order take it, beneath
    the open day."*
    → equipment-slot cosmetic/light-buff headpiece, a mid-tier
    "prestige" item that reads as a clear reward sentence once all
    glyphs are known.

15. **Wraithsteel** — `verb_altare, mat_ferrum, mod_umbra, verb_sacrificium,
    cond_nox`
    *"At the altar, offer iron; let darkness take it; sacrifice, by
    night.""*
    → iron + a sacrificed weak hostile mob nearby, at night → a
    darkness-aligned iron variant, direct counterpart pairing with
    Sunsteel (#3) using the same verb+material shape but the opposite
    modifier and condition, again reinforcing the recombination-teaches-
    grammar design goal from §1.

---

## Open questions / flagged for playtesting

- Empty-hand altar trigger vs. dedicated Ritual Wand (§4.2 step 1).
- Exact trace-tolerance and hold-duration tuning for the Astrolabe
  minigame (§3.1) — needs hands-on iteration, can't be right on paper.
- Whether `cond_nox`/`cond_infernus` co-occurrence (see recipe #10) needs
  a real "ambient darkness" condition type of its own instead of reusing
  `cond_nox`'s check with a dimension override — leaning yes, flagged as
  a likely Phase-1 code addition once the first Nether ritual is
  playtested.

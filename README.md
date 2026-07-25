# Epigraphy

A Minecraft 1.20.1 Forge magic mod about discovery and literacy: find glyphs in the stars,
in ancient ruins, and on tablets dropped by bosses; learn what they mean; then read and
perform the stone-glyph rituals they describe. Built as an original, clean-room mod for the
**Souls of Avarice** modpack (filling the Astral Sorcery slot) — own code, art, and names
throughout; mechanics-inspired only, nothing derived or copied.

See [`docs/DESIGN.md`](docs/DESIGN.md) for the full design: glyph vocabulary, the
Seen/Learned/Mastered knowledge system, the three discovery systems (star viewing, ruin
rubbings, boss tablets), the ritual engine, datapack JSON schemas, JEI integration, MVP
scoping, and example ritual recipes.

## Project layout

- `docs/DESIGN.md` — design document.
- `src/main/java/dev/yoitsoul/epigraphy/` — mod source, split into `glyph`, `knowledge`,
  `ritual`, `block`, `item`, `client`, and `datagen` packages.
- `src/main/resources/data/epigraphy/epigraphy/glyphs|tablets/` and
  `src/main/resources/data/epigraphy/recipes/` — starter datapack-driven content (a few
  glyphs, one tablet, and the Chaos Ingot ritual recipe from the design doc).

## Building

Standard Forge 1.20.1 MDK / ForgeGradle layout:

```
./gradlew build
```

(First run downloads Minecraft/Forge/mapping artifacts and will take a while.)

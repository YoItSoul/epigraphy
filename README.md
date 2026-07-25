# Epigraphy

A Forge 1.20.1 magic mod about **reading the world**. Discover ancient **glyphs**
hidden in the stars, ruins, and structures; work out the **rune words** they form;
and perform infusion rituals whose recipes read like a language you had to learn.

Inspired by **Astral Sorcery** and **Thaumcraft**, rebuilt around one idea: you
don't unlock progression by grinding — you unlock it by *learning to read*.

## Core vocabulary

| Term | Meaning |
|------|---------|
| **Glyph** | A single symbol, mapping to one Latin word (`FLAMMANS`, `CHAOS`). **Discovered** in the world. |
| **Rune word** | A set of 2–3 glyphs naming one concrete thing — `FLAMMANS · VIRGA` → Blaze Rod. **Guessed** by the player. |
| **Runes** | The whole system: every glyph and rune word together. |

*Glyphs are discovered; rune words are guessed.*

## Design docs

The design is being developed doc-first, before code. Start here:

- **[docs/DECISIONS.md](docs/DECISIONS.md)** — the running decisions log: what's
  settled (terminology, no-GUI + the 20-slot codex, passive glyph learning,
  backlash, sky-in-v1) and what's still open. **Read this first** — where other
  docs conflict, this one wins.
- **[docs/DESIGN.md](docs/DESIGN.md)** — vision, pillars, the core loop, and a
  fully worked example (the Chaos Ingot).
- **[docs/RUNES.md](docs/RUNES.md)** — the language: terminology, glyph lexicon,
  how 2–3 glyph rune words are formed, decoded, and read.
- **[docs/DISCOVERY.md](docs/DISCOVERY.md)** — how glyphs hide in worldgen and the
  sky, tablets, and the hand codex (submit + seek).
- **[docs/RITUALS.md](docs/RITUALS.md)** — the altar, pedestals, infusion fluid,
  world conditions, backlash, and the JSON recipe schema.
- **[docs/KNOWLEDGE.md](docs/KNOWLEDGE.md)** — the tiers of knowing and how exact
  recipes stay locked until you *do* the thing.
- **[docs/ROADMAP.md](docs/ROADMAP.md)** — architecture, package layout, and a
  phased build order.

## The one-paragraph pitch

Find a symbol carved into a ruin or written in the stars and *record* it. See it
enough times and you *learn* the word it stands for. Then the real puzzle starts:
a ritual is described in 2–3 glyph **rune words**, and *"Flaming · Rod"* is a clue
you have to solve — you cycle those glyphs into your codex, hit submit, and find
out it means **Blaze Rod**. Build a Blackstone Altar, ring it with blaze rods, and
quench netherite in liquid starlight beneath a thunderstorm. Guess wrong and go in
half-read, and the ritual bites back. Only once you've held the **Chaos Ingot** in
your hand does its exact recipe get written down. Knowing is not having.

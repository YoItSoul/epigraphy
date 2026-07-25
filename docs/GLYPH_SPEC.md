# Glyph Construction Spec

The deterministic rule that turns a Latin lemma into a 32×32 symbol. Every lit pixel
means something: it is either **a letter of the glyph's own name** or **a stroke
joining two letters in order**. Nothing is decorative.

This is the implementation contract for the renderer (`RUNES.md` §5, D14). Given the
same `lemma` and `determinative.class`, it must always produce byte-identical art.

---

## 1. Principle: the name *is* the glyph

> A glyph is its lemma, written as a path through a lettered grid, enclosed in a
> frame that declares its class.

Two layers, both meaningful, neither decorative:

| Layer | Encodes | Rendered as |
|---|---|---|
| **Stave path** (interior) | the lemma, letter by letter, **in order** | nodes + connecting strokes |
| **Class frame** (exterior) | the determinative class | one of five shapes |

Because the path traces letters *in sequence*, two words with the same letters in a
different order produce visibly different glyphs.

The finished glyph is then made **bilaterally symmetric** (§4) — the seed path plus
its mirror — so that every symbol in the lexicon reads as deliberately designed
rather than arbitrarily traced.

---

## 2. The stave grid

The interior is a **5 × 5 lattice of 25 nodes**. Each node is one letter of the
**classical Latin alphabet**, which has exactly **23 letters** — no J, U, or W. Two
nodes are held in reserve.

```
        col 0   col 1   col 2   col 3   col 4

row 0     A       B       C       D       E
row 1     F       G       H       I       K
row 2     L       M       N       O       P
row 3     Q       R       S       T       V
row 4     X       Y       Z       ·       ·
                                 └── reserved ──┘
```

Row-major, `index = row * 5 + col`. The mapping is fixed forever — changing it
invalidates every existing glyph, so it is frozen at v1.

### 2.1 Classical orthography is mandatory

Lemmas are normalised to classical forms before encoding:

| Modern | Classical | Example |
|---|---|---|
| `U` → `V` | Romans carved V | `PULVIS` → `PVLVIS` |
| `J` → `I` | Romans carved I | `IANUA` → `IANVA` |
| `W` | not a Latin letter | reject at validation |

This is not flavour — it's what makes 23 letters fit a 25-node lattice, and it's how
the words would actually have been cut in stone.

---

## 3. The path

1. **Normalise** the lemma (§2.1), uppercase, strip non-letters.
2. **Truncate** to at most **5 letters** (§3.2) — symmetry doubles the drawn
   density, so the seed stays short.
3. **Map** each letter to its node.
4. **Draw** a node mark at each, and a **stroke** from each node to the next in
   sequence. This is the **seed path**.
5. **Mirror** the seed about the vertical centre axis and draw the reflection too
   (§4). The union is the glyph.

### 3.1 Worked examples

**`VIRGA`** → V·I·R·G·A → `(4,3) (3,1) (1,3) (1,1) (0,0)`

```
 A · · · ·      A ·  ·  ·  ·        A = end
 · G · I ·      ·  G ·  I  ·        path: V → I → R → G → A
 · · · · ·      ·  ·  ·  ·  ·
 · R · · V      ·  R ·  ·  V        a wide diagonal sweep,
 · · · · ·      ·  ·  ·  ·  ·       closing on the corner
```

**`CHAOS`** → C·H·A·O·S → `(2,0) (2,1) (0,0) (3,2) (2,3)`

```
 A · C · ·      the A-corner hook plus a drop to O
 · · H · ·      makes a shape nothing else produces
 · · · O ·
 · · S · ·
 · · · · ·
```

**`FLAMMANS`** → 8 letters, so truncated (§3.2) to `FLMMNS` →
`(0,1) (0,2) (1,2) (1,2) (2,2) (2,3)`

A dense cluster low-left, with a **repeat marker** where M follows M.

### 3.2 Truncation: the abjad rule

Lemmas longer than 5 letters drop **vowels after the first letter**, keeping the
initial — the strongest recognition cue. `FLAMMANS` → `F` + `LMMNS` = `FLMMNS`,
then trimmed to `FLMMN`.

If still over 5, truncate from the end.

This is not a hack. Semitic abjads write consonants only, and Roman inscriptions
abbreviate relentlessly (`V.S.L.M.`, `D.M.`, `COS` for *consul*). A shortened stave
is exactly what a stonecutter would have done.

Authors may override with an explicit `stave` field when the default reads poorly:

```jsonc
{ "lemma": "FLAMMANS", "stave": "FLMS" }   // author's preferred abbreviation
```

### 3.3 Repeated letters

When the path would revisit the node it is already on (`M` → `M`), draw a **repeat
notch**: a single pixel offset diagonally from the node. Two identical letters in a
row are visible without a stroke to nowhere.

Non-adjacent repeats (`A … A` in `FLAMMANS`) simply revisit the node; the path
crosses itself, which is legible and distinctive.

---

## 4. Symmetry: the mirror rule

**Every glyph is bilaterally symmetric about its vertical centre axis.** The raw
stave path from §3 is the *seed*; the glyph is the seed **plus its reflection**.

```
   seed path            reflection           the glyph
   (V·I·R·G·A)          (mirrored)           (union)

   · · · · A            A · · · ·            A · · · A
   · G · I ·            · I · G ·            · G · G ·      ← I and G both
   · · · · ·      +     · · · · ·      =     · · · · ·         appear twice
   · R · · V            V · · R ·            V R · R V
   · · · · ·            · · · · ·            · · · · ·
```

Reflection maps column 0 ↔ 4 and column 1 ↔ 3. Column 2 — `C H N S Z` — lies on the
axis and mirrors onto itself, so those letters draw once, not twice.

### 4.1 Why vertical, not horizontal

Left–right symmetry is what reads as **writing**: runes, alchemical sigils, heraldic
charges and maker's marks are overwhelmingly mirror-symmetric about a vertical stem.
Top–bottom symmetry reads as a *playing card* or an inkblot — the eye stops looking
for a top, and the mark loses the sense of having been cut in a direction. Vertical
also survives the frames, all five of which are already vertical-axis symmetric.

### 4.2 What it costs, honestly

- **Density doubles.** Five seed segments become up to ten drawn ones. To hold
  legibility at 32 px, symmetric mode truncates the stave to **5 letters, not 6**
  (§3.2 otherwise unchanged).
- **A new collision class.** Two different words can now render identically if one's
  seed path is the *mirror image* of the other's. `A·E` and `E·A` both become the
  same figure. The validator must compare **mirrored path sets**, not just raw
  sequences (§7).

Neither is severe, and the payoff is that every glyph in the lexicon looks
deliberately *designed* rather than arbitrarily traced — which is the whole reason
to want symmetry.

### 4.3 Frames must be symmetric too

All five frames are vertical-axis symmetric by construction, and any frame added
later must be:

| Frame | Vertical axis | Horizontal axis |
|---|---|---|
| Hexagon | ✔ | ✔ |
| Circle | ✔ | ✔ |
| Plinth | ✔ | ✘ — the base is deliberate, it gives the glyph a "ground" |
| Open base | ✔ | ✘ — same |
| Doubled ring | ✔ | ✔ |

Plinth and open-base break horizontal symmetry on purpose: a structure sits on
something, and a quality rests on a baseline. That asymmetry is the visual cue that
they are *not* enclosures.

---

## 5. Pixel geometry (32 × 32)

| Element | Size | Placement |
|---|---|---|
| Canvas | 32 × 32 | — |
| Frame | outer 2 px band | inset 1 px from edge |
| Stave field | 22 × 22 | centred, 5 px inset |
| Node pitch | 5 px | nodes at 6, 11, 16, 21, 26 on both axes |
| Node mark | 2 × 2 px | centred on its node |
| Stroke | 1 px | Bresenham, hard pixels — no anti-aliasing |
| Repeat notch | 1 px | +2 px diagonal from node centre |

**No anti-aliasing anywhere.** Every pixel is on or off. This keeps the art
Minecraft-native, keeps "each pixel has meaning" literally true, and guarantees the
renderer is deterministic across platforms.

---

## 6. Accessibility: shape carries everything

**Colour encodes nothing.** This is a hard rule, not a preference. Every distinction
a player must make is carried by **shape, position, or presence**:

| Distinction | Carried by | Never by |
|---|---|---|
| Which glyph is this? | the stave path | colour |
| What class is it? | the frame shape (5 distinct silhouettes) | colour |
| Do I know it yet? | how much of the glyph is drawn (§5.1) | colour |
| Is this a clause marker? | doubled-ring frame | colour |

Colour may only ever be **redundant reinforcement** — if a build tints celestial
glyphs, the circle frame must still be doing the whole job on its own. Removing all
colour from the game must lose zero information.

The five frames were chosen for silhouette contrast, distinguishable at 16 px and in
monochrome:

| Frame | Silhouette cue |
|---|---|
| Hexagon | angled corners, flat top |
| Circle | no corners |
| Plinth | flat base wider than body |
| Open base | **not closed** — a single baseline stroke |
| Doubled ring | two concentric outlines |

Open-base vs. the others is the strongest contrast in the set, which is deliberate:
it marks the element/quality glyphs that can never head a word (`RUNES.md` §5.2).

### 6.1 Knowledge tier is drawn, not tinted

The glyph's own completeness encodes what the player knows — a progressive reveal
that needs no colour and no UI:

| Tier | Rendered | Reads as |
|---|---|---|
| **0 · Unknown** | frame only, stave field empty | "a symbol, meaning nothing" |
| **1 · Sighted** | frame + **node marks**, no strokes | "I've seen the shape but can't read it" |
| **2 · Learned** | frame + nodes + **full stroke path** | complete, legible |

The letters are literally present but unconnected at Tier 1 — you have the dots and
not the line. That is a precise visual metaphor for partial decipherment, and it's
the same information the text layer conveys with `???`.

---

## 7. Why a stave path, not overlaid letterforms

The earlier proposal overlaid Standard Galactic Alphabet letterforms directly. The
stave path is strictly better on the criteria that matter:

| | Overlaid SGA | Stave path |
|---|---|---|
| Every pixel meaningful | no — strokes collide arbitrarily | **yes — letter or connector** |
| Deterministic | yes | **yes** |
| Legible at 32 px | muddy with 6+ letters | **clean — 1 px strokes, fixed nodes** |
| Distinct across lexicon | siblings look alike | **path shape diverges fast** |
| Encodes letter *order* | no | **yes** |
| Tier reveal possible | no clean split | **yes — nodes vs. strokes** |

The SGA *aesthetic* is retained deliberately — hard geometric strokes, square caps,
dot terminals, no curves — so glyphs still read as kin to the enchanting table. What
changes is that the construction is now **systematic rather than impressionistic**.

### 7.1 Precedent

The stave path is close to **Ogham**, the ancient Irish alphabet that survives almost
entirely as *stone inscriptions* — letters encoded as counted strokes against a stem
line, purely positional, no pictography. It is also close to **Nordic bind-runes**,
where several runes are combined on a shared stave into one composite mark.

Both are real epigraphic writing systems that encode letters by **position and count
rather than picture** — which is exactly the property that keeps Epigraphy's glyphs
cryptic enough to be worth deciphering.

---

## 8. Validation

The renderer and datapack loader must reject:

- A `lemma` containing `W`, or any character outside A–Z after normalisation.
- A `lemma` or `stave` that normalises to fewer than 2 letters (nothing to path).
- A `stave` override longer than 5 letters.
- Two glyphs whose normalised, truncated staves are **identical** — they would render
  as the same art. (The lexicon-collision rule from `AUTHORING.md` §3, one level down:
  distinct lemmas can still collide *after* truncation, e.g. `FLAMMANS` and
  `FLAMMENS` both → `FLMMN`. Resolve with an explicit `stave`.)
- **Two glyphs whose *mirrored* figures coincide.** Symmetry (§4) introduces this:
  a seed path and its reflection render the same glyph, so `A·E` and `E·A` are
  visually identical despite being different sequences. Compare the **drawn edge
  set after mirroring**, not the raw letter sequence.
- A glyph whose `category` is `element` declaring a `determinative` (`RUNES.md` §5.2).
- A frame that is not vertical-axis symmetric (§4.3).

The two collision checks are what will actually fire during authoring, and they are
worth surfacing loudly: two glyphs that *look* identical are far worse than two that
merely sound alike.

---

## 9. Reference implementation sketch

```
normalise(lemma):
    s = uppercase(lemma)
    s = s.replace('U','V').replace('J','I')
    s = keep only A-Z
    reject if contains 'W'
    return s

stave(glyph):
    s = glyph.stave ?? normalise(glyph.lemma)
    if len(s) > 5:
        s = s[0] + [c for c in s[1:] if c not in 'AEIOVY']
        s = s[:5]
    return s

node(letter):                       # frozen at v1 — never reorder
    ALPHABET = "ABCDEFGHIKLMNOPQRSTVXYZ"
    i = ALPHABET.index(letter)
    return (col = i % 5, row = i / 5)

mirror(n):                          # reflect about the vertical centre axis
    return (col = 4 - n.col, row = n.row)

render(glyph, tier):
    draw frame(glyph.determinative?.class ?? glyph.category)   # already symmetric
    if tier == UNKNOWN: return
    seed = [node(c) for c in stave(glyph)]
    full = seed + [mirror(p) for p in seed]        # nodes drawn on both sides

    for p in dedupe(full): drawNodeMark(p)
    if tier == SIGHTED: return                     # dots, no line

    for (a, b) in consecutive(seed):
        for (p, q) in [(a, b), (mirror(a), mirror(b))]:
            if p == q: drawRepeatNotch(p)
            else:      drawLine(p, q)              # Bresenham, 1 px, no AA
```

`dedupe` matters for the centre column (`C H N S Z`), whose nodes are their own
mirror — they must be drawn once, not twice over.

Deterministic, dependency-free, and cheap enough to generate the whole lexicon into
an atlas at resource-reload time.

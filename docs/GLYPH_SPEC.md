# Glyph Construction Spec

The deterministic rule that turns a Latin lemma into a **16 × 16** symbol — Minecraft's
own item resolution.

> **A glyph is one continuous figure**: two stacked letter-forms that link at the
> centre, standing on a foot that tallies the word's letters. It is **chiselled into an
> octagonal stone tile** lit from the top-left, and the groove is inlaid with a pigment
> hashed from the word's own name.

**No frame, no floating marks, no disconnected pieces.** The whole symbol is a single
unbroken shape — something you could cut with one chisel.

This is the implementation contract for the renderer (`RUNES.md` §5, D14/D15/D17/D18).
Given the same `lemma` it must always produce byte-identical art.

### Why 16 × 16 forced two letters

32 × 32 held three six-row letter zones comfortably. 16 × 16 has **a quarter the
pixels** and holds two. Rather than shrink the forms into illegibility, the third
letter was dropped and **word length moved into the foot**.

Uniqueness survives: `VITA` (4 letters), `VIRGA` (5) and `VIGILIA` (7) all abbreviate
to `VI` and remain three different tiles because their feet differ. Two glyphs collide
only if they share their first two letters **and** their length — see §8, which treats
that as a *language* bug rather than an art bug.

Two other things had to go to make 16 × 16 work, and both were improvements:

| Dropped | Why it was affordable |
|---|---|
| The frame | cost a quarter of the usable area and never carried identity |
| Disconnected marks (pips, split bars, posts) | continuity rules them out anyway |

### Why continuity is structural, not checked

**Every letter-form touches the centre column at its top and bottom row.** Stack two
and they link automatically; the foot hangs off the lower one. The geometry cannot
produce a loose piece, so continuity is a property of the construction rather than
something validated and patched afterwards.

---

## 1. Anatomy

```
        VIRGA -> "VI"          blank tile
      ╱▔▔▔▔▔▔▔▔▔▔╲          ╱▔▔▔▔▔▔▔▔▔▔╲
     ╱   ╱▔▔╲     ╲ ← V    ╱            ╲
     │   ╲__╱      │       │             │   bare stone —
     │   ╱▔▔╲      │ ← I   │             │   unknown glyph,
     │   ╲__╱      │       │             │   empty codex slot,
     ╲ ▁▁▁▁▁▁▁▁▁  ╱ ←foot  ╲            ╱   uninscribed tablet
      ╲▁▁▁▁▁▁▁▁▁▁╱          ╲▁▁▁▁▁▁▁▁▁▁╱
```

| Layer | Encodes | Drawn as |
|---|---|---|
| **Form 1** (rows 2–7) | the lemma's **first** letter | one of 23 closed shapes |
| **Form 2** (rows 7–12) | the lemma's **second** letter | one of 23 closed shapes (sharing row 7) |
| **Foot** (row 13) | the word's **length** | a tally bar, §3 |
| **Pigment** | nothing on its own — reinforcement | groove inlay, §6 |
| **Stone** | nothing — material only | octagon, gradient + grain, §5 |

### 1.1 The blank glyph

A tile with **no cuts at all** — bare stone, still an octagon. It serves three jobs with
one asset: an **unknown glyph** (Tier 0), an **empty codex slot**, and an **uninscribed
tablet**.

---

## 2. The letter forms

23 letters, 23 **closed shapes**. Classical Latin has no J, U or W, so lemmas normalise
to the orthography they would have been carved in (`PULVIS` → `PVLVIS`).

| | | | | |
|---|---|---|---|---|
| **A** shaft | **B** lozenge | **C** wide lozenge | **D** narrow lozenge | **E** box |
| **F** lozenge + bar | **G** triangle | **H** wedge | **I** twin lozenge | **K** lozenge + shaft |
| **L** shaft + bar | **M** shaft + two bars | **N** horns | **O** roots | **P** flask |
| **Q** flask + bar | **R** barrel | **S** kite | **T** stem + skirt | **V** cap + stem |
| **X** nested lozenge | **Y** box + bar | **Z** double chevron | | |

**The form table is frozen at v1.** Changing one invalidates every glyph using that
letter.

Every form spans **six rows** and **must include the centre columns at its top and
bottom row** — that contract is what makes any two link into one continuous figure.
Forms are chosen for mutual contrast: a kite, a barrel, a flask and a double chevron
share no silhouette.

---

## 3. The mark, and the tally foot

1. **Normalise** the lemma — uppercase, `U`→`V`, `J`→`I`, strip non-letters.
2. Take the **first two letters**; each selects a form.
3. Count the whole normalised word and **tally** it into the foot.
4. An explicit `mark` overrides step 2 if two words collide (§8).

### 3.1 The foot is a tally

The bar widens one step per letter up to four. Past four its ends **turn up into a
serif** — the tally's hand mark — and the width starts counting again:

```
t     = clamp(length − 2, 0, 7)
width = 1 + (t mod 4)          // half-widths either side of the axis
serif = t ≥ 4                  // the hand mark
```

which reads back as **`letters = 2 + (width − 1) + 4 × serif`**. Eight distinguishable
lengths out of a bar that never exceeds four half-widths — and that ceiling is exactly
what holds the widest foot **2 px clear** of the octagon's bottom chamfer (§5.1).

| Lemma | Normalised | Mark | Letters | Width | Serif |
|---|---|---|---|---|---|
| `VITA` | VITA | `VI` | 4 | 3 | — |
| `VIRGA` | VIRGA | `VI` | 5 | 4 | — |
| `VIGILIA` | VIGILIA | `VI` | 7 | 2 | ✔ |
| `CAELUM` | CAELVM | `CA` | 6 | 1 | ✔ |
| `TENEBRAE` | TENEBRAE | `TE` | 8 | 3 | ✔ |

Three `VI` words, three different tiles — the foot does it.

---

## 4. Symmetry

Every glyph is **bilaterally symmetric about its vertical centre axis**, which sits
between pixel columns 7 and 8. The octagonal tile is symmetric about the same axis.

Symmetry is structural, not something to hand-check: the finished bitmap is passed
through a `symmetrise` step that ORs every column with its mirror. **A glyph therefore
cannot come out asymmetric by accident**, and stroke coordinates need not be
mirror-exact.

Left–right rather than top–bottom because mirror symmetry about a vertical axis is
what reads as *writing* — runes, alchemical sigils, heraldic charges, maker's marks.
Top–bottom symmetry reads as a playing card.

---

## 5. The tile: an octagon, cut in three depths

### 5.1 Silhouette

The tile is the 16 × 16 square with its **four corners chamfered by 3**, so the
silhouette itself reads as a cut stone rather than a sprite. Outside the octagon the
texture is **transparent**.

```
inTile(x,y)  ⟺  min(x, 15−x) + min(y, 15−y) ≥ 3        // 232 of 256 px
```

#### The 2 px margin

**No part of a glyph may come within 2 px of the stone's edge**, chamfered corners
included. It is a measured constraint, not an eyeballed one: for every cut pixel, walk
outward in Chebyshev rings to the nearest pixel with `inTile == false`; if any is closer
than 2, fail the build.

Two design facts fall straight out of it, and neither is negotiable afterwards:

- the tally's width ceiling is **4**, not 5 (§3.1) — a 5-wide foot puts its end pixel
  diagonally adjacent to the bottom chamfer;
- the two letter-forms **share row 7**. Both are already obliged to occupy the centre
  columns at their top and bottom row, so sharing that row is exactly where they were
  going to link anyway. It costs nothing and buys the row the margin needs.

**The silhouette is universal — it does not vary per glyph** (D17). Side count is
deliberately *not* tied to word length: length is already in the foot, 16 px holds
about three distinguishable silhouettes before they turn to mush, and Latin lemma
lengths cluster so hard at 4–7 that the encoding would read as random. The one variable
that would be worth the silhouette is **grammatical class** — thing / place / condition,
which is what a player parses first when guessing a rune word — and that is deferred
until the lexicon's classes are settled (`DECISIONS.md` Q10).

### 5.2 Depth, from one height field and one light

```
H = −1   void (outside the octagon)
H =  0   incised stroke
H =  1   stone surface
```

Each pixel is compared against its three **up-left** and three **down-right**
neighbours, diagonals double-weighted:

```
d = 2(H−H₍ₓ₋₁,ᵧ₋₁₎) + (H−H₍ₓ,ᵧ₋₁₎) + (H−H₍ₓ₋₁,ᵧ₎)
  − 2(H−H₍ₓ₊₁,ᵧ₊₁₎) − (H−H₍ₓ,ᵧ₊₁₎) − (H−H₍ₓ₊₁,ᵧ₎)

f = clamp(1 + amp·d, 0.42, 1.68)          amp = 0.155 (deep) | 0.085 (shallow)
```

Higher than up-left turns into the light; higher than down-right turns away. **That one
rule produces all of the depth** — the bevelled rim of the tile, the chamfered corners,
the shadowed wall on the upper-left of every groove and the lit wall on its lower-right.
Nothing is special-cased, and a two-pixel stem falls out as a true V-groove: dark left
wall, bright right wall.

### 5.3 Stone

```
value(x,y) = lerp(hi, lo, (x+y) / 2(S−1)) + grain(x,y)
grain(x,y) = deterministic hash of (x,y), range ±8
hi = (201,195,183)   lo = (112,107,99)     // one material — the texture is one image
```

The grain is a **hash, not randomness** — the renderer must stay byte-identical.
**The gradient is material, not information**: flatten every tile to one grey and
nothing is lost.

### 5.4 Pixel geometry

| Element | Value |
|---|---|
| Canvas | **16 × 16**, no anti-aliasing, transparent outside the octagon |
| Chamfer | 3 per corner (232 of 256 px opaque) |
| Mirror axis | between columns 7 and 8 |
| Form 1 | rows 2–7 · **Form 2** rows 7–12 — they **share row 7** |
| Form bounds | x 2–13; must include (7,8) at the form's top and bottom row |
| Foot | row 13, centred, `1 + ((len−2) mod 4)` half-widths; serif rises into row 12 |
| Margin | **≥ 2 px** from every cut pixel to the nearest transparent pixel |
| Mean ink | ~52 of 256 pixels |

---

## 6. Pigment: authored, with the name as fallback

The groove floor holds the glyph's own colour. **Two sources, in order:**

| | Source | When |
|---|---|---|
| **Static** | the glyph's authored `pigment` field | whenever it is present and parses |
| **Dynamic** | FNV-1a over the normalised lemma | missing, malformed, out of range, or pure black |

```
static:   rgb = parseHex(glyph.pigment)          // "#RRGGBB", case-insensitive
dynamic:  k   = FNV-1a(normalise(lemma))         // 32-bit
          hue = k mod 360
          sat = 0.52 + ((k >> 10) mod 16)/100    // 0.52 .. 0.67
          rgb = hsl(hue, sat, 0.42)

both:     rgb = rgb × (PIG_LUMA / luma(rgb))     // PIG_LUMA = 62, luma = Rec.709
```

**A bad `pigment` must never fail a datapack load.** Unparseable, wrong length, wrong
type, absent — all fall through to the hash silently. Log it at debug and move on; the
glyph is still perfectly usable, because the fallback is not a degraded mode.

**The fallback is the point, not the safety net.** It lets a modder add fifty glyphs in
an afternoon and get stable, per-word colour with no art decisions at all — nothing is
stored, so the same lemma is the same colour in every world, forever. The static layer
exists so the words that *deserve* a colour get the right one.

### 6.1 Only the hue survives

**Both paths are renormalised to one fixed relative luminance** (62 of 255, against
stone running 110–195), so a yellow groove and a blue groove cut exactly as deep.

**The author picks the hue; the renderer keeps the depth.** This is the load-bearing
step, and it is what makes the static layer safe: a hand-picked palette cannot brighten
one glyph into prominence or sink another into invisibility, which is the trap that
catches most authored rune palettes. Measured across the lexicon the spread is
**61.6 – 62.4**, under one percent — the same figure whether a glyph is authored or
hashed.

A consequence worth stating: **`#88AA88` and `#AACCAA` are the same pigment.** Authors
choose a hue and a saturation, nothing more.

### 6.2 Author for family, not for contrast

Because luminance is normalised away and colour carries nothing (§7), the useful thing a
hand-picked palette buys is **family relationship** — words that belong together looking
like they belong together. The shipped lexicon is grouped:

| Family | Glyphs | Band |
|---|---|---|
| Fire & hell | `IGNIS` `FLAMMANS` `INFERNVS` `SOL` | orange → deep red |
| Water, flow & air | `VNDA` `AQVA` `VENTVS` | blue → pale cyan |
| Life, wood & fleece | `VITA` `LIGNVM` `LANA` `PLVMA` | green → warm cream |
| Night, death & dark | `NOX` `TENEBRAE` `MORS` `OSSA` | indigo → violet → bone |
| Sky, stars & hours | `CAELVM` `STELLA` `LVNA` `AVRORA` `VIGILIA` `PLENVS` | sky → pale gold |
| Earth, stone & dust | `TERRA` `LAPIS` `FVNDVS` `PVLVIS` `GEMMA` | brown → grey |
| Metals | `FERRVM` `AVRVM` `METALLVM` `ADAMAS` | steel → gold → cyan |
| Realms & powers | `REGNVM` `FINIS` `CHAOS` | violet → pale teal → magenta |
| Creatures | `BESTIA` `DRACO` `CVSTOS` `VENENVM` | earth red → purple → acid |
| Implements | `GLADIVS` `DOLABRA` `SECVRIS` `PALA` `FALX` `ARCVS` `HAMVS` `FORFEX` `SCVTVM` `LORICA` | one band of worn metal |
| Ritual furniture | `ALTARE` `VIRGA` | sandstone → ochre |

`IGNIS`, `FLAMMANS` and `INFERNVS` visibly rhyme, which is the same compositional logic
the language itself runs on. **Two glyphs sharing a hue is allowed** and costs nothing —
ten implements in one grey band does not make two tools ambiguous, because their shapes
were already doing the work.

---

## 7. Accessibility: shape carries everything

**Colour never carries anything on its own** (D16). Desaturate the whole atlas and all
49 glyphs stay distinct — *verified in the audit, not asserted*, and verified against the
authored palette rather than only the hash. The pigment is
**redundant reinforcement**: a second, faster channel onto an identity that shape
already carries in full. That is the only footing on which colour is allowed in at all,
and it is what keeps the system safe for colourblind players.

| The player must tell… | Carried by | Reinforced by | Never by |
|---|---|---|---|
| which glyph this is | the two letter-forms | pigment hue | hue alone |
| how long the word is | the foot tally | — | hue |
| whether they know it yet | **depth of cut** (§7.1) | pigment present or absent | hue |

### 7.1 Knowledge tier is depth, not tint

| Tier | Rendered | Reads as |
|---|---|---|
| **0 · Unknown** | **the blank tile** — uncut stone | "a stone, meaning nothing" |
| **1 · Sighted** | the full figure, cut **shallow and unfilled** — weak bevel (`amp = 0.085`), groove holds bare stone-grey `(99,95,89)` instead of pigment | "seen, not yet taken down" |
| **2 · Learned** | cut to **full depth and inlaid** with the word's pigment | complete, legible |

The ladder runs **uncut → shallow and empty → deep and filled**. It is a *value*
difference before it is a colour one, so it survives desaturation and reads identically
to a colourblind player. This closes the gap left when the frame was removed.

---

## 8. Validation

The renderer and datapack loader must reject:

- A `lemma` (or `mark`) whose first two normalised characters aren't both A–Z, or that
  contains `W`.
- A `mark` override that isn't exactly two letters.
- **Two glyphs that render identically.** The mark is `(letter 1, letter 2, length
  tally)`, so two lemmas agreeing on all three *must* produce the same tile — `VELLUS`
  and `VENTVS` do. **That is a language bug, not an art bug**, and the fix is a synonym,
  not a renderer change: Latin offers one for nearly everything, which is why wool is
  `LANA`. The loader renders every glyph at load, hashes the bitmap, and refuses a
  duplicate; an explicit `mark` is the escape hatch when no synonym will do.
- **Never** a malformed `pigment`. It falls back to the hash (§6) — a colour typo must
  not be able to break someone's pack.
- A glyph whose `category` is `element` declaring a `determinative` (a grammar rule,
  `RUNES.md` §5.2 — unrelated to art).
- **Any lit pixel falling outside the octagon, or within 2 px of its edge.** Assert
  `inTile(x,y)` and `margin(x,y) ≥ 2` for every cut pixel (§5.1). Cheap, and it catches
  the class of bug where a stroke or a wide foot crowds or escapes the silhouette.

**Adding or altering a form requires re-auditing all 23** against each other, and
checking the new form includes its centre contacts at top and bottom row.

---

## 9. Reference implementation

```js
const S = 16, ALPHABET = "ABCDEFGHIKLMNOPQRSTVXYZ";   // frozen at v1
const normalise = s => s.toUpperCase().replace(/U/g,"V").replace(/J/g,"I").replace(/[^A-Z]/g,"");
const mark = g => (g.mark ?? normalise(g.lemma)).slice(0,2);

const CHAMFER = 3;
const inTile = (x,y) => x>=0 && y>=0 && x<S && y<S &&
                        Math.min(x,S-1-x) + Math.min(y,S-1-y) >= CHAMFER;

// 23 closed forms. Each spans 6 rows and MUST include the centre columns at its
// top and bottom row — that contract is what makes the figure continuous.
const FORMS = [ /* shaft, lozenge, box, flask, kite, barrel, … */ ];

function render(glyph){                                 // -> 1-bit cut mask
  const b = new Uint8Array(S*S), m = mark(glyph);
  FORMS[ALPHABET.indexOf(m[0])](b, 2);                  // rows 2..7
  FORMS[ALPHABET.indexOf(m[1])](b, 7);                  // rows 7..12 — shares row 7
  const t = clamp(normalise(glyph.lemma).length - 2, 0, 7);
  foot(b, 13, 1 + (t % 4), /* serif */ t >= 4);         // the tally, §3.1
  symmetrise(b);                                        // cannot come out asymmetric
  return b;
}

function paint(bits, lemma, tier){                      // cut mask -> stone tile
  const shallow = tier === SIGHTED;
  const pig = shallow ? [99,95,89]
                      : pigmentOf(lemma, glyph.pigment);  // §6 — static, else hashed
  const amp = shallow ? 0.085 : 0.155;
  const H = (x,y) => !inTile(x,y) ? -1 : (bits[y*S+x] ? 0 : 1);

  for (let y=0;y<S;y++) for (let x=0;x<S;x++){
    if (!inTile(x,y)) { setAlpha(x,y,0); continue; }     // outside the octagon
    const h = H(x,y);
    const d = 2*(h-H(x-1,y-1)) + (h-H(x,y-1)) + (h-H(x-1,y))
            - 2*(h-H(x+1,y+1)) - (h-H(x,y+1)) - (h-H(x+1,y));
    const f = clamp(1 + amp*d, 0.42, 1.68);             // §5.2 — one light, all the depth
    const base = h === 0 ? pig : stone(x,y);            // §5.3
    put(x, y, base.map(v => v*f));
  }
}
```

`grain` and the pigment hash are **deterministic functions**, never `Math.random` — the
renderer must produce byte-identical output.

**Audited** (49-lemma working lexicon, `VELLUS` excluded as a homograph of `VENTVS`):
all 23 forms distinct and inside the octagon; **49/49 glyphs distinct**; **49/49 still
distinct with colour stripped**; groove luminance 61.6–62.4 across every hue, authored and hashed alike; all 49 authored
pigments parse and every one names a lemma that exists; nine classes of malformed
`pigment` all fall through to the hash without throwing; every
output vertically symmetric; every glyph a **single connected component**; **tightest
margin to the stone's edge 2 px**; blank tile 232/256 px opaque and zero cuts.

The audit runs against the *live* renderer rather than a transcription of it — it
extracts the algorithm from the specimen page and executes it — so the numbers above
cannot drift away from the art they describe.

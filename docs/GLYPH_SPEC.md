# Glyph Construction Spec

The deterministic rule that turns a Latin lemma into a **16 × 16** symbol — Minecraft's
own item resolution.

> **A glyph is two stacked letter-forms** standing on a foot that tallies the word's
> letters, **chiselled into an octagonal stone tile** lit from the top-left, its groove
> inlaid with a pigment hashed from the word's own name.
>
> **Each letter is its own figure** — a gable, a thorn, a grate, a diamond, a coffer, a
> saltire — built from five carved parts.

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

### Continuity and mirror symmetry are no longer rules

Both did real work when a glyph was a closed outline. Dropping them is what let letters
lean, corner, and carry weight on one side — `I` (the horns) and `M` (three stones) are
drawn in separate pieces, and at this size detached marks read cleanly.

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
| **Form 1** (rows 2–7) | the lemma's **first** letter | one of 21 figures, §2 |
| **Form 2** (rows 7–12) | the lemma's **second** letter | one of 21 figures (sharing row 7) |
| **Foot** (row 13) | the word's **length** | a tally bar, §3 |
| **Pigment** | nothing on its own — reinforcement | groove inlay, §6 |
| **Stone** | nothing — material only | octagon, gradient + grain, §5 |

### 1.1 The blank glyph

A tile with **no cuts at all** — bare stone, still an octagon. It serves three jobs with
one asset: an **unknown glyph** (Tier 0), an **empty codex slot**, and an **uninscribed
tablet**.

---

## 2. The letter forms: 21 figures

**21 letters, 21 figures.** Not one skeleton with variations — that was the mistake
behind every earlier table. Each letter is built from a small shared vocabulary of
carved parts:

| Part | |
|---|---|
| **stave** | a 1 px upright |
| **bar** | a 1 px lintel |
| **diagonal** | a slash |
| **chevron** | a pair meeting at a point |
| **stone** | a 2 × 2 block |

Heavy 2 px strokes appear as a deliberate accent in `H`, `P` and `T`. What varies is
**which parts a letter uses**, **how they join** (meeting at a point, crossing,
cornering, closing, floating free), and **which side carries the weight** — never size,
and never a one-row offset.

| Letter | Figure | | Letter | Figure | | Letter | Figure |
|---|---|---|---|---|---|---|
| **A** | the gable | | **H** | the arm | | **Q** | the coffer |
| **B** | the thorn | | **I** | the horns | | **R** | the twin bars |
| **C** | the fang, west | | **K** | roof on the post, east | | **S** | the lightning |
| **D** | the fang, east | | **L** | floor under the post | | **T** | the tee |
| **E** | the grate | | **M** | three stones | | **V** | the valley |
| **F** | roof on the post, west | | **N** | the pale | | **X** | the saltire |
| **G** | the cross | | **O** | the diamond | | | |
| | | | **P** | the hammer | | | |

**Every pair difference is sayable aloud** — *the diamond* against *the coffer*, *roof on
the post* against *floor under the post*, *two bars* against *three*, *strokes* against
*stones*. That is the whole test.

All forms live in `x = 4..11` so both stack slots clear the 3 px margin, and no bottom
row fills `x = 6..9` completely, so the tally plinth (§3.2) can never be swallowed.

### 2.1 Twenty-one, because that is the alphabet

The classical Latin alphabet has **21 letters**. J, U and W are mediaeval; Y and Z were
Greek imports Latin had spent centuries absorbing as **I** and **S** already.

```
normalise: uppercase · U→V · W→V · J→I · Y→I · Z→S · strip non-letters
```

**The form table is frozen at v1.** Changing one invalidates every glyph using it.

### 2.2 The rule that finally worked: invert the shared fraction

Every earlier alphabet shared one skeleton across all 21 letters. In the last one — a
full-height stave plus a single mark — **about 12 of 17 pixels were identical in every
letter**, leaving ~4 to carry identity. Measured: **28 of 210 pairs differed by 4 px**,
and the whole alphabet spanned only 4–16 px.

> **A large fraction of each letter's pixels must be doing distinguishing work.**

So: no mandatory shared skeleton. Each letter is its own figure, drawn from shared
*parts* rather than a shared *frame* — which is also what makes the set read as one
script instead of 21 unrelated doodles.

### 2.3 Why strokes, not outlines

Several tables drew **closed outlines** — vessels, boxes, doubled rings — and each
rebuild made them *more* elaborate to keep them apart. Wrong direction.

> **An outline is a picture, and pictures must be intricate to differ. Writing is not
> made of outlines; it is made of strokes.**

Carved scripts settled this long ago under exactly our constraints — hard material, small
size, must be unmistakable. **Elder Futhark**, **Ogham**, **Tifinagh** and **Old Turkic**
all use uprights, bars, diagonals and dots, and nothing curved, because curves are
miserable to cut.

### 2.4 The distinctness bar

**A difference must be nameable.** A width is not nameable: telling a lozenge from a
slightly wider lozenge needs both in front of you, which is exactly what a reader never
gets.

Two forms pass if they differ by at least **12 px** — about one whole carved part — and
do not share a **row-extent footprint**.

**The threshold has been wrong twice, in both directions, and both times it drove the
design rather than judging it:**

| Bar | What it did |
|---|---|
| **10 px** | calibrated for dense outlines; excluded *every* light form, which forced heavy shapes |
| **4 px** | rubber-stamped an alphabet where 28 of 210 pairs differed by a single arm |
| **12 px** | one whole part — the smallest reliably nameable difference at 16 px |

Two further instrument bugs are worth remembering, because each one wasted a review:

- **Row width could not see side.** Width was right while forms were mirrored; once a
  mark could sit on one side, two forms could share every row width and be mirror images.
  The audit records row **extents**.
- **The contact sheet kept mirroring asymmetric forms** after symmetry had been dropped,
  so a whole visual review was worthless. **A metric that agrees with you is worth
  re-deriving.**

### 2.5 What the earlier tables got wrong

| Table | How it failed |
|---|---|
| lozenges at three widths | one shape, three sizes — a relative difference |
| one outline + interior marks | four letters shared an identical silhouette |
| distinct outlines (shells and posts) | passed every metric; every letter an ornate figure |
| stave, 6 join heights, 10 px bar | heights one row apart; bar excluded every light form |
| stave, 3 places, mirrored | needed 2- and 3-mark letters, because side carried nothing |
| stave + one mark, 4 px bar | 12 of 17 px identical in every letter; 28 pairs at 4 px |

---

## 3. The mark, and the tally foot

1. **Normalise** the lemma — uppercase, `U`/`W`→`V`, `J`/`Y`→`I`, `Z`→`S`, strip
   non-letters (§2.1).
2. Take the **first two letters**; each selects a form.
3. Count the whole normalised word and **tally** it into the foot.
4. An explicit `mark` overrides step 2 if two words collide (§8).

### 3.1 The foot is a tally

The bar widens one step per letter up to three. Past three its ends **turn up into a
serif** — the tally's hand mark — and the width starts counting again:

```
t     = clamp(length − 2, 0, 5)
width = 1 + (t mod 3)          // half-widths either side of the axis
serif = t ≥ 3                  // the hand mark
```

which reads back as **`letters = 2 + (width − 1) + 3 × serif`**. Six distinguishable
lengths out of a bar that never exceeds three half-widths — and that ceiling is exactly
what holds the widest foot **3 px clear** of the octagon's bottom chamfer (§5.1).

Six sounds thin and isn't: the forms do the heavy lifting, and the full 49-lemma lexicon
still renders 49 distinct tiles. Lengths of 8 or more all read as "three and a hand"; if
that ever collides in practice, the fix is an explicit `mark`, not a wider foot.

| Lemma | Normalised | Mark | Letters | Width | Serif |
|---|---|---|---|---|---|
| `VITA` | VITA | `VI` | 4 | 3 | — |
| `VIRGA` | VIRGA | `VI` | 5 | 1 | ✔ |
| `VIGILIA` | VIGILIA | `VI` | 7 | 3 | ✔ |
| `CAELUM` | CAELVM | `CA` | 6 | 2 | ✔ |
| `TENEBRAE` | TENEBRAE | `TE` | 8 | 3 | ✔ |

Three `VI` words, three different tiles — the foot does it.

### 3.2 The serif is a plinth, not a tick

The serif is a short step at **x 6–9, one row above the bar** — not a tick at the bar's
ends.

**A tick at the ends is maskable.** A falling arm from the waist lands on x 3–4 in that
row and swallows it whole, which silently made `VIGILIA` render identical to `VITA` — a
tally that encodes nothing when the second letter happens to have a falling mark. Arms
reach x=6 a row *higher* than they reach x=3, so the plinth sits where no mark can ever
be drawn.

The general lesson, worth applying to anything else that hangs off the figure: **a
feature that shares rows with the letter-forms must be placed where their geometry cannot
reach, not merely where it usually doesn't.**

---

## 4. Symmetry — dropped

**Glyphs are no longer mirrored.** The axis is gone, and its removal is what made the
alphabet simple (§2.2): while every mark had to be reflected, side carried no
information, so letters had to differ by *quantity* of marks instead.

What remains symmetric is the **stone**, not the writing — the octagonal tile and the
tally foot are both symmetric about the vertical centre, which is what keeps a line of
glyphs looking like a course of cut blocks rather than a ransom note.

There is no `symmetrise` step in the render path any more. A form draws exactly the
pixels it means, and **which side a mark is on is load-bearing** — mirroring a glyph
turns it into a different letter.

---

## 5. The tile: an octagon, cut in three depths

### 5.1 Silhouette

The tile is the 16 × 16 square with its **four corners chamfered by 2**, so the
silhouette itself reads as a cut stone rather than a sprite. Outside the octagon the
texture is **transparent**.

```
inTile(x,y)  ⟺  min(x, 15−x) + min(y, 15−y) ≥ 2        // 244 of 256 px
```

#### The 3 px margin — the tightest constraint in the system

**No part of a glyph may come within 3 px of the stone's edge**, chamfered corners
included. It is a measured constraint, not an eyeballed one: for every cut pixel, walk
outward in Chebyshev rings to the nearest pixel with `inTile == false`; if any is closer
than 3, fail the build.

**The margin sets the chamfer, not the other way round.** A deep octagon and a 3 px
margin eat the same corners, and the figure needs all twelve usable rows (2–13), so the
envelope the margin leaves is what decides how deep the corners can be cut:

| Chamfer | Columns free at rows 2 and 13 | Verdict |
|---|---|---|
| 1 | 10 (x 3–12) | barely a chamfer — one pixel per corner |
| **2** | **8 (x 4–11)** | **shipping** — deepest cut the figure still clears |
| 3 | 6 (x 5–10) | foot limited to width 2; top form row crowded |
| 4 | 4 (x 6–9) | nothing usable survives |

Going deeper than 2 means redrawing the letter-forms shorter, which is a different and
much larger change.

Three design facts fall straight out of the margin, and none is negotiable afterwards:

- **the chamfer is 2**, per the table above;
- **the tally's width ceiling is 3** (§3.1) — a 4-wide foot lands on the row-13 limit;
- **the two letter-forms share row 7.** Both are already obliged to occupy the centre
  columns at their top and bottom row, so sharing that row is exactly where they were
  going to link anyway. It costs nothing and buys the row the margin needs.

One form was redrawn for it: **`H` (wedge)** is the only form that flares on its *first*
row, where the envelope is tightest, so its arms pull in to x 4–11.

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
| Chamfer | 2 per corner (244 of 256 px opaque) |
| Mirror axis | between columns 7 and 8 |
| Form 1 | rows 2–7 · **Form 2** rows 7–12 — they **share row 7** |
| Form bounds | x 2–13; must include (7,8) at the form's top and bottom row |
| Foot | row 13, centred, `1 + ((len−2) mod 3)` half-widths; serif rises into row 12 |
| Margin | **≥ 3 px** from every cut pixel to the nearest transparent pixel |
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
- **Any lit pixel falling outside the octagon, or within 3 px of its edge.** Assert
  `inTile(x,y)` and `margin(x,y) ≥ 3` for every cut pixel (§5.1). Cheap, and it catches
  the class of bug where a stroke or a wide foot crowds or escapes the silhouette. Check
  each **form** at both slots it can occupy (rows 2 and 7), not just finished glyphs —
  the top slot is the tight one, and a form that only ever renders in the bottom slot
  during testing will hide the violation.

- **Two forms failing the distinctness bar** (§2.4): fewer than 12 differing pixels, or a
  shared row-extent footprint.

**Adding or altering a form requires re-auditing all 23** against each other — a form
narrowed to clear the margin must not collapse onto another — and checking the new form
includes its centre contacts at top and bottom row.

---

## 9. Reference implementation

```js
const S = 16, ALPHABET = "ABCDEFGHIKLMNOPQRSTVX";     // the classical 21, frozen at v1
const normalise = s => s.toUpperCase()
  .replace(/[UW]/g,"V").replace(/[JY]/g,"I").replace(/Z/g,"S").replace(/[^A-Z]/g,"");
const mark = g => (g.mark ?? normalise(g.lemma)).slice(0,2);

const CHAMFER = 2;
const inTile = (x,y) => x>=0 && y>=0 && x<S && y<S &&
                        Math.min(x,S-1-x) + Math.min(y,S-1-y) >= CHAMFER;

// Each form is its own figure (§2), drawn from shared parts. All live in x = 4..11.
const stone = (b,x,y) => { px(b,x,y); px(b,x+1,y); px(b,x,y+1); px(b,x+1,y+1); };

const FORMS = [
  (b,y) => { line(b,4,y+5,7,y); line(b,8,y,11,y+5); },                    // A the gable
  (b,y) => { line(b,4,y,4,y+5); line(b,4,y+1,9,y+3); line(b,9,y+3,4,y+4); }, // B the thorn
  /* … 21 in all, §2 */
];

function render(glyph){                                 // -> 1-bit cut mask
  const b = new Uint8Array(S*S), m = mark(glyph);
  FORMS[ALPHABET.indexOf(m[0])](b, 2);                  // rows 2..7
  FORMS[ALPHABET.indexOf(m[1])](b, 7);                  // rows 7..12 — shares row 7
  const t = clamp(normalise(glyph.lemma).length - 2, 0, 5);
  foot(b, 13, 1 + (t % 3), /* serif */ t >= 3);         // the tally, §3.1 — the
                                                        // serif is a PLINTH, §3.2
  return b;                                             // NOT symmetrised: side means something
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
all 21 forms clearing the distinctness bar (§2.4) — **tightest pair 12 px, median ~21**,
no shared row-extent footprint — and clearing the margin in both slots; **49/49 glyphs distinct**; **49/49 still
distinct with colour stripped**; groove luminance 61.6–62.4 across every hue, authored and hashed alike; all 49 authored
pigments parse and every one names a lemma that exists; nine classes of malformed
`pigment` all fall through to the hash without throwing; every
every glyph still a single connected component (reported, not required); **tightest
margin to the stone's edge 3 px**; all 23 forms distinct from each other and clearing the
margin in both slots; blank tile 244/256 px opaque and zero cuts.

The audit runs against the *live* renderer rather than a transcription of it — it
extracts the algorithm from the specimen page and executes it — so the numbers above
cannot drift away from the art they describe.

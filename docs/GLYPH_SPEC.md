# Glyph Construction Spec

The deterministic rule that turns a Latin lemma into a 32×32 symbol.

> **A glyph is a three-letter abbreviation of its word**, written as three
> **structurally distinct strokes** inside **one universal frame**, with the word's
> **length notched into the frame's edges**.

Every lit pixel means something: it is one of the three strokes, a length notch, or
the frame.

**Every glyph is its own shape.** The frame is identical on all of them, so it cannot
possibly be what you are reading — identity lives entirely in the strokes. That is the
property to protect: a symbol the player picks out of a grid hundreds of times must
never depend on its border.

This is the implementation contract for the renderer (`RUNES.md` §5, D14/D15). Given
the same `lemma` and `determinative.class` it must always produce byte-identical art.

### Why abbreviation beats transcription

**Players read these constantly**: every rune-word guess means scanning the codex for
the glyph they want. That is a *recognition* task, and recognition wants **few, bold,
structurally different** shapes.

Three revisions failed that test, each for a different reason worth recording:

| Attempt | Why it failed |
|---|---|
| Lattice path — letters as grid points joined in sequence | tangled diagonals, strokes leaving the frame |
| Five rungs on a stem, one per letter | every glyph a variation on one comb |
| Two bold marks | `VIRGA`/`VITA`/`VIGILIA` rendered *identically*, leaning on frames |
| Three marks, width × thickness | still one shared skeleton — a wide bar and a slightly wider bar are the same shape |

The common thread: **varying parameters of a shared skeleton does not produce distinct
symbols.** Only varying *structure* does.

The abbreviation is also the honest choice, and three letters is Roman practice at its
most common: `IMP`, `AVG`, `COS`, `PON`. Once a player knows the 23 letterforms they
don't memorise glyphs, they **read** them: `CAE` is `CAELVM`.

**The trade, stated plainly:** a glyph no longer transcribes its lemma — it *names*
it. That is the right way round. Decipherment difficulty belongs in the **rune
words**, not in reading a symbol the player must pick out of a grid hundreds of times.

---

## 1. Anatomy

```
        VIRGA -> "VIR"              CAELVM -> "CAE"
     ┌───────────────┐           ┌───────────────┐
   ──┤    ◇ ◇ ◇      │ ← V     ──┤   ═══════     │ ← C
     │      ○        │ ← I       │   ═══════     │ ← A
   ──┤    \_____/     │ ← R     ──┤     ∧∧∧       │ ← E
     └───────────────┘           └───────────────┘
      ↑ length notches            one universal frame
```

| Layer | Encodes | Drawn as |
|---|---|---|
| **Frame** | nothing — deliberately universal | one shared tablet outline |
| **Stroke 1–3** | the lemma's first three letters | one of 23 **structurally distinct** shapes each |
| **Notches** | the word's **length** | one notch per letter past the third, cut into both sides |

### 1.1 Structure, not parameters

Earlier revisions varied *parameters* — bar width, rung count, tick direction — all
hanging off one vertical stem. Every glyph came out a variation on a ladder, and two
glyphs differing by two pixels of arm length are not two symbols; they are one symbol
with a typo.

**Each letter is now its own kind of mark**: ring, saltire, arch, bowtie, triangle,
twin rings, three posts. `VIR` is diamond-ring-cup; `CAE` is bar-double-bar-chevron.
Those do not merely measure differently, they *look* different — the only kind of
difference a player scanning a codex grid can actually use.

Uniqueness becomes **structural rather than earned**: three independent zones × 23
distinct forms means two glyphs collide only if they share all three letters, and the
length notches separate even those (`VIRGA` 5 vs `VIRIDIS` 7).

### 1.2 The frame is universal — and what that costs

Every glyph wears the same tablet. A border shared by all glyphs *cannot* be what
distinguishes any of them, so identity is forced entirely into the strokes.

**This gives up a real property.** The frame previously encoded the determinative
class — hexagon for materials, circle for celestial, escutcheon for creatures — so an
*undecoded* glyph still announced its category and the grammar was visible in the art
(`RUNES.md` §5). That is gone.

The trade is deliberate: class is information the codex supplies anyway, whereas
*recognising the symbol at all* is something a player does hundreds of times an hour.
If the category cue is wanted back, the cheap version is **a small class pip on the
frame's top edge** — seven positions, subordinate to the strokes, never load-bearing.

---

## 2. The stroke alphabet

23 letters, 23 shapes. Classical Latin has no J, U or W, so lemmas normalise to the
orthography they would have been carved in (`PULVIS` → `PVLVIS`).

| | | | | |
|---|---|---|---|---|
| **A** bar | **B** short bar | **C** double bar | **D** split bar | **E** chevron up |
| **F** chevron down | **G** zigzag | **H** diamond | **I** ring | **K** saltire |
| **L** cross | **M** tau | **N** inverted tau | **O** two posts | **P** three posts |
| **Q** arch | **R** cup | **S** triangle | **T** wedge | **V** bowtie |
| **X** beam | **Y** box | **Z** twin rings | | |

**The form table is frozen at v1.** Changing one invalidates every glyph using that
letter.

Forms are chosen for **mutual contrast**, not beauty in isolation: a ring, a saltire,
an arch and a bowtie share no silhouette, so any three stacked produce a figure with
no near-neighbour. Each is vertically symmetric and confined to its own zone
(x 7–24, y ±3) so any three stack cleanly without touching.

---

## 3. The mark

1. **Normalise** the lemma — uppercase, `U`→`V`, `J`→`I`, strip non-letters.
2. Take the **first three letters**; each selects a stroke form.
3. Count the **whole normalised word**; `length − 3` notches (capped at 5) are cut into
   the frame's sides.
4. An explicit `mark` field overrides step 2 if two words share three letters *and* a
   length.

| Lemma | Normalised | Mark | Letters | Notches |
|---|---|---|---|---|
| `VIRGA` | VIRGA | `VIR` | 5 | 2 |
| `VIRIDIS` | VIRIDIS | `VIR` | 7 | 4 |
| `CAELUM` | CAELVM | `CAE` | 6 | 3 |
| `PULVIS` | PVLVIS | `PVL` | 6 | 3 |
| `BESTIA` | BESTIA | `BES` | 6 | 3 |

`VIRGA` and `VIRIDIS` share a mark and are still distinct — the notches do it.

---

## 4. Symmetry

Every glyph is **bilaterally symmetric about its vertical centre axis**, which sits
between pixel columns 15 and 16.

Symmetry is structural, not something to hand-check: marks are drawn outward from the
stem in both directions, and each frame is passed through a `symmetrise` step that
ORs every column with its mirror. **A frame therefore cannot be drawn asymmetrically
by accident**, and vertex coordinates need not be mirror-exact.

Left–right rather than top–bottom because mirror symmetry about a vertical stem is
what reads as *writing* — runes, alchemical sigils, heraldic charges, maker's marks.
Top–bottom symmetry reads as a playing card.

Plinth and open-base frames deliberately break *horizontal* symmetry: a structure
sits on something, a quality rests on a baseline. That is the cue that they are not
enclosures.

---

## 5. Pixel geometry (32 × 32)

| Element | Value |
|---|---|
| Canvas | 32 × 32, no anti-aliasing anywhere |
| Mirror axis | between columns 15 and 16 |
| Frame | clipped tablet, vertices (9,2) (22,2) (28,8) (28,23) (22,29) (9,29) (3,23) (3,8) |
| Stroke zones | rows **9, 16, 23**; each stroke confined to x 7–24, y ±3 |
| Stroke weight | **2 px** on every limb |
| Notch rows | 10, 13, 16, 19, 22 — outside the frame at x 1–2 and 29–30 |

The frame keeps **straight vertical sides across the whole stroke band** (rows 8–23),
so no stroke ever meets a tapering edge. Zones are 7 rows apart and strokes reach ±3,
which guarantees adjacent strokes never touch — the reason any three forms stack
cleanly without a legibility audit per combination.

---

## 6. The frame

**One frame, shared by every glyph.** See §1.2 for what that trades away and how to
restore a class cue if wanted.

The frame must stay vertically symmetric and keep straight sides across rows 8–23.
Because it is universal there is no per-class audit any more — a single shape is
checked once, and adding decoration to it affects every glyph equally, which makes it
far safer to change than the seven-frame scheme it replaces.

---

## 7. Accessibility: shape carries everything

**Colour encodes nothing.** Strip every colour from the game and zero information is
lost. Colour may only ever be redundant reinforcement.

| The player must tell… | Carried by | Never by |
|---|---|---|
| which glyph this is | the three stroke shapes | hue |
| what class it belongs to | frame silhouette | hue |
| whether they know it yet | how much is drawn (§7.1) | hue |
| where a clause begins | doubled ring | hue |

### 7.1 Knowledge tier is drawn, not tinted

| Tier | Rendered | Reads as |
|---|---|---|
| **0 · Unknown** | frame only | "a symbol, meaning nothing" |
| **1 · Sighted** | frame + **length notches**, no strokes | "I know how long the word is, not what it says" |
| **2 · Learned** | frame + notches + **all three strokes** | complete, legible |

Tier 1 reveals the word's *length* and nothing else — you can count its letters but not
read one. A precise visual metaphor for partial decipherment, identical for every
colour vision.

---

## 8. Validation

The renderer and datapack loader must reject:

- A `lemma` (or `mark`) whose first three normalised characters aren't all A–Z, or
  that contains `W`.
- A `mark` override that isn't exactly three letters.
- **Two glyphs with the same mark *and* the same word length.** Uniqueness is global
  and needs no per-class exception, because the frame is universal. `VIRGA` and
  `VIRIDIS` share `VIR` and are separated by their notch counts; two 5-letter `VIR`
  words would not be, and need an explicit `mark`.
- A glyph whose `category` is `element` declaring a `determinative` (a grammar rule,
  `RUNES.md` §5.2 — unrelated to art now that frames are universal).

**Adding or altering a stroke form requires re-auditing all 23** against each other:
render each alone and assert 23 distinct figures. Because the frame is shared and the
zones are isolated, that single audit is sufficient — there is no per-frame or
per-position matrix to re-check, which is a real simplification over the previous
scheme.

---

## 9. Reference implementation

```js
const ALPHABET = "ABCDEFGHIKLMNOPQRSTVXYZ";      // frozen at v1
const ROWS  = [9,16,23];                          // stroke zones
const NOTCH = [10,13,16,19,22];                   // length notches

const normalise = s => s.toUpperCase().replace(/U/g,"V").replace(/J/g,"I").replace(/[^A-Z]/g,"");
const mark = g => (g.mark ?? normalise(g.lemma)).slice(0,3);

// 23 stroke forms, each its own shape — bar, short bar, double bar, split bar,
// chevron up/down, zigzag, diamond, ring, saltire, cross, tau, inverted tau,
// two posts, three posts, arch, cup, triangle, wedge, bowtie, beam, box, twin rings.
const FORMS = [ /* one drawing fn per letter, confined to x 7..24, y ±3 */ ];

function render(glyph, tier){
  const b = new Uint8Array(32*32);
  drawFrame(b);                                   // the one universal tablet
  if (tier !== "unknown"){
    const n = Math.min(5, Math.max(0, normalise(glyph.lemma).length - 3));
    for (let i=0;i<n;i++) notch(b, NOTCH[i]);      // length, countable
    if (tier === "learned")
      [...mark(glyph)].forEach((c,i) => FORMS[ALPHABET.indexOf(c)](b, ROWS[i]));
    else
      ROWS.forEach(y => stub(b,y));               // sighted: zones marked, not drawn
  }
  symmetrise(b);                                  // cannot come out asymmetric
  return b;
}
```

**Audited:** all 23 stroke forms distinct from one another; every glyph in the worked
lexicon unique; `VIRGA`/`VIRIDIS` separated by notches alone; every output vertically
symmetric.

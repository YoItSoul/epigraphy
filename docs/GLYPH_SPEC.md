# Glyph Construction Spec

The deterministic rule that turns a Latin lemma into a **16 × 16** symbol — Minecraft's
own item resolution.

> **A glyph is one continuous figure**: two stacked letter-forms that link at the
> centre, standing on a foot bar whose width counts the word's letters. It is carved
> into a stone tile lit from the top-left.

**No frame, no floating marks, no disconnected pieces.** The whole symbol is a single
unbroken shape — something you could cut with one chisel.

This is the implementation contract for the renderer (`RUNES.md` §5, D14/D15). Given
the same `lemma` it must always produce byte-identical art.

### Why 16 × 16 forced two letters

32 × 32 held three six-row letter zones comfortably. 16 × 16 has **a quarter the
pixels** and holds two. Rather than shrink the forms into illegibility, the third
letter was dropped and **word length moved into the foot bar**.

Uniqueness survives: `VITA` (4 letters), `VIRGA` (5) and `VIGILIA` (7) all abbreviate
to `VI` and remain three different tiles because their feet differ. Two glyphs now
collide only if they share their first two letters **and** their length — rarer than
the three-letter clash it replaces, and still fixable with an explicit `mark`.

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
     ┌──────────────┐       ┌──────────────┐
     │    ╱▔▔╲      │ ← V   │              │
     │    ╲__╱      │       │              │   bare stone —
     │    ╱▔▔╲      │ ← I   │              │   unknown glyph,
     │    ╲__╱      │       │              │   empty codex slot,
     │  ▁▁▁▁▁▁▁▁    │ ←foot │              │   uninscribed tablet
     └──────────────┘       └──────────────┘
```

| Layer | Encodes | Drawn as |
|---|---|---|
| **Form 1** (rows 2–7) | the lemma's **first** letter | one of 23 closed shapes |
| **Form 2** (rows 8–13) | the lemma's **second** letter | one of 23 closed shapes |
| **Foot** (row 14) | the word's **length** | a bar `1 + min(5, len−2)` half-widths wide |
| **Stone** | nothing — material only | gradient + grain, see §7 |

### 1.1 The blank glyph

A tile with **no cuts at all** — bare stone. It serves three jobs with one asset:
an **unknown glyph** (Tier 0), an **empty codex slot**, and an **uninscribed tablet**.

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

## 3. The mark

1. **Normalise** the lemma — uppercase, `U`→`V`, `J`→`I`, strip non-letters.
2. Take the **first two letters**; each selects a form.
3. Count the **whole normalised word**; the foot is `1 + min(5, length − 2)` wide.
4. An explicit `mark` overrides step 2 if two words share two letters *and* a length.

| Lemma | Normalised | Mark | Letters | Foot |
|---|---|---|---|---|
| `VITA` | VITA | `VI` | 4 | 3 |
| `VIRGA` | VIRGA | `VI` | 5 | 4 |
| `VIGILIA` | VIGILIA | `VI` | 7 | 6 |
| `CAELUM` | CAELVM | `CA` | 6 | 5 |
| `BESTIA` | BESTIA | `BE` | 6 | 5 |

Three `VI` words, three different tiles — the foot does it.

---

## 4. Symmetry

Every glyph is **bilaterally symmetric about its vertical centre axis**, which sits
between pixel columns 7 and 8.

Symmetry is structural, not something to hand-check: the finished bitmap is passed
through a `symmetrise` step that ORs every column with its mirror. **A glyph therefore
cannot come out asymmetric by accident**, and neither stroke nor frame coordinates
need to be mirror-exact.

Left–right rather than top–bottom because mirror symmetry about a vertical axis is
what reads as *writing* — runes, alchemical sigils, heraldic charges, maker's marks.
Top–bottom symmetry reads as a playing card.

---

## 5. Pixel geometry (16 × 16)

| Element | Value |
|---|---|
| Canvas | **16 × 16**, no anti-aliasing |
| Mirror axis | between columns 7 and 8 |
| Form 1 | rows 2–7 · **Form 2** rows 8–13 |
| Form bounds | x 2–13; must include (7,8) at the form's top and bottom row |
| Foot | row 14, centred, `1 + min(5, len−2)` half-widths |
| Mean ink | ~52 of 256 pixels |

---

## 6. Stone

The tile is stone lit from the **top-left**, grading to shadow at the bottom-right.

```
value(x,y) = lerp(hi, lo, (x+y) / 2(S−1)) + grain(x,y)
grain(x,y) = deterministic hash of (x,y), range ±8
cut  pixel = value × 0.34          // incised groove
lip  pixel = value × 1.18          // lit edge, down-right of any cut
```

The grain is a **hash, not randomness** — the renderer must stay byte-identical.

**The gradient is material, not information.** It carries no meaning: flatten every
tile to one grey and nothing is lost. The rule that colour never encodes anything
(§7) is unaffected.

---

## 7. Accessibility: shape carries everything

**Colour encodes nothing.** Strip every colour from the game and zero information is
lost. Colour may only ever be redundant reinforcement.

| The player must tell… | Carried by | Never by |
|---|---|---|
| which glyph this is | the two letter-forms + foot width | hue |
| what class it belongs to | frame silhouette | hue |
| whether they know it yet | how much is drawn (§7.1) | hue |
| where a clause begins | doubled ring | hue |

### 7.1 Knowledge tier is drawn, not tinted

| Tier | Rendered | Reads as |
|---|---|---|
| **0 · Unknown** | **the blank tile** — bare stone | "a stone, meaning nothing" |
| **1 · Sighted** | *open — see below* | "seen but unread" |
| **2 · Learned** | the full carved figure | complete, legible |

**Tier 1 has no representation yet.** With the frame gone there is nothing left to draw
that isn't the answer. Three options, none chosen: carve the **foot only** (reveals
length, hides the letters — closest to the old behaviour); render the tile as
**rougher, unfinished stone**; or drop the middle tier's visual entirely and let
Sighted look like Unknown until learned.

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
- **Any lit pixel falling outside the frame outline.** Scan each row for the frame's
  leftmost and rightmost pixel; assert nothing lies beyond. Cheap, and it catches the
  class of bug where a stroke or notch escapes the border.

**Adding or altering a form requires re-auditing all 23** against each other, and
checking the new form includes its centre contacts at top and bottom row. Because
there is no frame and the zones are isolated, that single audit is sufficient.

---

## 9. Reference implementation

```js
const S = 16, ALPHABET = "ABCDEFGHIKLMNOPQRSTVXYZ";   // frozen at v1
const normalise = s => s.toUpperCase().replace(/U/g,"V").replace(/J/g,"I").replace(/[^A-Z]/g,"");
const mark = g => (g.mark ?? normalise(g.lemma)).slice(0,2);

// 23 closed forms. Each spans 6 rows and MUST include the centre columns at its
// top and bottom row — that contract is what makes the figure continuous.
const FORMS = [ /* shaft, lozenge, box, flask, kite, barrel, … */ ];

function render(glyph){
  const b = new Uint8Array(S*S);
  const m = mark(glyph);
  FORMS[ALPHABET.indexOf(m[0])](b, 2);                 // rows 2..7
  FORMS[ALPHABET.indexOf(m[1])](b, 8);                 // rows 8..13
  const n = Math.min(5, Math.max(0, normalise(glyph.lemma).length - 2));
  foot(b, 14, 1 + n);                                  // length, connected to form 2
  symmetrise(b);                                       // cannot come out asymmetric
  return b;                                            // 1-bit cut mask
}

function paint(bits, palette){                          // cut mask -> stone tile
  for (let y=0;y<S;y++) for (let x=0;x<S;x++){
    const t = (x+y)/(2*(S-1));                          // lit top-left -> dark bottom-right
    let v = lerp(palette.hi, palette.lo, t) + grain(x,y);
    if (bits[y*S+x])                     v = v * 0.34;  // incised
    else if (bits[(y-1)*S+(x-1)])        v = v * 1.18;  // lit lip
    put(x,y,v);
  }
}
```

`grain` is a deterministic hash of `(x,y)`, never `Math.random` — the renderer must
produce byte-identical output.

**Audited:** all 23 forms distinct; every glyph in the worked lexicon unique;
`VITA`/`VIRGA`/`VIGILIA` separated by foot width alone; every output vertically
symmetric; and **every glyph a single connected component**.

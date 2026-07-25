# Glyph Construction Spec

The deterministic rule that turns a Latin lemma into a 32×32 symbol.

> **A glyph is a three-letter abbreviation of its word**, drawn as three bold marks on
> a stem inside a frame that names its class. That is the whole rule.

Every lit pixel means something: it is stem, one of the three marks, or frame.

**Every glyph's interior is unique on its own.** Strip the frames off the entire
lexicon and all 49 glyphs remain tellable apart — the frame adds *class*, never
identity. That is the property to protect: a symbol the player must pick out of a
grid hundreds of times must never depend on its border.

This is the implementation contract for the renderer (`RUNES.md` §5, D14/D15). Given
the same `lemma` and `determinative.class` it must always produce byte-identical art.

### Why abbreviation beats transcription

Earlier revisions transcribed the whole lemma — five rungs, one per letter. That
optimised the wrong thing. **Players read these constantly**: every rune-word guess
means scanning the codex for the glyph they want. That is a *recognition* task, and
recognition wants **few, bold, specific** shapes, not a dense record of data nobody
parses at a glance. Five faint rungs made every glyph a variation on one comb.

**Two letters proved too few.** `VIRGA`, `VITA` and `VIGILIA` all abbreviate to `VI`
and rendered *identical interiors*, distinguishable only by frame — exactly the
failure this system exists to avoid.

**Three is the number that works.** `VIR` / `VIT` / `VIG` are three different figures,
and across the whole 49-glyph worked lexicon every interior is unique. Three marks is
still few enough to draw each **2 px thick** — weight is what makes a symbol readable
across a room.

The abbreviation is also the honest choice, and three letters is Roman practice at its
most common: `IMP`, `AVG`, `COS`, `PON`. Once a player knows the 23 letterforms they
don't memorise glyphs, they **read** them: `CAE` is `CAELVM`.

**The trade, stated plainly:** a glyph no longer transcribes its lemma — it *names*
it. That is the right way round. Decipherment difficulty belongs in the **rune
words**, not in reading a symbol the player must pick out of a grid hundreds of times.

---

## 1. Anatomy

```
        CAELVM  ->  "CAE"             FLAMMANS  ->  "FLA"
     ┌─────────────────┐           ┌─────────────────┐
     │    ════╬════    │  ← C      │       ╱╬╲       │  ← F
     │        ╬        │           │        ╬        │
     │      ══╬══      │  ← A      │       ╱╬╲       │  ← L
     │        ╬        │           │        ╬        │
     │     ══╬══╬══    │  ← E      │      ══╬══      │  ← A
     └─────────────────┘           └─────────────────┘
        circle = celestial            open base = element
```

| Layer | Encodes | Drawn as |
|---|---|---|
| **Frame** | the class | one of seven silhouettes |
| **Stem** | nothing — it is the constant spine | 2 px vertical, rows 5–27 |
| **Mark 1** | the lemma's **first** letter | a bold arm, row 9 |
| **Mark 2** | the lemma's **second** letter | a bold arm, row 16 |
| **Mark 3** | the lemma's **third** letter | a bold arm, row 23 |

A letter is identified by its mark's **width** (5 steps) and **shape** (5 kinds).
5 × 5 = **25 slots for the 23 letters** of the classical Latin alphabet.

Interiors are unique without their frames; the frame is a bonus channel carrying class.

### 1.1 Identity lives in the marks, not the frame

Two glyphs collide only if they share their **first three letters** — globally, not
per class. The validator rejects that (§8) and the author supplies an explicit
three-letter `mark`:

```jsonc
{ "lemma": "VIRIDIS", "mark": "VRD" }   // VIR was taken by VIRGA
```

Because identity is carried entirely by the interior, the frame is free to do its own
job — announcing the class — without being load-bearing for recognition.

---

## 2. The alphabet

Classical Latin has exactly **23 letters** — no J, U or W. Lemmas normalise to the
orthography they would have been carved in:

| Modern | Classical | Example |
|---|---|---|
| `U` → `V` | Romans carved V | `PULVIS` → `PVLVIS` |
| `J` → `I` | Romans carved I | `IANUA` → `IANVA` |
| `W` | not a Latin letter | rejected at validation |

**The letter → (width, shape) map is frozen at v1.** Changing it invalidates every
glyph ever made. Note that only a lemma's *first three* letters ever reach the art, so
normalisation matters most at the front of a word.

```
index = ALPHABET.indexOf(letter)        // "ABCDEFGHIKLMNOPQRSTVXYZ"
width  = index % 5 + 1                  // 1..5
shape  = floor(index / 5)               // 0..4
```

| shape ↓ / width → | 1 | 2 | 3 | 4 | 5 |
|---|---|---|---|---|---|
| **0** bar `═══` | A | B | C | D | E |
| **1** chevron up `∧` | F | G | H | I | K |
| **2** chevron down `∨` | L | M | N | O | P |
| **3** double bar `≡` | Q | R | S | T | V |
| **4** broken bar `═ ═` | X | Y | Z | — | — |

---

## 3. The mark

1. **Normalise** the lemma (§2) — uppercase, `U`→`V`, `J`→`I`, strip non-letters.
2. Take the **first three letters**. That is the glyph.
3. An explicit `mark` field overrides step 2 when another glyph has taken the triple.

No truncation rules, no vowel-dropping, no special case for repeated letters — a word
with a doubled letter simply draws the same mark twice, which is legible and correct.

### 3.1 Worked marks

| Lemma | Normalised | Mark | Class | Reads as |
|---|---|---|---|---|
| `CAELUM` | CAELVM | `CAE` | celestial | circle |
| `FLAMMANS` | FLAMMANS | `FLA` | element | open base |
| `PULVIS` | PVLVIS | `PVL` | material | hexagon |
| `UNDA` | VNDA | `VND` | fluid | basin |
| `TERRA` | TERRA | `TER` | structure | plinth |
| `BESTIA` | BESTIA | `BES` | creature | escutcheon |
| `OPUS` | OPVS | `OPV` | formula | doubled ring |

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
| Stem | columns 15–16, rows 5–27 |
| Mark rows | 9, 16, 23 |
| Mark thickness | **2 px** — bold enough to read at a glance |
| Mark half-extent by width | **3, 5, 7, 9, 11** — arms start at column 13, 11, 9, 7, 5 |
| Chevron rise | −1 to +2 rows, apex on the stem |
| Double bar | two bars at row −2 and row +1 |
| Broken-bar gap | one pixel, mid-arm, both sides, both rows |

Two constraints are load-bearing, each found by an audit that failed before it was
added:

- **All three mark rows sit in the frame's straight band.** Every frame keeps vertical
  sides across rows 8–24 and tapers only above and below. Where a frame tapered into
  the band, wide marks clamped to the same column and distinct letters collapsed.
- **Widths are two pixels apart, and arms stop one pixel inside the frame.** Arms can
  then never cross the frame, and adjacent widths never coincide once clamped.

---

## 6. Frames

Seven silhouettes, each vertically symmetric, each with straight sides across rows
8–24.

| Frame | Class | Heads | Shape |
|---|---|---|---|
| Hexagon | Material | `METALLUM` `LAPIS` `VIRGA` `PULVIS` `GLADIUS` | pointed top and bottom, vertical sides |
| Circle | Celestial | `CAELUM` `LUNA` `NOX` | r = 14 |
| Plinth | Structure / Place | `ALTARE` `INFERNUS` `TERRA` | box on a wider base |
| Basin | Fluid | `UNDA` | flat top, tapered bottom |
| Open base | Element | `FLAMMANS` `TENEBRAE` `CHAOS` `VITA` `PLENUS` `FUNDUS` | a baseline, **not** an enclosure |
| Escutcheon | Creature | `BESTIA` `DRACO` `CUSTOS` | flat top, straight sides, coming to a point |
| Doubled ring | Formula | `OPUS` `MERSIO` `TACTUS` `VIGILIA` `FIAT` | r = 14, plus an inner arc |

**The doubled ring's inner arc is clipped out of rows 8–24.** A full inner ring at
r = 11 lands exactly where width-4 arms end and merges with them, collapsing C/D,
H/I, N/O and S/T. Clipping it to the top and bottom keeps the doubled-rim silhouette
without intruding on the stave.

**The element frame is open on purpose.** Quality glyphs modify and can never head a
word, so their frame never closes. The shape *is* the rule, and it is enforced in
data: a glyph with `category: element` may not declare a `determinative`.

---

## 7. Accessibility: shape carries everything

**Colour encodes nothing.** Strip every colour from the game and zero information is
lost. Colour may only ever be redundant reinforcement.

| The player must tell… | Carried by | Never by |
|---|---|---|
| which glyph this is | the three marks | hue |
| what class it belongs to | frame silhouette | hue |
| whether they know it yet | how much is drawn (§7.1) | hue |
| where a clause begins | doubled ring | hue |

### 7.1 Knowledge tier is drawn, not tinted

| Tier | Rendered | Reads as |
|---|---|---|
| **0 · Unknown** | frame only | "a symbol, meaning nothing" |
| **1 · Sighted** | frame + stem + **mark positions**, arms unextended | "I've seen its shape but can't read it" |
| **2 · Learned** | frame + stem + **full marks** | complete, legible |

Tier 1 shows that a glyph *has* three marks without revealing any of them — a precise
visual metaphor for partial decipherment, identical for every colour vision.

---

## 8. Validation

The renderer and datapack loader must reject:

- A `lemma` (or `mark`) whose first three normalised characters aren't all A–Z, or
  that contains `W`.
- A `mark` override that isn't exactly three letters.
- **Any two glyphs with the same mark, in any class.** Uniqueness is global, not
  per-class, precisely so that no glyph depends on its frame to be recognised. Fix
  with an explicit `mark`.
- A glyph whose `category` is `element` declaring a `determinative` (§6).
- A new frame that is not vertically symmetric, or that intrudes into rows 8–24.

**Adding a frame requires re-running the letter-distinctness audit**: render all 23
letters in **all three** mark positions in the new frame and assert 23 distinct figures
each time. Every collision found while developing this spec came from a frame intruding on
the marks, never from the letter map.

---

## 9. Reference implementation

```js
const ALPHABET = "ABCDEFGHIKLMNOPQRSTVXYZ";   // frozen at v1 — never reorder
const EXT  = [0,3,5,7,9,11];                  // half-extent by width 1..5
const ROWS = [9,16,23];                       // three marks

const normalise = s => s.toUpperCase().replace(/U/g,"V").replace(/J/g,"I").replace(/[^A-Z]/g,"");
const mark = glyph => glyph.mark ?? normalise(glyph.lemma).slice(0,3);

const form = c => { const i = ALPHABET.indexOf(c); return { w: i % 5 + 1, t: Math.floor(i / 5) }; };
//  t: 0 bar · 1 chevron up · 2 chevron down · 3 double bar · 4 broken bar

function symmetrise(b){                        // frames cannot be asymmetric
  for (let y = 0; y < 32; y++) for (let x = 0; x < 16; x++)
    if (b[y*32+x] || b[y*32+31-x]) { b[y*32+x] = 1; b[y*32+31-x] = 1; }
}
const boundL = (b,y) => { for (let x = 1; x < 15; x++) if (b[y*32+x]) return x; return 3; };
const hbar = (b,x0,x1,y) => { hline(b,x0,x1,y); hline(b,x0,x1,y+1); };   // 2 px = bold

function render(glyph, tier){
  const b = new Uint8Array(32*32);
  drawFrame(b, glyph.determinative?.class ?? glyph.category);
  symmetrise(b);
  if (tier === "unknown") return b;

  vline(b,15,5,27); vline(b,16,5,27);                        // stem
  if (tier === "sighted"){ ROWS.forEach(y => { set(b,14,y); set(b,17,y); }); return b; }

  [...mark(glyph)].forEach((c,i) => {
    const {w,t} = form(c), y = ROWS[i];
    const xl = Math.max(16 - EXT[w], boundL(b,y) + 1);        // never crosses the frame
    const xr = 31 - xl;
    if (t === 0) hbar(b,xl,xr,y);                                           // bar
    else if (t === 1){ dband(b,xl,y+2,15,y-1); dband(b,xr,y+2,16,y-1); }    // chevron up
    else if (t === 2){ dband(b,xl,y-1,15,y+2); dband(b,xr,y-1,16,y+2); }    // chevron down
    else if (t === 3){ hbar(b,xl,xr,y-2); hbar(b,xl,xr,y+1); }              // double bar
    else { hbar(b,xl,xr,y);                                                 // broken bar
           const g = Math.round((xl+14)/2);
           for (const d of [0,1]){ b[(y+d)*32+g] = 0; b[(y+d)*32+31-g] = 0; } }
  });
  return b;
}
```

`dband` draws a 2-px-thick diagonal (two offset Bresenham runs).

Deterministic, dependency-free, and cheap enough to generate the whole lexicon into an
atlas at resource-reload time.

**Audited:** every one of the 23 letters renders distinctly in **all three** mark
positions in **all seven** frames; all 49 lexicon glyphs have **unique interiors** with
frames ignored entirely; every output is vertically symmetric.

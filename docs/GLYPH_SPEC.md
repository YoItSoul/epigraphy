# Glyph Construction Spec

The deterministic rule that turns a Latin lemma into a 32×32 symbol. Every lit pixel
means something: it is part of the **stem**, part of a **rung** naming one letter, or
part of the **frame** declaring the word's class. Nothing is decorative.

This is the implementation contract for the renderer (`RUNES.md` §5, D14/D15). Given
the same `lemma` and `determinative.class` it must always produce byte-identical art.

> **Supersedes the lattice-path construction.** The first version mapped letters to
> scattered grid points and joined them in sequence. Consecutive letters could be
> anywhere, so it produced long diagonals, self-crossings, wildly uneven density, and
> strokes that ran outside the frame. This replaces it with a stem-and-rung form that
> is clean by construction.

---

## 1. Anatomy

```
        ┌─────────────────────────┐
        │            ║            │   ║  stem — full height, meets the frame
        │      ══════╬══════      │   ═  rung — one per letter, read downward
        │        ╌╌╌╌╬╌╌╌╌        │
        │    ════════╬════════    │   frame — the determinative class
        │            ║            │
        └─────────────────────────┘
```

| Layer | Encodes | Drawn as |
|---|---|---|
| **Frame** | the determinative class | one of six silhouettes |
| **Stem** | nothing — it is the constant | 2 px vertical, touching frame top and bottom |
| **Rungs** | the lemma, **one rung per letter**, top to bottom | horizontal arms + an end-form |

A letter is identified by its rung's **width** (5 steps) and **end-form** (5 kinds).
5 × 5 = **25 slots for the 23 letters** of the classical Latin alphabet.

---

## 2. The alphabet

Classical Latin has exactly **23 letters** — no J, U or W. Lemmas normalise to the
orthography they would have been carved in:

| Modern | Classical | Example |
|---|---|---|
| `U` → `V` | Romans carved V | `PULVIS` → `PVLVIS` |
| `J` → `I` | Romans carved I | `IANUA` → `IANVA` |
| `W` | not a Latin letter | rejected at validation |

**The letter → (width, end-form) map is frozen at v1.** Changing it invalidates every
glyph ever made.

```
index = ALPHABET.indexOf(letter)        // "ABCDEFGHIKLMNOPQRSTVXYZ"
width  = index % 5 + 1                  // 1..5
form   = floor(index / 5)               // 0..4
```

| form ↓ / width → | 1 | 2 | 3 | 4 | 5 |
|---|---|---|---|---|---|
| **0** plain | A | B | C | D | E |
| **1** up-tick | F | G | H | I | K |
| **2** down-tick | L | M | N | O | P |
| **3** cross | Q | R | S | T | V |
| **4** broken | X | Y | Z | — | — |

---

## 3. The stave

1. **Normalise** the lemma (§2), uppercase, strip non-letters.
2. **Truncate** to at most **5 letters** (§3.1).
3. **Draw the stem**, then one **rung per letter** down the five rows.

### 3.1 Truncation: the abjad rule

Lemmas over five letters drop **vowels after the first letter**, keeping the initial —
the strongest recognition cue. `FLAMMANS` → `F` + `LMMNS` = `FLMMN`. If still over
five, truncate from the end.

Semitic abjads write consonants only, and Roman inscriptions abbreviate relentlessly
(`V.S.L.M.`, `COS` for *consul*). A shortened stave is what a stonecutter would have
cut.

> **`V` is treated as a vowel** for dropping, since it absorbs classical `U`. So
> `CAELUM` → `CAELVM` → `CLM`. Authors who dislike a particular result override it
> with an explicit `stave` field:
> ```jsonc
> { "lemma": "FLAMMANS", "stave": "FLMS" }
> ```

### 3.2 Worked staves

| Lemma | Normalised | Stave | Rungs |
|---|---|---|---|
| `VIRGA` | VIRGA | `VIRGA` | 5 |
| `UNDA` | VNDA | `VNDA` | 4 |
| `GLADIUS` | GLADIVS | `GLDS` | 4 |
| `FLAMMANS` | FLAMMANS | `FLMMN` | 5 |
| `CAELUM` | CAELVM | `CLM` | 3 |
| `TERRA` | TERRA | `TERRA` | 5 |

Repeated letters need **no special case** — they are simply identical rungs at
different heights, which is legible and correct.

---

## 4. Symmetry

Every glyph is **bilaterally symmetric about its vertical centre axis**, which sits
between pixel columns 15 and 16.

Symmetry is structural, not something to hand-check: rungs are drawn outward from the
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
| Stem | columns 15–16, rows 3–28 |
| Rung rows | **8, 12, 16, 20, 24** |
| Rung half-extent by width | **3, 5, 7, 9, 11** — arms start at column 13, 11, 9, 7, 5 |
| End-form ticks | columns **14 and 17**, at row ± 1 |
| Broken-arm gap | one pixel, mid-arm, both sides |

Three constraints in that table are load-bearing, each found by an audit that failed
before it was added:

- **Rung rows sit in the frame's straight band (8–24).** Every frame keeps vertical
  sides across those rows and tapers only above and below. Where a frame tapered into
  the band, wide rungs clamped to the same column and distinct letters collapsed.
- **Widths are two pixels apart, and arms stop one pixel inside the frame.** Arms can
  then never cross the frame, and adjacent widths never coincide.
- **End-form ticks sit beside the stem, not at the arm ends.** At the ends they landed
  on frame pixels — on curved frames especially — and vanished, collapsing E/K/P/V
  onto one figure.

---

## 6. Frames

Six silhouettes, each vertically symmetric, each with straight sides across rows 8–24.

| Frame | Class | Heads | Shape |
|---|---|---|---|
| Hexagon | Material | `METALLUM` `LAPIS` `VIRGA` `PULVIS` `GLADIUS` | pointed top and bottom, vertical sides |
| Circle | Celestial | `CAELUM` `LUNA` `NOX` | r = 14 |
| Plinth | Structure / Place | `ALTARE` `INFERNUS` `TERRA` | box on a wider base |
| Basin | Fluid | `UNDA` | flat top, tapered bottom |
| Open base | Element | `FLAMMANS` `TENEBRAE` `CHAOS` `VITA` `PLENUS` `FUNDUS` | a baseline, **not** an enclosure |
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
| which glyph this is | the rung pattern | hue |
| what class it belongs to | frame silhouette | hue |
| whether they know it yet | how much is drawn (§7.1) | hue |
| where a clause begins | doubled ring | hue |

### 7.1 Knowledge tier is drawn, not tinted

| Tier | Rendered | Reads as |
|---|---|---|
| **0 · Unknown** | frame only | "a symbol, meaning nothing" |
| **1 · Sighted** | frame + stem + **rung positions**, arms unextended | "I can see it has five letters, not which" |
| **2 · Learned** | frame + stem + **full rungs** | complete, legible |

Tier 1 shows *how many letters* the word has without revealing any of them — a
precise visual metaphor for partial decipherment, identical for every colour vision.

---

## 8. Validation

The renderer and datapack loader must reject:

- A `lemma` containing `W`, or any character outside A–Z after normalisation.
- A `lemma` or `stave` normalising to fewer than 2 letters, or a `stave` over 5.
- Two glyphs **in the same class** whose finished figures are pixel-identical.
- A glyph whose `category` is `element` declaring a `determinative` (§6).
- A new frame that is not vertically symmetric, or that intrudes into rows 8–24.

The last rule is the one a contributor will trip. **Adding a frame requires re-running
the letter-distinctness audit**: render all 23 letters in the new frame and assert 23
distinct figures. Every collision found while developing this spec came from a frame
intruding on the stave, never from the letter map itself.

---

## 9. Reference implementation

```js
const ALPHABET = "ABCDEFGHIKLMNOPQRSTVXYZ";   // frozen at v1 — never reorder
const VOWELS   = "AEIOVY";
const ROWS = [8,12,16,20,24];
const EXT  = [0,3,5,7,9,11];                  // half-extent by width 1..5

const normalise = s => s.toUpperCase().replace(/U/g,"V").replace(/J/g,"I").replace(/[^A-Z]/g,"");

function stave(glyph){
  let s = glyph.stave ?? normalise(glyph.lemma);
  if (s.length > 5) s = (s[0] + [...s.slice(1)].filter(c => !VOWELS.includes(c)).join("")).slice(0,5);
  return s;
}

const form = c => { const i = ALPHABET.indexOf(c); return { w: i % 5 + 1, t: Math.floor(i / 5) }; };

function symmetrise(b){                        // frames cannot be asymmetric
  for (let y = 0; y < 32; y++) for (let x = 0; x < 16; x++)
    if (b[y*32+x] || b[y*32+31-x]) { b[y*32+x] = 1; b[y*32+31-x] = 1; }
}

const boundL = (b,y) => { for (let x = 1; x < 15; x++) if (b[y*32+x]) return x; return 3; };

function render(glyph, tier){
  const b = new Uint8Array(32*32);
  drawFrame(b, glyph.determinative?.class ?? glyph.category);
  symmetrise(b);
  if (tier === "unknown") return b;

  vline(b,15,3,28); vline(b,16,3,28);                        // stem

  [...stave(glyph)].forEach((c,i) => {
    const {w,t} = form(c), y = ROWS[i];
    if (tier === "sighted"){ set(b,14,y); set(b,17,y); return; }   // positions only
    const xl = Math.max(16 - EXT[w], boundL(b,y) + 1);
    const xr = 31 - xl;
    hline(b, xl, xr, y);
    if (t === 4){ const g = Math.round((xl+14)/2); b[y*32+g] = 0; b[y*32+31-g] = 0; }
    if (t === 1 || t === 3){ set(b,14,y-1); set(b,17,y-1); }
    if (t === 2 || t === 3){ set(b,14,y+1); set(b,17,y+1); }
  });
  return b;
}
```

Deterministic, dependency-free, and cheap enough to generate the whole lexicon into an
atlas at resource-reload time.

**Audited:** all 23 letters render as distinct figures in all six frames, all sample
words render distinct, and every output is vertically symmetric.

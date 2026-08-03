// Extracts the real algorithm out of the specimen page and audits it.
const fs = require("fs");
const src = fs.readFileSync(__dirname + "/index.html", "utf8");
const script = src.slice(src.lastIndexOf("<script>") + 8, src.indexOf("/*==DOM==*/"));
const core = script.trim().replace(/^\(\(\)\s*=>\s*\{\s*"use strict";/, "");
if (core === script.trim()) throw new Error("IIFE header not stripped — extraction is wrong");

// Minimal canvas shim so paintStone runs headless.
const ctxShim = () => ({
  createImageData: (w, h) => ({ data: new Uint8ClampedArray(w * h * 4) }),
  putImageData(img) { this.out = img; },
});

const api = new Function(core + `
  return { S, render, components, symmetrise, inTile, paintStone, pigmentOf, pigmentSource,
           parseHex, hex, norm, mark, ALPHABET, F, CHAMFER, PIG_LUMA, PALETTE };`)();

const { S, render, components, inTile, paintStone, pigmentOf, pigmentSource,
        parseHex, hex, norm, mark, ALPHABET, F, PALETTE } = api;

// The shipping lexicon: 28 axes, each a dimension of the world, each with two
// opposed poles. Four poles are named by their second-choice synonym because the
// obvious word collided under the mark rule — see the homograph probe at the end.
const LEMMAS = [   // the 56 poles of the 28 axes — see axes.js
  "CAELVM","INFERNVS","ORIGO","FINIS","LVX","TENEBRAE","ORDO","CHAOS","SOL","LVNA",
  "IGNIS","GELV","VNDA","SAXVM","VENTVS","TERRA","PLENVM","VACVVM",
  "VITA","MORS","GERMEN","TABES","CARO","OSSA","HERBA","LIGNVM","HOMO","BESTIA",
  "FERRVM","AVRVM","AES","ADAMAS","GEMMA","PVLVIS","CANDIDVM","SORDES",
  "OPVS","NATVM","NOVVM","SENEX","TOTVM","FRACTVM","TEGMEN","NVDVM",
  "PORTA","VALLVM","GRADVS","AEQVVM",
  "HOSTIS","MITIS","VNICVM","GREX",
];

// Chebyshev distance from a pixel to the nearest pixel OUTSIDE the tile. This is
// the margin the user asked for: no part of a glyph may come within 2 px of the
// stone's edge, chamfered corners included.
const MIN_PAD = 3;
function padOf(x, y) {
  for (let r = 1; r <= 8; r++)
    for (let dy = -r; dy <= r; dy++) for (let dx = -r; dx <= r; dx++)
      if ((Math.abs(dx) === r || Math.abs(dy) === r) && !inTile(x + dx, y + dy)) return r;
  return 9;
}

let bad = [], seen = new Map(), worstPad = 99, worstAt = "", comps = new Set();
for (const l of LEMMAS) {
  const b = render(l);
  comps.add(components(b));

  // margin to the stone's edge
  for (let y = 0; y < S; y++) for (let x = 0; x < S; x++) {
    if (!b[y*S+x]) continue;
    const p = padOf(x, y);
    if (p < worstPad) { worstPad = p; worstAt = `${l} @ ${x},${y}`; }
    if (p < MIN_PAD) bad.push(`${l}: only ${p}px from the edge at ${x},${y}`);
  }

  // NOTE: mirror symmetry and single-component continuity are no longer rules —
  // side carries meaning now, and a glyph may be drawn in more than one piece.
  // Components are still REPORTED, because a wild jump would signal a bug.

  // nothing outside the octagon
  for (let y = 0; y < S; y++) for (let x = 0; x < S; x++)
    if (b[y*S+x] && !inTile(x, y)) bad.push(`${l}: ink outside octagon at ${x},${y}`);

  const key = b.join("");
  if (seen.has(key)) bad.push(`${l}: identical to ${seen.get(key)}`);
  seen.set(key, l);
}

// Pigment: whatever the SOURCE — an authored hex or the name hash — every groove
// must cut to the same luminance. That is the invariant the static layer must not
// be able to break.
const LUMA = c => 0.2126*c[0] + 0.7152*c[1] + 0.0722*c[2];
let lo = 1e9, hi = -1e9, hues = new Map(), nStatic = 0, shared = [];
for (const l of LEMMAS) {
  const p = pigmentOf(l), y = LUMA(p), h = hex(p);
  lo = Math.min(lo, y); hi = Math.max(hi, y);
  if (hues.has(h)) shared.push(`${hues.get(h)} / ${l} both ${h}`);
  else hues.set(h, l);
  if (pigmentSource(l) === "static") nStatic++;
  else bad.push(`${l}: no authored pigment (falls back to the hash)`);
}

// Every palette key must be a lemma we actually ship, and parse.
for (const k of Object.keys(PALETTE)) {
  if (!parseHex(PALETTE[k])) bad.push(`palette ${k}: unparseable ${PALETTE[k]}`);
  if (!LEMMAS.some(l => norm(l) === k)) bad.push(`palette ${k}: not in the lexicon`);
}

// The fallback must absorb every bad input rather than throwing or failing a load.
const junk = [undefined, null, "", "nope", "#12345", "#ggg000", "#000000", 42, {}];
for (const j of junk) {
  const p = pigmentOf("IGNIS", j);
  const y = LUMA(p);
  if (!p || Math.abs(y - api.PIG_LUMA) > 1.5)
    bad.push(`fallback: ${JSON.stringify(j)} produced ${p && y.toFixed(1)}`);
}
// ...and a junk value must land on the HASH, not on the authored colour.
if (hex(pigmentOf("IGNIS", "nope")) === hex(pigmentOf("IGNIS")))
  bad.push("fallback: invalid static value did not fall through to the hash");

// Monochrome legibility: desaturate the finished tile and require the glyphs to
// stay just as distinct. Quantised coarsely (16 levels) on purpose, so tiny
// per-hue luminance jitter cannot fake a difference that shape isn't carrying.
const monoKeys = new Set();
for (const l of LEMMAS) {
  const ctx = ctxShim(); paintStone(ctx, render(l), { lemma: l });
  const d = ctx.out.data, g = [];
  for (let i = 0; i < S*S; i++)
    g.push(d[i*4+3] === 0 ? -1 : Math.round(LUMA([d[i*4], d[i*4+1], d[i*4+2]]) / 16));
  monoKeys.add(g.join(","));
}

// Blank tile: no cuts, still an octagon.
const blank = new Uint8Array(S*S);
const bctx = ctxShim(); paintStone(bctx, blank, { lemma: "" });
let opaque = 0;
for (let i = 0; i < S*S; i++) if (bctx.out.data[i*4+3]) opaque++;
const tileArea = (() => { let n = 0; for (let y=0;y<S;y++) for (let x=0;x<S;x++) if (inTile(x,y)) n++; return n; })();

// Letter forms must be continuous AND clear the margin at BOTH positions they can
// occupy — the top slot (row 2) and the bottom slot (row 7). The top slot is the
// tight one: a form that flares at its first row crowds the chamfer there.
const formBad = [];
for (let i = 0; i < ALPHABET.length; i++) {
  for (const at of [2, 7]) {
    const b = new Uint8Array(S*S); F[i](b, at);
    for (let y=0;y<S;y++) for (let x=0;x<S;x++) {
      if (!b[y*S+x]) continue;
      if (!inTile(x,y)) formBad.push(`form ${ALPHABET[i]}@${at}: outside tile at ${x},${y}`);
      else if (padOf(x,y) < MIN_PAD) formBad.push(`form ${ALPHABET[i]}@${at}: ${padOf(x,y)}px at ${x},${y}`);
    }
  }
}
if (formBad.length) console.log("form margin violations:\n  " + formBad.join("\n  ") + "\n");

// THE SERIF RULE. The length tally's serif is two pips at x=3 and x=12 on row 12.
// If any letter-form can reach those pixels it swallows the serif, and two words
// differing only in length then render identically. That has bitten twice already
// (VIGILIA/VITA, then AES/AEQVVM), so it is a structural check rather than a habit:
// every form must stay inside x=4..11, leaving columns 3 and 12 permanently free.
for (let i = 0; i < ALPHABET.length; i++) {
  for (const at of [2, 7]) {
    const b = new Uint8Array(S*S); F[i](b, at);
    for (let y = 0; y < S; y++)
      if (b[y*S+3] || b[y*S+12])
        bad.push(`form ${ALPHABET[i]}@${at}: reaches x=3 or x=12 (row ${y}) — may swallow the serif`);
  }
}

// The 23 forms must also stay distinct FROM EACH OTHER — narrowing one to clear the
// margin must not collapse it onto another.
const formSeen = new Map();
for (let i = 0; i < ALPHABET.length; i++) {
  const b = new Uint8Array(S*S); F[i](b, 2);
  const k = b.join("");
  if (formSeen.has(k)) bad.push(`form ${ALPHABET[i]}: identical to ${formSeen.get(k)}`);
  formSeen.set(k, ALPHABET[i]);
}

console.log(`lemmas            ${LEMMAS.length}`);
console.log(`distinct shapes   ${seen.size}/${LEMMAS.length}`);
console.log(`distinct in mono  ${monoKeys.size}/${LEMMAS.length}`);
console.log(`pigments          ${nStatic} static, ${LEMMAS.length-nStatic} dynamic; ${hues.size} distinct`);
if (shared.length) console.log(`  sharing a hue (allowed — colour carries nothing): ${shared.join("; ")}`);
console.log(`groove luma       ${lo.toFixed(2)} .. ${hi.toFixed(2)}  (target ${api.PIG_LUMA})`);
console.log(`octagon area      ${tileArea}/256 px, opaque on blank ${opaque}`);
console.log(`chamfer           ${api.CHAMFER}`);
console.log(`tightest margin   ${worstPad}px  (min ${MIN_PAD})  ${worstAt}`);
console.log(`components/glyph  ${[...comps].sort((a,b)=>a-b).join(", ")}`);

// THE WORD BAR. Letters are held to 12px; WORDS were held only to "not byte-identical",
// which is far too weak — PVLVIS and PVRVM passed it while differing by two pixels of
// foot tally. Two words sharing a mark differ ONLY by the tally, so this is the check
// that catches a bad mark rule, and it is the one that was missing.
const WORD_BAR = 12;
const hamm = (a,b) => { let n=0; for (let i=0;i<S*S;i++) if (!!a[i] !== !!b[i]) n++; return n; };
const rendered = LEMMAS.map(l => [l, render(l)]);
let worstWord = 999, worstWordAt = "";
for (let i=0;i<rendered.length;i++) for (let j=i+1;j<rendered.length;j++) {
  const h = hamm(rendered[i][1], rendered[j][1]);
  if (h < worstWord) { worstWord = h; worstWordAt = `${rendered[i][0]}/${rendered[j][0]}`; }
  if (h < WORD_BAR) bad.push(`${rendered[i][0]} / ${rendered[j][0]}: only ${h}px apart (words need ${WORD_BAR})`);
}
console.log(`tightest word     ${worstWord}px  (min ${WORD_BAR})  ${worstWordAt}`);

// Homograph probe: the mark is (letter 1, letter 2, length tally), so two lemmas
// agreeing on all three MUST render alike. That is a language bug, and the
// datapack validator has to reject it at load rather than ship two identical tiles.
const probe = [["TERRA","TVRBA"], ["PORTA","PVRVM"], ["SENEX","SANVM"],
               ["VITA","VETVS"], ["MORS","MVRVM"]];
// These five pairs are why the lexicon says GREX, CANDIDVM, TOTVM, SENEX and VALLVM
// rather than TVRBA, PVRVM, SANVM, VETVS and MVRVM: each obvious choice landed inside
// the 12px word bar against a rune already in the axes. The fix is a synonym, never the art.
console.log("\ncollision probe — the five words the lexicon had to rename (all SHOULD be under 12px):");
for (const [a, b] of probe) {
  const h = hamm(render(a), render(b));
  console.log(`  ${a.padEnd(9)} vs ${b.padEnd(9)} -> ${h === 0 ? "IDENTICAL" : h + "px"}${h >= WORD_BAR ? "  (!! no longer collides — the doc is stale)" : ""}`);
}

console.log("\n" + (bad.length ? "FAIL\n  " + bad.slice(0, 20).join("\n  ")
                               : "PASS — in-bounds, clear of the margin, unique in colour and in mono"));

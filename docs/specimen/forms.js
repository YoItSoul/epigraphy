// Pairwise confusability audit of the 23 letter-forms.
const fs = require("fs");
const src = fs.readFileSync(__dirname + "/index.html", "utf8");
const script = src.slice(src.lastIndexOf("<script>") + 8, src.indexOf("/*==DOM==*/"));
const core = script.trim().replace(/^\(\(\)\s*=>\s*\{\s*"use strict";/, "");
const api = new Function(core + `return { S, F, ALPHABET, symmetrise };`)();
const { S, F, ALPHABET, symmetrise } = api;

const AT = 2, ROWS = 6;
const bits = i => { const b = new Uint8Array(S*S); F[i](b, AT); return b; };
const forms = ALPHABET.split("").map((_, i) => bits(i));

// Row-width profile: how wide the form is at each of its six rows. Two forms with
// the same profile are the same drawing at different scales — the exact failure mode
// that keeps recurring, so measure it directly.
// Row EXTENTS, not row widths. Width was the right measure while every form was
// mirrored; now that a mark can sit on one side, two forms can share every row
// width and be mirror images of each other — which is a real difference and one
// of the most visible there is. So record where each row starts and ends.
const profile = b => {
  const p = [];
  for (let y = AT; y < AT + ROWS; y++) {
    let lo = 99, hi = -1;
    for (let x = 0; x < S; x++) if (b[y*S+x]) { lo = Math.min(lo,x); hi = Math.max(hi,x); }
    p.push(hi < 0 ? "." : `${lo}-${hi}`);
  }
  return p;
};
// Normalised profile: the SHAPE of the profile, scale removed. If two forms match
// here they are the same silhouette drawn at different widths.
const shapeOf = p => p.join("|");   // extents are absolute; nothing to normalise

const iou = (a, b) => {
  let inter = 0, uni = 0;
  for (let i = 0; i < S*S; i++) { if (a[i] && b[i]) inter++; if (a[i] || b[i]) uni++; }
  return uni ? inter/uni : 1;
};
const ink = b => b.reduce((n,v) => n+v, 0);

// Differing pixels — the test that matters. IoU is reported but not used as a gate:
// it punishes a form for being a superset of another, and "a post" vs "a post with
// horns" is an ABSOLUTE difference you can name.
//
// The threshold has been wrong twice, in both directions, and both times it drove
// the design rather than judging it. At 10px (calibrated for dense outlines) every
// light form was excluded, which forced heavy shapes. At 4px it rubber-stamped an
// alphabet where 28 of 210 pairs differed by a single arm. 12px is about one whole
// carved part — a bar, a diagonal, a stone — which is the smallest difference that
// is reliably NAMEABLE at 16px.
const MIN_DIFF = 12;
const hamming = (a, b) => { let n = 0; for (let i = 0; i < S*S; i++) if (!!a[i] !== !!b[i]) n++; return n; };

const pairs = [];
for (let i = 0; i < forms.length; i++) for (let j = i+1; j < forms.length; j++) {
  const pi = profile(forms[i]), pj = profile(forms[j]);
  pairs.push({
    a: ALPHABET[i], b: ALPHABET[j],
    diff: hamming(forms[i], forms[j]),
    iou: iou(forms[i], forms[j]),
    sameShape: shapeOf(pi) === shapeOf(pj),
    pi, pj,
  });
}
pairs.sort((x,y) => x.diff - y.diff);

const fails = pairs.filter(p => p.diff < MIN_DIFF || p.sameShape);
console.log(`PAIRS FAILING THE BAR (differ by < ${MIN_DIFF}px, or identical footprint): ${fails.length}\n`);
for (const p of fails)
  console.log(`  ${p.a}/${p.b}  ${String(p.diff).padStart(2)}px differ  IoU ${p.iou.toFixed(2)}`
            + (p.sameShape ? "  SAME SILHOUETTE, different width" : "")
            + `\n         ${p.a}: ${p.pi.join(" ")}\n         ${p.b}: ${p.pj.join(" ")}`);

console.log(`\ntightest pairs overall:`);
for (const p of pairs.slice(0, 8))
  console.log(`  ${p.a}/${p.b}  ${String(p.diff).padStart(2)}px  IoU ${p.iou.toFixed(2)}`);
const groups = new Map();
for (const i in forms) {
  const k = shapeOf(profile(forms[i]));
  if (!groups.has(k)) groups.set(k, []);
  groups.get(k).push(ALPHABET[i]);
}
console.log("\nSILHOUETTE FAMILIES (letters sharing a profile shape):");
[...groups.entries()].sort((a,b) => b[1].length - a[1].length)
  .forEach(([k, ls]) => console.log(`  [${k}]  ${ls.join(" ")}${ls.length > 1 ? "   <-- collapses" : ""}`));

console.log("\nink per form:");
console.log("  " + ALPHABET.split("").map((c,i) => `${c}:${ink(forms[i])}`).join("  "));

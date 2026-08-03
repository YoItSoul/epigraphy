# The specimen page

`index.html` is the **live renderer** — the reference implementation of
[`GLYPH_SPEC.md`](../GLYPH_SPEC.md), plus a specimen of everything it produces:
the 21 letter forms, all 52 runes paired by axis with their English, and the
worked vocabulary from the thirty-item test.

Open it directly in a browser. No build, no dependencies, no network.

## The audits

Both scripts extract the *live* algorithm out of `index.html` rather than
reimplementing it, so they cannot drift from what the page draws.

```
node verify.js     # the full glyph audit — margins, distinctness, pigment, the serif rule
node forms.js      # pairwise letterform distinctness (12px bar)
```

`verify.js` is the one that matters. It asserts, over all 52 runes:

| Check | Bar |
|---|---|
| Distinct shapes | 52/52 |
| Distinct with colour stripped | 52/52 |
| Margin to the tile edge | ≥ 3 px |
| Distance between any two words | ≥ 12 px |
| Groove luminance | 62, invariant across every pigment |
| Letter forms confined to `x = 4..11` | so no form can swallow the tally serif |

A failure here is a **content** bug — two runes that need a synonym — far more
often than a renderer bug. See `GLYPH_SPEC.md` §8.

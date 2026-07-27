#!/usr/bin/env python3
"""Generate the blank glyph tile — docs/GLYPH_SPEC.md §1.1, §5.

A tile with no cuts at all: bare stone, still an octagon. It does three jobs with one
asset — the unknown glyph (Tier 0), the empty codex slot, and the uninscribed tablet.

It is also the whole tile half of the renderer, minus the letter-forms: the silhouette
(§5.1), the stone gradient and grain (§5.3), and the one-light bevel (§5.2). Landing the
21 letter-forms later means adding a cut mask to this, not rewriting it.

Everything here is deterministic — no randomness — so the tile is byte-identical on
every run. Usage:

    python3 tools/blank_tile.py src/main/resources/assets/epigraphy/textures/item/glyph_blank.png
"""

import struct
import sys
import zlib

S = 16
CHAMFER = 2                       # §5.1 — deepest cut the figure still clears
AMP = 0.155                       # §5.2 — deep cut; sighted glyphs use 0.085
STONE_HI = (201, 195, 183)        # §5.3 — lit corner
STONE_LO = (112, 107, 99)         # §5.3 — shadowed corner
GRAIN = 8                         # ±8, hashed from (x, y)


def in_tile(x, y):
    """§5.1 — the 16x16 square with its four corners chamfered by 2. 244 of 256 px."""
    if not (0 <= x < S and 0 <= y < S):
        return False
    return min(x, S - 1 - x) + min(y, S - 1 - y) >= CHAMFER


def grain(x, y):
    """A hash, not randomness — the renderer must stay byte-identical (§5.3)."""
    h = 2166136261
    for b in (x & 0xFF, y & 0xFF):
        h = ((h ^ b) * 16777619) & 0xFFFFFFFF
    return ((h >> 8) % (2 * GRAIN + 1)) - GRAIN


def stone(x, y):
    """Gradient lit from the top-left, plus grain. Material, not information (§5.3)."""
    t = (x + y) / (2 * (S - 1))
    g = grain(x, y)
    return [hi + (lo - hi) * t + g for hi, lo in zip(STONE_HI, STONE_LO)]


def height(x, y, cuts):
    """§5.2 — void at -1, incised stroke at 0, stone surface at 1."""
    if not in_tile(x, y):
        return -1
    return 0 if (x, y) in cuts else 1


def clamp(v, lo, hi):
    return lo if v < lo else hi if v > hi else v


def render(cuts=frozenset()):
    """One height field and one light produce all of the depth (§5.2)."""
    rows = []
    for y in range(S):
        row = []
        for x in range(S):
            if not in_tile(x, y):
                row.append((0, 0, 0, 0))
                continue
            h = height(x, y, cuts)
            d = (2 * (h - height(x - 1, y - 1, cuts))
                 + (h - height(x, y - 1, cuts))
                 + (h - height(x - 1, y, cuts))
                 - 2 * (h - height(x + 1, y + 1, cuts))
                 - (h - height(x, y + 1, cuts))
                 - (h - height(x + 1, y, cuts)))
            f = clamp(1 + AMP * d, 0.42, 1.68)
            r, g, b = (int(round(clamp(c * f, 0, 255))) for c in stone(x, y))
            row.append((r, g, b, 255))
        rows.append(row)
    return rows


def write_png(path, rows):
    raw = b"".join(b"\x00" + bytes(v for px in row for v in px) for row in rows)

    def chunk(tag, data):
        return (struct.pack(">I", len(data)) + tag + data
                + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF))

    png = (b"\x89PNG\r\n\x1a\n"
           + chunk(b"IHDR", struct.pack(">IIBBBBB", S, S, 8, 6, 0, 0, 0))
           + chunk(b"IDAT", zlib.compress(raw, 9))
           + chunk(b"IEND", b""))
    with open(path, "wb") as f:
        f.write(png)


def main():
    if len(sys.argv) != 2:
        print(__doc__)
        return 1
    rows = render()
    opaque = sum(1 for row in rows for px in row if px[3] == 255)
    assert opaque == 244, f"expected 244 opaque px, got {opaque}"  # §5.1
    write_png(sys.argv[1], rows)
    print(f"wrote {sys.argv[1]} — {opaque}/256 px opaque, zero cuts")
    return 0


if __name__ == "__main__":
    sys.exit(main())

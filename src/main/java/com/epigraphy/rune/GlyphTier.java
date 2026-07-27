package com.epigraphy.rune;

/**
 * How much a player knows about a glyph ({@code docs/RUNES.md} §6, D19).
 *
 * <p>The ladder runs <b>uncut → shallow and empty → deep and filled</b>. It is a
 * <em>value</em> difference before it is a colour one, so it survives desaturation and
 * reads identically to a colourblind player.
 *
 * <p>(Tier 3, <em>decoded</em>, belongs to a rune word rather than a glyph: it is the
 * moment a sequence of learned glyphs is guessed correctly and starts rendering as its
 * true referent.)
 */
public enum GlyphTier {

    /** The blank tile — bare stone, no cuts. The player has never seen this symbol. */
    UNKNOWN,
    /** The full figure cut shallow and unfilled: seen, but not yet taken down. */
    SIGHTED,
    /** Cut to full depth and inlaid. The gloss reads, and the glyph is submittable. */
    LEARNED;

    public boolean isAtLeast(GlyphTier other) {
        return ordinal() >= other.ordinal();
    }
}

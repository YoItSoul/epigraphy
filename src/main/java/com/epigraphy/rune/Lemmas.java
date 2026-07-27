package com.epigraphy.rune;

/**
 * The deterministic lemma rules from {@code docs/GLYPH_SPEC.md} — normalisation
 * (§2.1), the first-and-third mark (§3.0), and the tally foot (§3.1).
 *
 * <p>These are the parts of the glyph construction rule that do not need a
 * renderer: they decide a glyph's <em>identity</em>, so the datapack loader can
 * reject two glyphs that would render alike (§8) long before any art exists.
 * Given the same lemma they must always produce the same answer.
 */
public final class Lemmas {

    /** The classical Latin alphabet — 21 letters, frozen at v1 (§2.1). */
    public static final String ALPHABET = "ABCDEFGHIKLMNOPQRSTVX";

    private Lemmas() {
    }

    /**
     * {@code uppercase · U→V · W→V · J→I · Y→I · Z→S · strip non-letters} (§2.1).
     * J, U and W are mediaeval; Y and Z were Greek imports Latin had already spent
     * centuries absorbing as I and S. So {@code PULVIS} and {@code PVLVIS} are the
     * same word, and so are {@code ZONA} and {@code SONA}.
     */
    public static String normalise(String lemma) {
        StringBuilder out = new StringBuilder(lemma.length());
        for (int i = 0; i < lemma.length(); i++) {
            char c = Character.toUpperCase(lemma.charAt(i));
            switch (c) {
                case 'U', 'W' -> c = 'V';
                case 'J', 'Y' -> c = 'I';
                case 'Z' -> c = 'S';
                default -> {
                }
            }
            if (c >= 'A' && c <= 'Z') {
                out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * The two letters the glyph is drawn from: the normalised lemma's <b>first and
     * third</b> (§3.0), or the authored {@code mark} override when one is given.
     *
     * <p>First-and-third rather than first-and-second because Latin clusters hard on
     * prefixes: measured over the shipped lexicon, first-and-second left 13 colliding
     * clusters and first-and-third leaves 5 — all of which were then fixed in the
     * lexicon with a synonym rather than in the art.
     *
     * <p>A word of fewer than three letters falls back to its last letter, so a
     * two-letter lemma still yields a two-letter mark.
     */
    public static String mark(String normalisedLemma, String override) {
        if (override != null && !override.isEmpty()) {
            String m = normalise(override);
            return m.length() >= 2 ? m.substring(0, 2) : m;
        }
        if (normalisedLemma.length() < 2) {
            return normalisedLemma;
        }
        char first = normalisedLemma.charAt(0);
        char third = normalisedLemma.charAt(Math.min(2, normalisedLemma.length() - 1));
        return "" + first + third;
    }

    /**
     * The tally foot's half-width, {@code 1 + ((len − 2) mod 3)} (§3.1). The bar widens
     * one step per letter up to three, then restarts with the serif raised.
     */
    public static int footWidth(int normalisedLength) {
        return 1 + (tally(normalisedLength) % 3);
    }

    /** Whether the foot carries the hand mark — the two detached pips (§3.1/§3.2). */
    public static boolean footSerif(int normalisedLength) {
        return tally(normalisedLength) >= 3;
    }

    private static int tally(int normalisedLength) {
        return Math.max(0, Math.min(5, normalisedLength - 2));
    }

    /**
     * The glyph's visual identity: {@code (letter 1, letter 3, length tally)}. Two
     * glyphs agreeing on all three <em>must</em> render as the same tile, so the loader
     * compares these rather than waiting on the renderer (§8).
     *
     * <p>Per the spec this is a <b>language</b> bug and not an art one — the fix is a
     * synonym, which is why a throng is {@code GREX} and not {@code TVRBA}.
     */
    public static String signature(String lemma, String markOverride) {
        String n = normalise(lemma);
        return mark(n, markOverride) + "/" + footWidth(n.length()) + (footSerif(n.length()) ? "+" : "");
    }

    /** True if every character is one of the classical 21 (§8). */
    public static boolean isRenderable(String normalised) {
        if (normalised.isEmpty()) {
            return false;
        }
        for (int i = 0; i < normalised.length(); i++) {
            if (ALPHABET.indexOf(normalised.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }
}

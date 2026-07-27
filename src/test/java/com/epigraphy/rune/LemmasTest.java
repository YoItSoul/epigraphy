package com.epigraphy.rune;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The lemma rules decide what a glyph <em>looks like</em>, so getting them wrong is the
 * one class of bug that ships two identical tiles and blames the artist
 * ({@code docs/GLYPH_SPEC.md} §3, §8).
 */
class LemmasTest {

    @ParameterizedTest(name = "{0} normalises to {1}")
    @CsvSource({
            "PULVIS,  PVLVIS",   // U folds to V
            "PVLVIS,  PVLVIS",   // ...and is already there
            "ZONA,    SONA",     // Z was a Greek import Latin absorbed as S
            "WATER,   VATER",    // W is mediaeval
            "JOY,     IOI",      // J and Y both fold to I
            "'IGNIS', IGNIS",
            "ignis,   IGNIS",
            "'A-B C', ABC",      // non-letters are stripped
    })
    void normalisesToTheClassicalTwentyOne(String raw, String expected) {
        assertEquals(expected, Lemmas.normalise(raw));
    }

    @Test
    @DisplayName("normalisation only ever emits letters of the classical 21")
    void normalisationEmitsOnlyRenderableLetters() {
        for (char c = 'A'; c <= 'Z'; c++) {
            String normalised = Lemmas.normalise("" + c + c);
            assertTrue(Lemmas.isRenderable(normalised),
                    "'" + c + "' normalised to \"" + normalised + "\", which has no figure to draw");
        }
    }

    @ParameterizedTest(name = "{0} marks {1}")
    @CsvSource({
            "SOL,       SL",
            "VITA,      VT",
            "TERRA,     TR",
            "CAELVM,    CE",
            "TENEBRAE,  TN",
            "IGNIS,     IN",     // first and THIRD — not IG
            "OSSA,      OS",
    })
    void marksFirstAndThirdLetters(String lemma, String expected) {
        assertEquals(expected, Lemmas.mark(Lemmas.normalise(lemma), null));
    }

    @Test
    @DisplayName("a two-letter lemma marks its last letter rather than running off the end")
    void shortLemmaFallsBackToItsLastLetter() {
        assertEquals("AB", Lemmas.mark(Lemmas.normalise("AB"), null));
    }

    @Test
    @DisplayName("an explicit mark overrides the first-and-third rule")
    void explicitMarkWins() {
        assertEquals("TB", Lemmas.mark(Lemmas.normalise("TVRBA"), "TB"));
    }

    /** The worked table in {@code GLYPH_SPEC.md} §3.1, read straight off the doc. */
    @ParameterizedTest(name = "{0} ({1} letters) -> width {2}, serif {3}")
    @CsvSource({
            "SOL,       3, 2, false",
            "VITA,      4, 3, false",
            "TERRA,     5, 1, true",
            "CAELVM,    6, 2, true",
            "TENEBRAE,  8, 3, true",
    })
    void footTalliesWordLength(String lemma, int letters, int width, boolean serif) {
        String normalised = Lemmas.normalise(lemma);
        assertEquals(letters, normalised.length(), "test data disagrees with the lemma");
        assertEquals(width, Lemmas.footWidth(normalised.length()));
        assertEquals(serif, Lemmas.footSerif(normalised.length()));
    }

    @Test
    @DisplayName("the foot never exceeds three half-widths, whatever the word's length")
    void footWidthIsCappedAtThree() {
        // The ceiling is what holds the widest foot 3px clear of the bottom chamfer (§5.1).
        for (int length = 1; length <= 40; length++) {
            int width = Lemmas.footWidth(length);
            assertTrue(width >= 1 && width <= 3, "length " + length + " gave width " + width);
        }
    }

    @Test
    @DisplayName("the signature separates lemmas that share a mark but differ in length")
    void signatureSeesLength() {
        // SOL and SORDES both mark SL/SR territory; the foot is what tells them apart.
        assertEquals("SL", Lemmas.mark(Lemmas.normalise("SOL"), null));
        assertEquals("SR", Lemmas.mark(Lemmas.normalise("SORDES"), null));

        // Same mark, different length -> different tile.
        assertFalse(Lemmas.signature("AXE", null).equals(Lemmas.signature("AXEEEE", null)));
    }

    @Test
    @DisplayName("two lemmas agreeing on mark and length share a signature — a language bug")
    void collisionsAreVisibleInTheSignature() {
        // TERRA and TVRBA both mark TR at five letters, which is exactly why a throng
        // is GREX. The loader has to be able to see this before any art exists.
        assertEquals(Lemmas.signature("TERRA", null), Lemmas.signature("TVRBA", null));
    }
}

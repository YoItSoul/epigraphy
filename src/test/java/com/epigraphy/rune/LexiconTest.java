package com.epigraphy.rune;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * The audit {@code docs/GLYPH_SPEC.md} §8 and {@code docs/RUNES.md} §2 describe, run
 * against the shipped lexicon.
 *
 * <p>The one that matters most is {@link #everyGlyphRendersToItsOwnTile()}: two lemmas
 * agreeing on their marked letters and their length produce the same tile, and that is a
 * <b>language</b> bug whose fix is a synonym. Catching it here means catching it before
 * the renderer exists to blame.
 */
class LexiconTest {

    @Test
    @DisplayName("26 axes, 52 runes")
    void shipsFiftyTwoRunes() {
        assertEquals(52, Lexicon.size());
        assertEquals(52, Lexicon.byId().size(), "an id was reused");
    }

    @Test
    @DisplayName("every rune has an opposite, and the two agree with each other")
    void everyAxisIsReciprocal() {
        Map<ResourceLocation, Glyph> lexicon = Lexicon.byId();
        lexicon.forEach((id, glyph) -> {
            ResourceLocation opposite = glyph.opposite().orElseThrow(() ->
                    new AssertionError(id + " has no opposite; the lexicon is a list of axes, not of words"));
            Glyph other = lexicon.get(opposite);
            assertTrue(other != null, id + " names " + opposite + " as its opposite, but no such rune ships");
            assertEquals(id, other.opposite().orElse(null),
                    id + " and " + opposite + " disagree about being each other's opposite");
            assertEquals(glyph.axis(), other.axis(), id + " and " + opposite + " sit on different axes");
            assertEquals(glyph.category(), other.category(), id + " and " + opposite + " sit in different categories");
            assertEquals(glyph.rarity(), other.rarity(),
                    id + " and " + opposite + " hide in different places, though they are one concept");
        });
    }

    @Test
    @DisplayName("no two runes render to the same tile")
    void everyGlyphRendersToItsOwnTile() {
        Map<String, ResourceLocation> bySignature = new HashMap<>();
        Lexicon.byId().forEach((id, glyph) -> {
            ResourceLocation clash = bySignature.putIfAbsent(glyph.signature(), id);
            if (clash != null) {
                fail(id + " and " + clash + " both mark " + glyph.signature()
                        + " — replace one lemma with a synonym, as TVRBA became GREX");
            }
        });
        assertEquals(52, bySignature.size());
    }

    @Test
    @DisplayName("no two runes even share a mark — the foot is headroom, not the thing holding them apart")
    void noTwoRunesShareAMark() {
        Set<String> marks = new HashSet<>();
        Lexicon.byId().values().forEach(glyph -> {
            String mark = Lemmas.mark(glyph.normalisedLemma(), glyph.mark().orElse(null));
            assertTrue(marks.add(mark), "two runes mark " + mark
                    + "; the first-and-third rule is supposed to separate all 52 on its own");
        });
    }

    @Test
    @DisplayName("every lemma is drawable, and every rune says what it means")
    void everyRuneIsWellFormed() {
        Lexicon.byId().forEach((id, glyph) -> {
            String normalised = glyph.normalisedLemma();
            assertTrue(normalised.length() >= 2, id + " is too short to mark a tile");
            assertTrue(Lemmas.isRenderable(normalised),
                    id + " has letters outside the classical 21: " + normalised);
            assertFalse(glyph.gloss().isBlank(), id + " has no gloss, so learning it would show nothing");
            assertTrue(glyph.description().isPresent(), id + " has no description");
            assertTrue(glyph.sightingsToLearn() >= 1, id + " cannot be learned");
        });
    }

    @Test
    @DisplayName("the lexicon can name things: enough runes may head a word")
    void enoughRunesCanHeadAWord() {
        long heads = Lexicon.byId().values().stream().filter(Glyph::head).count();
        // A word needs a head, so a lexicon of pure qualifiers names nothing at all.
        assertTrue(heads >= 26, "only " + heads + " of 52 runes can head a rune word");
    }

    @Test
    @DisplayName("every rune's item path is unique and legal")
    void itemPathsAreDistinct() {
        Set<String> paths = new HashSet<>();
        for (Lexicon.Entry entry : Lexicon.entries()) {
            String path = entry.itemPath();
            assertTrue(paths.add(path), "two runes want the item " + path);
            assertNull(ResourceLocation.read("epigraphy:" + path).error().orElse(null),
                    path + " is not a legal registry path");
        }
        assertEquals(52, paths.size());
    }
}

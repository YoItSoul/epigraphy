package com.epigraphy.rune;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * One glyph: a single symbol standing for one Latin word ({@code docs/RUNES.md} §1).
 * The atomic unit of the language — glyphs are <b>discovered</b> in the world, and only
 * discovered glyphs may be spent on a rune word guess.
 *
 * <p>Loaded from {@code data/<ns>/glyphs/*.json} by {@link GlyphManager}; the shipped
 * lexicon is written out of {@link Lexicon} at datagen so the JSON can never drift from
 * the items registered for it. Authoring guide: {@code docs/AUTHORING.md} §2.
 *
 * @param lemma      the Latin word, e.g. {@code IGNIS}; what a fluent player reads
 * @param gloss      the short English meaning, shown once the glyph is learned
 * @param category   which grouping of the lexicon it belongs to — organisational only
 * @param head       the ✦ mark: whether this glyph may <b>head</b> a rune word, i.e.
 *                   name a kind of thing rather than merely qualify one
 *                   ({@code docs/RUNES.md} §3.1)
 * @param rarity     biases where the glyph hides, and supplies the sighting default
 * @param sightings  independent sightings needed to learn it; defaults from {@code rarity}
 * @param axis       the dimension this glyph is one pole of, e.g. {@code heat}
 * @param opposite   the glyph at the other pole of that axis, if it has one
 * @param description flavour text
 * @param mark       forces the two letters the tile is drawn from, when a synonym will
 *                   not resolve a collision ({@code docs/GLYPH_SPEC.md} §8)
 * @param pigment    authored inlay colour as {@code #RRGGBB}; anything malformed falls
 *                   through to the name hash <em>silently</em> — a colour typo must
 *                   never be able to fail a datapack load ({@code GLYPH_SPEC.md} §6.3)
 * @param texture    overrides generated art entirely, for a glyph worth hand-drawing
 */
public record Glyph(
        String lemma,
        String gloss,
        GlyphCategory category,
        boolean head,
        GlyphRarity rarity,
        Optional<Integer> sightings,
        Optional<String> axis,
        Optional<ResourceLocation> opposite,
        Optional<String> description,
        Optional<String> mark,
        Optional<String> pigment,
        Optional<ResourceLocation> texture
) {

    public static final Codec<Glyph> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("lemma").forGetter(Glyph::lemma),
            Codec.STRING.fieldOf("gloss").forGetter(Glyph::gloss),
            GlyphCategory.CODEC.fieldOf("category").forGetter(Glyph::category),
            Codec.BOOL.optionalFieldOf("head", false).forGetter(Glyph::head),
            GlyphRarity.CODEC.optionalFieldOf("rarity", GlyphRarity.COMMON).forGetter(Glyph::rarity),
            Codec.INT.optionalFieldOf("sightings_to_learn").forGetter(Glyph::sightings),
            Codec.STRING.optionalFieldOf("axis").forGetter(Glyph::axis),
            ResourceLocation.CODEC.optionalFieldOf("opposite").forGetter(Glyph::opposite),
            Codec.STRING.optionalFieldOf("description").forGetter(Glyph::description),
            Codec.STRING.optionalFieldOf("mark").forGetter(Glyph::mark),
            // Deliberately Codec.STRING and not a colour codec: see pigmentRgb().
            Codec.STRING.optionalFieldOf("pigment").forGetter(Glyph::pigment),
            ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(Glyph::texture)
    ).apply(i, Glyph::new));

    /** The lemma with U/W→V, J/Y→I, Z→S applied ({@code GLYPH_SPEC.md} §2.1). */
    public String normalisedLemma() {
        return Lemmas.normalise(lemma);
    }

    /**
     * {@code (letter 1, letter 3, length tally)} — everything that decides what the tile
     * looks like. Two glyphs sharing a signature would render identically, which the
     * loader treats as a content error ({@code GLYPH_SPEC.md} §8).
     */
    public String signature() {
        return Lemmas.signature(lemma, mark.orElse(null));
    }

    /** Sightings needed to learn this glyph, falling back to the rarity default. */
    public int sightingsToLearn() {
        return Math.max(1, sightings.orElseGet(rarity::defaultSightings));
    }

    /**
     * The authored inlay colour, or empty when none was given <em>or the one given was
     * malformed</em>. Wrong length, stray characters, wrong type and pure black all read
     * as absent, and the caller falls back to hashing the lemma
     * ({@code GLYPH_SPEC.md} §6.3). This method never throws.
     */
    public OptionalInt pigmentRgb() {
        if (pigment.isEmpty()) {
            return OptionalInt.empty();
        }
        String raw = pigment.get().trim();
        if (raw.startsWith("#")) {
            raw = raw.substring(1);
        }
        if (raw.length() != 6) {
            return OptionalInt.empty();
        }
        try {
            int rgb = Integer.parseInt(raw, 16);
            // Pure black carries no hue to renormalise, so it falls through to the hash.
            return rgb == 0 ? OptionalInt.empty() : OptionalInt.of(rgb);
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}

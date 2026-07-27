package com.epigraphy.rune;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * How hard a glyph is to come by ({@code docs/AUTHORING.md} §2). Rarity biases where a
 * glyph hides — rare glyphs appear in dangerous structures and boss loot — and supplies
 * the default number of independent sightings needed to learn it
 * ({@code docs/DISCOVERY.md} §4).
 */
public enum GlyphRarity implements StringRepresentable {

    COMMON("common", 1),
    UNCOMMON("uncommon", 2),
    RARE("rare", 3);

    public static final Codec<GlyphRarity> CODEC = StringRepresentable.fromEnum(GlyphRarity::values);

    private final String name;
    private final int defaultSightings;

    GlyphRarity(String name, int defaultSightings) {
        this.name = name;
        this.defaultSightings = defaultSightings;
    }

    /** Sightings to learn, when the glyph does not state its own. */
    public int defaultSightings() {
        return defaultSightings;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}

package com.epigraphy.rune;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * Which of the lexicon's groupings a glyph belongs to ({@code docs/RUNES.md} §2).
 *
 * <p>The category is <b>organisational, not grammatical</b> — whether a glyph may head
 * a rune word is carried by {@link Glyph#head()} (the ✦ mark), never inferred from the
 * category. It biases where a glyph hides in worldgen and how it is grouped for
 * reference, and nothing else.
 */
public enum GlyphCategory implements StringRepresentable {

    /** Sky, the below, source and edge, light, order — the axes of the world itself. */
    WORLD("world"),
    /** Fire, flow, air, fullness and their poles. */
    ELEMENT("element"),
    /** Life, growth, flesh, green things, folk. */
    LIVING("living"),
    /** Metals, gems, dust, purity. */
    MATTER("matter"),
    /** Wrought against found, new against old, whole against cut. */
    MAKING("making"),
    /** Gate, wall, step, level. */
    FORM("form"),
    /** Temper and number — hostile against tame, one against many. */
    WILL("will"),
    /**
     * Frame glyphs: the invariant signs that bracket an inscription rather than name
     * anything in it (D11/D12). None ship in v1 — D11 is still a leaning, not a
     * decision — but the category exists so a datapack may add them.
     */
    FRAME("frame");

    public static final Codec<GlyphCategory> CODEC = StringRepresentable.fromEnum(GlyphCategory::values);

    private final String name;

    GlyphCategory(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}

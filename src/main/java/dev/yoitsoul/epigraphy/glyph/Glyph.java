package dev.yoitsoul.epigraphy.glyph;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * A single glyph definition loaded from {@code data/<ns>/epigraphy/glyphs/<id>.json}
 * (schema: docs/DESIGN.md §5.1). {@code id} is derived from the loader's file path, not stored
 * in the JSON itself.
 */
public record Glyph(
        ResourceLocation id,
        GlyphRole role,
        String gloss,
        String translation,
        ResourceLocation symbolTexture,
        String flavorText,
        Set<DiscoverySource> discoverySources,
        @Nullable Constellation constellation
) {

    /**
     * Star-trace pattern for a glyph discoverable via the Astrolabe (see docs/DESIGN.md §3.1).
     * Points are in an arbitrary flat coordinate space local to the constellation, not tied to
     * real sky coordinates.
     */
    public record Constellation(List<int[]> points, boolean nightOnly) {
    }
}

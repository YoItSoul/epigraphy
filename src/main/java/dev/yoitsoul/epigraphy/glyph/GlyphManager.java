package dev.yoitsoul.epigraphy.glyph;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Loads {@code data/<ns>/epigraphy/glyphs/<id>.json} datapack entries into {@link Glyph}s
 * (schema: docs/DESIGN.md §5.1) and exposes the merged registry to the rest of the mod.
 */
public class GlyphManager extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();

    private Map<ResourceLocation, Glyph> glyphs = Map.of();

    public GlyphManager() {
        super(new com.google.gson.Gson(), "epigraphy/glyphs");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, Glyph> loaded = new HashMap<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : entries.entrySet()) {
            try {
                loaded.put(entry.getKey(), parse(entry.getKey(), entry.getValue().getAsJsonObject()));
            } catch (JsonSyntaxException | IllegalStateException e) {
                LOGGER.error("Skipping malformed glyph {}: {}", entry.getKey(), e.getMessage());
            }
        }
        this.glyphs = Map.copyOf(loaded);
        LOGGER.info("Epigraphy loaded {} glyphs", this.glyphs.size());
    }

    private static Glyph parse(ResourceLocation id, JsonObject json) {
        GlyphRole role = GlyphRole.valueOf(GsonHelper.getAsString(json, "role").toUpperCase(java.util.Locale.ROOT));
        String gloss = GsonHelper.getAsString(json, "gloss");
        String translation = GsonHelper.getAsString(json, "translation");
        ResourceLocation symbolTexture = new ResourceLocation(GsonHelper.getAsString(json, "symbol_texture"));
        String flavorText = GsonHelper.getAsString(json, "flavor_text", "");

        Set<DiscoverySource> sources = EnumSet.noneOf(DiscoverySource.class);
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "discovery_sources")) {
            sources.add(DiscoverySource.valueOf(element.getAsString().toUpperCase(java.util.Locale.ROOT)));
        }

        Glyph.Constellation constellation = null;
        if (json.has("constellation")) {
            JsonObject constellationJson = json.getAsJsonObject("constellation");
            List<int[]> points = new ArrayList<>();
            for (JsonElement pointElement : GsonHelper.getAsJsonArray(constellationJson, "points")) {
                JsonArray pair = pointElement.getAsJsonArray();
                points.add(new int[]{pair.get(0).getAsInt(), pair.get(1).getAsInt()});
            }
            boolean nightOnly = GsonHelper.getAsBoolean(constellationJson, "night_only", false);
            constellation = new Glyph.Constellation(points, nightOnly);
        }

        return new Glyph(id, role, gloss, translation, symbolTexture, flavorText, sources, constellation);
    }

    public Map<ResourceLocation, Glyph> getGlyphs() {
        return glyphs;
    }
}

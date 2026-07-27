package com.epigraphy.rune;

import com.epigraphy.Epigraphy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Loads glyphs from {@code data/<ns>/glyphs/*.json} and validates them
 * ({@code docs/AUTHORING.md} §8, {@code docs/GLYPH_SPEC.md} §8).
 *
 * <p>Validation reports rather than throws: one bad glyph drops out of the lexicon with
 * an explanation in the log, and the rest of the pack still loads. The one thing that is
 * <em>never</em> allowed to fail a load is a malformed {@code pigment} — a colour typo
 * must not be able to break someone's pack, so it falls silently through to the hash
 * ({@code GLYPH_SPEC.md} §6.3).
 */
public class GlyphManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String DIRECTORY = "glyphs";

    private Map<ResourceLocation, Glyph> glyphs = Map.of();

    public GlyphManager() {
        super(GSON, DIRECTORY);
    }

    public Optional<Glyph> get(ResourceLocation id) {
        return Optional.ofNullable(glyphs.get(id));
    }

    public Set<ResourceLocation> ids() {
        return Collections.unmodifiableSet(glyphs.keySet());
    }

    public Collection<Glyph> all() {
        return Collections.unmodifiableCollection(glyphs.values());
    }

    public int size() {
        return glyphs.size();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, Glyph> loaded = new LinkedHashMap<>();
        // Two glyphs agreeing on (letter 1, letter 3, length tally) render as the same
        // tile, which is a language bug rather than an art one — the fix is a synonym.
        Map<String, ResourceLocation> bySignature = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> file : files.entrySet()) {
            ResourceLocation id = file.getKey();
            Glyph glyph;
            try {
                glyph = Glyph.CODEC.parse(JsonOps.INSTANCE, file.getValue())
                        .getOrThrow(false, error -> {
                            throw new IllegalArgumentException(error);
                        });
            } catch (RuntimeException e) {
                Epigraphy.LOGGER.error("Skipping glyph {}: {}", id, e.getMessage());
                continue;
            }

            String problem = validate(glyph);
            if (problem != null) {
                Epigraphy.LOGGER.error("Skipping glyph {}: {}", id, problem);
                continue;
            }

            ResourceLocation clash = bySignature.putIfAbsent(glyph.signature(), id);
            if (clash != null) {
                Epigraphy.LOGGER.error(
                        "Skipping glyph {}: it renders identically to {} — both mark {}. "
                                + "This is a language bug, not an art bug: replace one lemma with a synonym, "
                                + "or force the tile with an explicit \"mark\".",
                        id, clash, glyph.signature());
                continue;
            }

            loaded.put(id, glyph);
        }

        this.glyphs = Map.copyOf(loaded);
        reportDanglingReferences(loaded);
        Epigraphy.LOGGER.info("Loaded {} glyphs.", loaded.size());
    }

    /** @return a description of the first problem found, or {@code null} if the glyph is sound. */
    private static String validate(Glyph glyph) {
        String normalised = glyph.normalisedLemma();
        if (normalised.length() < 2) {
            return "lemma \"" + glyph.lemma() + "\" normalises to \"" + normalised
                    + "\", which is too short to mark a tile — it needs at least two letters of the classical 21.";
        }
        if (!Lemmas.isRenderable(normalised)) {
            return "lemma \"" + glyph.lemma() + "\" contains characters outside the classical 21 ("
                    + Lemmas.ALPHABET + ").";
        }
        if (glyph.mark().isPresent()) {
            String mark = Lemmas.normalise(glyph.mark().get());
            if (mark.length() != 2) {
                return "mark \"" + glyph.mark().get() + "\" must be exactly two letters of the classical 21.";
            }
        }
        if (glyph.sightings().isPresent() && glyph.sightings().get() < 1) {
            return "sightings_to_learn must be at least 1.";
        }
        if (glyph.gloss().isBlank()) {
            return "gloss is blank — it is what the player reads once the glyph is learned.";
        }
        return null;
    }

    /**
     * An {@code opposite} pointing at a glyph nobody loaded leaves an axis with one pole,
     * which costs the player the free half-lesson the axis system exists to give. Worth a
     * warning; not worth dropping the glyph.
     */
    private static void reportDanglingReferences(Map<ResourceLocation, Glyph> loaded) {
        loaded.forEach((id, glyph) -> glyph.opposite().ifPresent(opposite -> {
            if (!loaded.containsKey(opposite)) {
                Epigraphy.LOGGER.warn("Glyph {} names {} as its opposite, but no such glyph is loaded.",
                        id, opposite);
            }
        }));

        // Items are registered from the shipped lexicon at construction, long before any
        // datapack is read. A pack that removes one leaves an item with nothing behind it.
        Lexicon.byId().keySet().forEach(id -> {
            if (!loaded.containsKey(id)) {
                Epigraphy.LOGGER.warn("Shipped glyph {} has a registered item but was not loaded from data — "
                        + "a datapack has removed it. The item will read as an uninscribed tablet.", id);
            }
        });
    }
}

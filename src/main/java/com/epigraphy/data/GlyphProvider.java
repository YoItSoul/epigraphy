package com.epigraphy.data;

import com.epigraphy.rune.Glyph;
import com.epigraphy.rune.GlyphManager;
import com.epigraphy.rune.Lexicon;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Writes the shipped lexicon out to {@code data/epigraphy/glyphs/*.json}.
 *
 * <p>{@link Lexicon} has to exist in code, because items are registered at mod
 * construction and glyphs are not loaded until a datapack is read. Generating the JSON
 * from it — rather than maintaining both by hand — is what stops the two from drifting:
 * there is exactly one place a glyph is written down, and 52 items and 52 files both
 * come out of it.
 */
public class GlyphProvider implements DataProvider {

    private final PackOutput.PathProvider path;

    public GlyphProvider(PackOutput output) {
        this.path = output.createPathProvider(PackOutput.Target.DATA_PACK, GlyphManager.DIRECTORY);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> written = new ArrayList<>();
        for (Lexicon.Entry entry : Lexicon.entries()) {
            ResourceLocation id = entry.id();
            JsonElement json = Glyph.CODEC.encodeStart(JsonOps.INSTANCE, entry.glyph())
                    .getOrThrow(false, error -> {
                        throw new IllegalStateException("Cannot serialise shipped glyph " + id + ": " + error);
                    });
            Path file = path.json(id);
            written.add(DataProvider.saveStable(cache, json, file));
        }
        return CompletableFuture.allOf(written.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Epigraphy glyphs";
    }
}

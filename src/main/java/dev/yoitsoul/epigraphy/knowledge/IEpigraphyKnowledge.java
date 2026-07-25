package dev.yoitsoul.epigraphy.knowledge;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;

/**
 * Per-player knowledge capability (see docs/DESIGN.md §2.1-2.2). Two independent sets: glyph
 * tier progress, and mastered ritual recipe ids (the JEI unlock signal).
 */
public interface IEpigraphyKnowledge {

    KnowledgeTier getGlyphTier(ResourceLocation glyphId);

    void setGlyphTier(ResourceLocation glyphId, KnowledgeTier tier);

    boolean isRecipeMastered(ResourceLocation recipeId);

    void setRecipeMastered(ResourceLocation recipeId);

    Map<ResourceLocation, KnowledgeTier> getAllGlyphTiers();

    Set<ResourceLocation> getAllMasteredRecipes();
}

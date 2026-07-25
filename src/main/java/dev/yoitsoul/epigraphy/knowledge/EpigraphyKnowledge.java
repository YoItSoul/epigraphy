package dev.yoitsoul.epigraphy.knowledge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default {@link IEpigraphyKnowledge} implementation, attached to {@code Player} via a
 * capability (see docs/DESIGN.md §2.2). Server-authoritative; the client only ever holds a
 * mirrored read-only copy synced down after a mutation.
 */
public class EpigraphyKnowledge implements IEpigraphyKnowledge, INBTSerializable<CompoundTag> {

    private final Map<ResourceLocation, KnowledgeTier> glyphTiers = new HashMap<>();
    private final Set<ResourceLocation> masteredRecipes = new HashSet<>();

    @Override
    public KnowledgeTier getGlyphTier(ResourceLocation glyphId) {
        return glyphTiers.get(glyphId);
    }

    @Override
    public void setGlyphTier(ResourceLocation glyphId, KnowledgeTier tier) {
        KnowledgeTier current = glyphTiers.get(glyphId);
        if (current == null || tier.ordinal() > current.ordinal()) {
            glyphTiers.put(glyphId, tier);
        }
    }

    @Override
    public boolean isRecipeMastered(ResourceLocation recipeId) {
        return masteredRecipes.contains(recipeId);
    }

    @Override
    public void setRecipeMastered(ResourceLocation recipeId) {
        masteredRecipes.add(recipeId);
    }

    @Override
    public Map<ResourceLocation, KnowledgeTier> getAllGlyphTiers() {
        return Map.copyOf(glyphTiers);
    }

    @Override
    public Set<ResourceLocation> getAllMasteredRecipes() {
        return Set.copyOf(masteredRecipes);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        CompoundTag glyphsTag = new CompoundTag();
        glyphTiers.forEach((id, tier) -> glyphsTag.putString(id.toString(), tier.name()));
        tag.put("glyphs", glyphsTag);

        ListTag recipesTag = new ListTag();
        masteredRecipes.forEach(id -> recipesTag.add(StringTag.valueOf(id.toString())));
        tag.put("masteredRecipes", recipesTag);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        glyphTiers.clear();
        CompoundTag glyphsTag = tag.getCompound("glyphs");
        for (String key : glyphsTag.getAllKeys()) {
            glyphTiers.put(new ResourceLocation(key), KnowledgeTier.valueOf(glyphsTag.getString(key)));
        }

        masteredRecipes.clear();
        ListTag recipesTag = tag.getList("masteredRecipes", StringTag.TAG_STRING);
        for (int i = 0; i < recipesTag.size(); i++) {
            masteredRecipes.add(new ResourceLocation(recipesTag.getString(i)));
        }
    }
}

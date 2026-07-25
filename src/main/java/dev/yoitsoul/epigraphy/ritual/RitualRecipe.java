package dev.yoitsoul.epigraphy.ritual;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * A ritual recipe (schema: docs/DESIGN.md §5.3). Matching runs against the altar's gathered
 * pedestal/ground items, fluid tank, and {@link RitualCondition}s — never against a vanilla
 * crafting grid, so this only implements {@link Recipe} for registry/JEI integration purposes.
 */
public record RitualRecipe(
        ResourceLocation id,
        List<ResourceLocation> sentence,
        List<Ingredient> pedestalItems,
        List<Ingredient> groundItems,
        FluidStack fluid,
        List<RitualCondition> conditions,
        ItemStack result,
        int scanRadius
) implements Recipe<Container> {

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, net.minecraft.core.RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> all = NonNullList.create();
        all.addAll(pedestalItems);
        all.addAll(groundItems);
        return all;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.RITUAL_TYPE.get();
    }
}

package dev.yoitsoul.epigraphy.ritual;

import dev.yoitsoul.epigraphy.Epigraphy;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Epigraphy.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Epigraphy.MOD_ID);

    public static final RegistryObject<RecipeSerializer<RitualRecipe>> RITUAL_SERIALIZER =
            RECIPE_SERIALIZERS.register("ritual", RitualRecipeSerializer::new);

    public static final RegistryObject<RecipeType<RitualRecipe>> RITUAL_TYPE =
            RECIPE_TYPES.register("ritual", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Epigraphy.MOD_ID + ":ritual";
                }
            });

    private ModRecipeTypes() {
    }
}

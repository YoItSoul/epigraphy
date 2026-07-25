package dev.yoitsoul.epigraphy.ritual;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses {@code data/<ns>/recipes/<id>.json} entries with {@code "type": "epigraphy:ritual"}
 * into {@link RitualRecipe}s (schema: docs/DESIGN.md §5.3).
 */
public class RitualRecipeSerializer implements RecipeSerializer<RitualRecipe> {

    @Override
    public RitualRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        List<ResourceLocation> sentence = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "sentence")) {
            sentence.add(new ResourceLocation(element.getAsString()));
        }

        List<Ingredient> pedestalItems = readIngredients(json, "pedestal_items");
        List<Ingredient> groundItems = readIngredients(json, "ground_items");

        FluidStack fluid = FluidStack.EMPTY;
        if (json.has("fluid") && !json.get("fluid").isJsonNull()) {
            // TODO: parse a fluid tag/id + amount into a FluidStack once fluid content is authored.
        }

        List<RitualCondition> conditions = new ArrayList<>();
        if (json.has("conditions")) {
            for (JsonElement element : GsonHelper.getAsJsonArray(json, "conditions")) {
                // TODO: dispatch on "type" to the concrete RitualCondition implementations
                // (weather/time/dimension/moon_phase/sky/fluid_adjacent — see docs/DESIGN.md §5.3).
            }
        }

        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        int scanRadius = GsonHelper.getAsInt(json, "scan_radius", 3);

        return new RitualRecipe(recipeId, sentence, pedestalItems, groundItems, fluid, conditions, result, scanRadius);
    }

    private static List<Ingredient> readIngredients(JsonObject json, String key) {
        List<Ingredient> ingredients = new ArrayList<>();
        if (!json.has(key)) {
            return ingredients;
        }
        for (JsonElement element : GsonHelper.getAsJsonArray(json, key)) {
            JsonObject entry = element.getAsJsonObject();
            Ingredient ingredient = Ingredient.fromJson(entry.get("ingredient"));
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    @Override
    public RitualRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        List<ResourceLocation> sentence = buffer.readList(FriendlyByteBuf::readResourceLocation);
        List<Ingredient> pedestalItems = buffer.readList(Ingredient::fromNetwork);
        List<Ingredient> groundItems = buffer.readList(Ingredient::fromNetwork);
        FluidStack fluid = FluidStack.readFromPacket(buffer);
        ItemStack result = buffer.readItem();
        int scanRadius = buffer.readVarInt();
        return new RitualRecipe(recipeId, sentence, pedestalItems, groundItems, fluid, List.of(), result, scanRadius);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, RitualRecipe recipe) {
        buffer.writeCollection(recipe.sentence(), FriendlyByteBuf::writeResourceLocation);
        buffer.writeCollection(recipe.pedestalItems(), (buf, ingredient) -> ingredient.toNetwork(buf));
        buffer.writeCollection(recipe.groundItems(), (buf, ingredient) -> ingredient.toNetwork(buf));
        recipe.fluid().writeToPacket(buffer);
        buffer.writeItem(recipe.result());
        buffer.writeVarInt(recipe.scanRadius());
    }
}

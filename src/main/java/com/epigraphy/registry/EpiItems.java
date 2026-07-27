package com.epigraphy.registry;

import com.epigraphy.Epigraphy;
import com.epigraphy.item.GlyphItem;
import com.epigraphy.rune.Lexicon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Every item the mod registers. */
public final class EpiItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Epigraphy.MODID);

    /**
     * The unbound tablet. Blank until inscribed, and the only way a glyph added by a
     * datapack — which cannot have had an item registered for it — gets one (D13).
     */
    public static final RegistryObject<Item> GLYPH =
            ITEMS.register("glyph", () -> new GlyphItem(new Item.Properties(), null));

    /**
     * One tablet per shipped glyph, in lexicon order, so the two poles of an axis sit
     * next to each other everywhere they are listed.
     */
    private static final Map<ResourceLocation, RegistryObject<Item>> GLYPH_ITEMS = new LinkedHashMap<>();

    static {
        for (Lexicon.Entry entry : Lexicon.entries()) {
            ResourceLocation glyphId = entry.id();
            GLYPH_ITEMS.put(glyphId, ITEMS.register(entry.itemPath(),
                    () -> new GlyphItem(new Item.Properties(), glyphId)));
        }
    }

    private EpiItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    /** The tablet bound to a glyph, or empty if that glyph has no item of its own. */
    public static Optional<Item> glyphItem(ResourceLocation glyphId) {
        return Optional.ofNullable(GLYPH_ITEMS.get(glyphId)).map(RegistryObject::get);
    }

    /** Glyph id → its bound tablet, in lexicon order. */
    public static Map<ResourceLocation, RegistryObject<Item>> glyphItems() {
        return Collections.unmodifiableMap(GLYPH_ITEMS);
    }

    /** The bound tablets in lexicon order. */
    public static List<RegistryObject<Item>> glyphItemsInOrder() {
        return List.copyOf(GLYPH_ITEMS.values());
    }
}

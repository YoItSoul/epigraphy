package dev.yoitsoul.epigraphy.item;

import dev.yoitsoul.epigraphy.Epigraphy;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Epigraphy.MOD_ID);

    public static final RegistryObject<Item> CODEX =
            ITEMS.register("codex", () -> new CodexItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ASTROLABE =
            ITEMS.register("astrolabe", () -> new AstrolabeItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RUBBING_KIT =
            ITEMS.register("rubbing_kit", () -> new RubbingKitItem(new Item.Properties().durability(16)));

    public static final RegistryObject<Item> GLYPH_RUBBING =
            ITEMS.register("glyph_rubbing", () -> new GlyphRubbingItem(new Item.Properties()));

    public static final RegistryObject<Item> GLYPH_TABLET =
            ITEMS.register("glyph_tablet", () -> new GlyphTabletItem(new Item.Properties()));

    public static final RegistryObject<Item> CHAOS_INGOT =
            ITEMS.register("chaos_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ORDER_INGOT =
            ITEMS.register("order_ingot", () -> new Item(new Item.Properties()));

    private ModItems() {
    }
}

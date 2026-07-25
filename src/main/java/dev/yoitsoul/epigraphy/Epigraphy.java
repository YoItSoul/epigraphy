package dev.yoitsoul.epigraphy;

import dev.yoitsoul.epigraphy.block.ModBlocks;
import dev.yoitsoul.epigraphy.glyph.GlyphManager;
import dev.yoitsoul.epigraphy.item.ModItems;
import dev.yoitsoul.epigraphy.ritual.ModRecipeTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Epigraphy — an original glyph-discovery and ritual magic mod for Minecraft 1.20.1 Forge,
 * built for the Souls of Avarice modpack. See docs/DESIGN.md for the full design.
 */
@Mod(Epigraphy.MOD_ID)
public class Epigraphy {

    public static final String MOD_ID = "epigraphy";

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(ForgeRegistries.CREATIVE_MODE_TABS, MOD_ID);

    public static final RegistryObject<CreativeModeTab> EPIGRAPHY_TAB = CREATIVE_TABS.register(
            "epigraphy_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.CODEX.get()))
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup.epigraphy"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CODEX.get());
                        output.accept(ModItems.ASTROLABE.get());
                        output.accept(ModItems.RUBBING_KIT.get());
                        output.accept(ModItems.GLYPH_RUBBING.get());
                        output.accept(ModItems.GLYPH_TABLET.get());
                        output.accept(ModItems.CHAOS_INGOT.get());
                        output.accept(ModBlocks.GLYPH_ALTAR_ITEM.get());
                        output.accept(ModBlocks.GLYPH_PEDESTAL_ITEM.get());
                        output.accept(ModBlocks.CARVED_GLYPH_ITEM.get());
                    })
                    .build());

    public Epigraphy() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlocks.BLOCK_ENTITIES.register(modEventBus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.addListener(this::registerReloadListeners);
    }

    private void commonSetup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        // Capability + network channel registration happens here once implemented.
    }

    private void registerReloadListeners(final AddReloadListenerEvent event) {
        event.addListener(new GlyphManager());
    }
}

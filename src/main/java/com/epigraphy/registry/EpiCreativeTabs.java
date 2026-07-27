package com.epigraphy.registry;

import com.epigraphy.Epigraphy;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class EpiCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Epigraphy.MODID);

    /**
     * The glyph tablets, listed in lexicon order so each axis reads as its two opposed
     * poles side by side rather than as fifty-two unrelated symbols.
     */
    public static final RegistryObject<CreativeModeTab> RUNES = TABS.register("runes", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.epigraphy.runes"))
                    .icon(() -> new ItemStack(EpiItems.GLYPH.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(EpiItems.GLYPH.get());
                        EpiItems.glyphItemsInOrder().forEach(item -> output.accept(item.get()));
                    })
                    .build());

    private EpiCreativeTabs() {
    }

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }
}

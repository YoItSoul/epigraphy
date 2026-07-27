package com.epigraphy;

import com.epigraphy.command.EpigraphyCommand;
import com.epigraphy.registry.EpiCreativeTabs;
import com.epigraphy.registry.EpiItems;
import com.epigraphy.rune.GlyphManager;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;

/**
 * Epigraphy — a magic mod about <b>reading the world</b>.
 *
 * <p>Discover ancient glyphs hidden in the stars, ruins and structures; work out the
 * rune words they form; and perform infusion rituals whose recipes read like a language
 * you had to learn. Design docs live in {@code docs/}; {@code docs/DECISIONS.md} is the
 * source of truth where they disagree.
 */
@Mod(Epigraphy.MODID)
public class Epigraphy {

    public static final String MODID = "epigraphy";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final GlyphManager GLYPHS = new GlyphManager();

    public Epigraphy() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        EpiItems.register(modBus);
        EpiCreativeTabs.register(modBus);

        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    /** {@code epigraphy:<path>}. */
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    /** The server-side glyph registry, reloaded with datapacks. */
    public static GlyphManager glyphs() {
        return GLYPHS;
    }

    private void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(GLYPHS);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        EpigraphyCommand.register(event.getDispatcher());
    }
}

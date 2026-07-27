package com.epigraphy.data;

import com.epigraphy.Epigraphy;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Epigraphy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EpiDataGenerators {

    private EpiDataGenerators() {
    }

    @SubscribeEvent
    public static void gather(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new GlyphProvider(output));
        generator.addProvider(event.includeClient(), new EpiItemModelProvider(output, helper));
        generator.addProvider(event.includeClient(), new EpiLanguageProvider(output));
    }
}

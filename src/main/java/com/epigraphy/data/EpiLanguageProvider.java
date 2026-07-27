package com.epigraphy.data;

import com.epigraphy.Epigraphy;
import com.epigraphy.rune.Lexicon;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class EpiLanguageProvider extends LanguageProvider {

    public EpiLanguageProvider(PackOutput output) {
        super(output, Epigraphy.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.epigraphy.runes", "Epigraphy: Runes");

        add("item.epigraphy.glyph", "Uninscribed Tablet");
        add("item.epigraphy.glyph.named", "Tablet of %s");
        for (Lexicon.Entry entry : Lexicon.entries()) {
            add("item.epigraphy." + entry.itemPath(), "Tablet of " + entry.glyph().lemma());
        }

        add("tooltip.epigraphy.glyph.uninscribed", "Bare stone. Nothing has been cut into it.");
        add("tooltip.epigraphy.glyph.unlearned", "???");

        add("commands.epigraphy.runes.header", "%s glyphs loaded");
        add("commands.epigraphy.unknown_glyph", "No such glyph is loaded.");
    }
}

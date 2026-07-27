package com.epigraphy.data;

import com.epigraphy.Epigraphy;
import com.epigraphy.rune.Lexicon;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * A flat item model per glyph tablet.
 *
 * <p>Every one points at the <b>blank tile</b> — bare stone, no cuts
 * ({@code docs/GLYPH_SPEC.md} §1.1) — because the procedural renderer that cuts the two
 * letter-forms and the tally foot is the other half of Phase 1 and does not exist yet.
 * The models are per-item rather than shared so that landing the renderer is a texture
 * change and not a model rewrite.
 */
public class EpiItemModelProvider extends ItemModelProvider {

    public EpiItemModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, Epigraphy.MODID, helper);
    }

    @Override
    protected void registerModels() {
        blankTile("glyph");
        for (Lexicon.Entry entry : Lexicon.entries()) {
            blankTile(entry.itemPath());
        }
    }

    private void blankTile(String name) {
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/glyph_blank"));
    }
}

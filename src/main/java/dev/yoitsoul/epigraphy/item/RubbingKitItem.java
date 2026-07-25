package dev.yoitsoul.epigraphy.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

/**
 * Chalk-and-paper tool used on {@code block_carved_glyph} to produce a {@link GlyphRubbingItem}
 * and grant the SEEN knowledge tier (see docs/DESIGN.md §3.2).
 */
public class RubbingKitItem extends Item {

    public RubbingKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // TODO: check for a carved-glyph block entity at context.getClickedPos(), consume paper,
        // grant SEEN on the referenced glyph, and produce a GlyphRubbingItem stamped with its id.
        return InteractionResult.PASS;
    }
}

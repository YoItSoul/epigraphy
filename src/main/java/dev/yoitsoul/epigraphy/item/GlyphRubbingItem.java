package dev.yoitsoul.epigraphy.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A rubbed copy of a single glyph (see docs/DESIGN.md §3.2). Studying it in hand promotes the
 * referenced glyph from SEEN to LEARNED. Carries only a glyph id, so it is safe to trade.
 */
public class GlyphRubbingItem extends Item {

    public GlyphRubbingItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // TODO: on server, start the "study" hold-duration timer; on completion, promote the
        // glyph referenced by this stack's data component from SEEN to LEARNED.
        return InteractionResultHolder.pass(stack);
    }
}

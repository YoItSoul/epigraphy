package dev.yoitsoul.epigraphy.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A single item covering every tablet configured in {@code data/*}/epigraphy/tablets/*.json
 * (see docs/DESIGN.md §3.3 and §5.2). Which glyph and tier it represents is stored on the stack.
 */
public class GlyphTabletItem extends Item {

    public GlyphTabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // TODO: read this stack's tablet id, look up its tablets/*.json entry, grant the
        // configured knowledge tier (seen|learned) on its referenced glyph, then consume the stack.
        return InteractionResultHolder.pass(stack);
    }
}

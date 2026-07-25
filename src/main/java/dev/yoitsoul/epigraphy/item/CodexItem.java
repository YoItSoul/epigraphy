package dev.yoitsoul.epigraphy.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The player's glyph journal (see docs/DESIGN.md §2.3). Opens a client-only screen backed by
 * the player's synced knowledge cache; holds no state of its own.
 */
public class CodexItem extends Item {

    public CodexItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            // TODO: open the Codex screen (client-side only, reads ClientKnowledgeCache).
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

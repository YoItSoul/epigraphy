package dev.yoitsoul.epigraphy.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Star-viewing tool (see docs/DESIGN.md §3.1). Opens a 2D client-only overlay over a procedural
 * starfield; never touches the world skybox.
 */
public class AstrolabeItem extends Item {

    public AstrolabeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            // TODO: open the Astrolabe overlay screen and begin the constellation trace minigame.
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

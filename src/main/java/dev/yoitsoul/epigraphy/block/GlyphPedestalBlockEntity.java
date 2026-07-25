package dev.yoitsoul.epigraphy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GlyphPedestalBlockEntity extends BlockEntity {

    private ItemStack heldItem = ItemStack.EMPTY;

    public GlyphPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.GLYPH_PEDESTAL_BE.get(), pos, state);
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public void interact(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (heldItem.isEmpty() && !held.isEmpty()) {
            heldItem = held.split(1);
            setChanged();
        } else if (!heldItem.isEmpty() && held.isEmpty()) {
            player.setItemInHand(hand, heldItem);
            heldItem = ItemStack.EMPTY;
            setChanged();
        }
    }
}

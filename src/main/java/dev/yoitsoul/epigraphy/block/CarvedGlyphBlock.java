package dev.yoitsoul.epigraphy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Wall-mounted decorative block referencing a single glyph, placed in generated glyph ruins
 * (see docs/DESIGN.md §3.2). Interacted with via {@code RubbingKitItem}, not directly.
 */
public class CarvedGlyphBlock extends Block implements EntityBlock {

    public CarvedGlyphBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CarvedGlyphBlockEntity(pos, state);
    }
}

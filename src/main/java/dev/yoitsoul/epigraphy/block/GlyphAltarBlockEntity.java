package dev.yoitsoul.epigraphy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * See docs/DESIGN.md §4.2 for the full resolution flow this will implement: gather pedestals,
 * ground items and fluid within {@code scan_radius}, check environmental conditions, match
 * against registered {@code RitualRecipe}s, then consume/output and update player knowledge.
 */
public class GlyphAltarBlockEntity extends BlockEntity {

    private static final int DEFAULT_SCAN_RADIUS = 3;

    public GlyphAltarBlockEntity(BlockPos pos, BlockState state) {
        super(dev.yoitsoul.epigraphy.block.ModBlocks.GLYPH_ALTAR_BE.get(), pos, state);
    }

    public void attemptRitual(Player player) {
        // TODO: gather pedestals/ground items/fluid/conditions within DEFAULT_SCAN_RADIUS,
        // match a RitualRecipe, resolve it, and update the triggering player's knowledge.
    }
}

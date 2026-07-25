package dev.yoitsoul.epigraphy.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * One environmental check a ritual requires (see docs/DESIGN.md §5.3). Each condition glyph in
 * docs/DESIGN.md §1.5 maps to one {@code type} value here, decoupled by design so new conditions
 * can be added without a glyph existing yet, and vice versa.
 */
public interface RitualCondition {

    boolean test(Level level, BlockPos altarPos);

    String type();
}

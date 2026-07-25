package dev.yoitsoul.epigraphy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CarvedGlyphBlockEntity extends BlockEntity {

    @Nullable
    private ResourceLocation glyphId;

    public CarvedGlyphBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CARVED_GLYPH_BE.get(), pos, state);
    }

    @Nullable
    public ResourceLocation getGlyphId() {
        return glyphId;
    }

    public void setGlyphId(ResourceLocation glyphId) {
        this.glyphId = glyphId;
        setChanged();
    }
}

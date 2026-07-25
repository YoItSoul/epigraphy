package dev.yoitsoul.epigraphy.block;

import dev.yoitsoul.epigraphy.Epigraphy;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Epigraphy.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Epigraphy.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Epigraphy.MOD_ID);

    public static final RegistryObject<Block> GLYPH_ALTAR = BLOCKS.register("glyph_altar",
            () -> new GlyphAltarBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(5.0f, 1200.0f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> GLYPH_ALTAR_ITEM = ITEMS.register("glyph_altar",
            () -> new BlockItem(GLYPH_ALTAR.get(), new Item.Properties()));

    public static final RegistryObject<Block> GLYPH_PEDESTAL = BLOCKS.register("glyph_pedestal",
            () -> new GlyphPedestalBlock(Block.Properties.of().mapColor(MapColor.STONE).strength(3.0f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> GLYPH_PEDESTAL_ITEM = ITEMS.register("glyph_pedestal",
            () -> new BlockItem(GLYPH_PEDESTAL.get(), new Item.Properties()));

    public static final RegistryObject<Block> CARVED_GLYPH = BLOCKS.register("carved_glyph",
            () -> new CarvedGlyphBlock(Block.Properties.of().mapColor(MapColor.STONE).strength(3.0f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> CARVED_GLYPH_ITEM = ITEMS.register("carved_glyph",
            () -> new BlockItem(CARVED_GLYPH.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<GlyphAltarBlockEntity>> GLYPH_ALTAR_BE =
            BLOCK_ENTITIES.register("glyph_altar", () -> BlockEntityType.Builder.of(
                    GlyphAltarBlockEntity::new, GLYPH_ALTAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<GlyphPedestalBlockEntity>> GLYPH_PEDESTAL_BE =
            BLOCK_ENTITIES.register("glyph_pedestal", () -> BlockEntityType.Builder.of(
                    GlyphPedestalBlockEntity::new, GLYPH_PEDESTAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<CarvedGlyphBlockEntity>> CARVED_GLYPH_BE =
            BLOCK_ENTITIES.register("carved_glyph", () -> BlockEntityType.Builder.of(
                    CarvedGlyphBlockEntity::new, CARVED_GLYPH.get()).build(null));

    private ModBlocks() {
    }
}

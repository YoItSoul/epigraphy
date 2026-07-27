package com.epigraphy.item;

import com.epigraphy.Epigraphy;
import com.epigraphy.rune.Glyph;
import com.epigraphy.rune.GlyphTier;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * An <b>inscribed tablet</b>: a stone tile carrying exactly one glyph
 * ({@code docs/DISCOVERY.md} §1.3). Studying one at a Lectern of Study is worth a
 * sighting, which is how a player who fights makes progress alongside one who explores.
 *
 * <p>A tablet learns its glyph one of two ways, and the distinction is invisible in play:
 * <ul>
 *   <li><b>Bound</b> — one item per shipped glyph, {@code epigraphy:glyph_ignis} and its
 *       fifty-one siblings, so worldgen, loot tables and recipes can name a concrete item
 *       rather than reaching into NBT.</li>
 *   <li><b>Unbound</b> — {@code epigraphy:glyph}, which reads its glyph from NBT. Items
 *       are registered at mod construction and glyphs are loaded from datapacks
 *       afterwards (D13), so this is how a glyph nobody compiled against still gets a
 *       tablet.</li>
 * </ul>
 *
 * <p>A tablet with no glyph at all is not an error: bare stone with no cuts is the
 * uninscribed tablet, one of the three jobs the blank tile does
 * ({@code docs/GLYPH_SPEC.md} §1.1).
 */
public class GlyphItem extends Item {

    /** NBT key on the unbound variant. Ignored when the item is bound to a glyph. */
    public static final String TAG_GLYPH = "Glyph";

    @Nullable
    private final ResourceLocation bound;

    public GlyphItem(Properties properties, @Nullable ResourceLocation bound) {
        super(properties);
        this.bound = bound;
    }

    /** The glyph this stack carries, from its binding or its NBT. */
    public Optional<ResourceLocation> glyphId(ItemStack stack) {
        if (bound != null) {
            return Optional.of(bound);
        }
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_GLYPH)) {
            return Optional.empty();
        }
        return Optional.ofNullable(ResourceLocation.tryParse(tag.getString(TAG_GLYPH)));
    }

    /** The glyph an item type always carries, or empty for the unbound variant. */
    public Optional<ResourceLocation> boundGlyph() {
        return Optional.ofNullable(bound);
    }

    /** Writes a glyph onto an unbound tablet. A bound tablet already knows its own. */
    public static ItemStack inscribe(ItemStack stack, ResourceLocation glyph) {
        stack.getOrCreateTag().putString(TAG_GLYPH, glyph.toString());
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        // A bound tablet has a name of its own; only the unbound one has to build one.
        // The lemma is the glyph's identity rather than its meaning, so naming a tablet
        // after it gives nothing away that the tile itself does not already show.
        if (bound == null) {
            Optional<Glyph> glyph = resolve(stack);
            if (glyph.isPresent()) {
                return Component.translatable("item.epigraphy.glyph.named", glyph.get().lemma());
            }
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        Optional<Glyph> resolved = resolve(stack);
        if (resolved.isEmpty()) {
            lines.add(Component.translatable("tooltip.epigraphy.glyph.uninscribed")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        Glyph glyph = resolved.get();
        GlyphTier tier = tierOf(stack);

        if (!tier.isAtLeast(GlyphTier.LEARNED)) {
            // Seen but not taken down: the tile shows, the meaning does not.
            lines.add(Component.translatable("tooltip.epigraphy.glyph.unlearned")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        lines.add(Component.literal(glyph.gloss()).withStyle(ChatFormatting.GRAY));
        glyph.description().ifPresent(text ->
                lines.add(Component.literal(text).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)));

        if (flag.isAdvanced()) {
            glyphId(stack).ifPresent(id ->
                    lines.add(Component.literal(id.toString()).withStyle(ChatFormatting.DARK_GRAY)));
        }
    }

    private Optional<Glyph> resolve(ItemStack stack) {
        return glyphId(stack).flatMap(id -> Epigraphy.glyphs().get(id));
    }

    /**
     * How much the holder knows about this tablet's glyph.
     *
     * <p>Knowledge is per-player and permanent, held in a capability that arrives in
     * Phase 2 ({@code docs/ROADMAP.md} §4). Until then every glyph reads as learned, so
     * tablets are legible while the discovery loop is being built. This is the single
     * seam that has to change — everything downstream already branches on the tier.
     */
    private GlyphTier tierOf(ItemStack stack) {
        return GlyphTier.LEARNED;
    }
}

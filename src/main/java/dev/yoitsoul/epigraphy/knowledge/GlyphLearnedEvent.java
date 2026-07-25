package dev.yoitsoul.epigraphy.knowledge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a glyph crosses into {@link KnowledgeTier#LEARNED} for a player. The SOA quest
 * system can listen for this to advance stages (see docs/DESIGN.md §2.2).
 */
public class GlyphLearnedEvent extends Event {

    private final Player player;
    private final ResourceLocation glyphId;

    public GlyphLearnedEvent(Player player, ResourceLocation glyphId) {
        this.player = player;
        this.glyphId = glyphId;
    }

    public Player getPlayer() {
        return player;
    }

    public ResourceLocation getGlyphId() {
        return glyphId;
    }
}

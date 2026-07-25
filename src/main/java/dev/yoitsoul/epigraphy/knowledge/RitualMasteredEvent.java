package dev.yoitsoul.epigraphy.knowledge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a ritual recipe is mastered for a player — the trigger for both the SOA quest
 * system and the JEI runtime unhide (see docs/DESIGN.md §2.2 and §6).
 */
public class RitualMasteredEvent extends Event {

    private final Player player;
    private final ResourceLocation recipeId;

    public RitualMasteredEvent(Player player, ResourceLocation recipeId) {
        this.player = player;
        this.recipeId = recipeId;
    }

    public Player getPlayer() {
        return player;
    }

    public ResourceLocation getRecipeId() {
        return recipeId;
    }
}

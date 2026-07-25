package dev.yoitsoul.epigraphy.knowledge;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class EpigraphyCapabilities {

    public static final net.minecraftforge.common.capabilities.Capability<IEpigraphyKnowledge> KNOWLEDGE =
            CapabilityManager.get(new CapabilityToken<>() {
            });

    private EpigraphyCapabilities() {
    }
}

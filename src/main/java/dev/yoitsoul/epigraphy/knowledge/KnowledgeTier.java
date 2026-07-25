package dev.yoitsoul.epigraphy.knowledge;

/**
 * Per-glyph knowledge progression (see docs/DESIGN.md §2.1). UNSEEN is never stored explicitly —
 * its absence from a player's glyph map means UNSEEN.
 */
public enum KnowledgeTier {
    SEEN,
    LEARNED
}

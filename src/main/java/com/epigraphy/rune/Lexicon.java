package com.epigraphy.rune;

import com.epigraphy.Epigraphy;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The shipped lexicon: <b>26 axes, 52 runes</b> ({@code docs/RUNES.md} §2).
 *
 * <p>Every rune has an opposite. The lexicon is not a list of words but a list of
 * <em>axes</em> — each one a dimension of the world with two opposed poles — which is
 * what lets 52 runes describe far more than 52 things, and what makes the language
 * cheap to teach: learn one pole and you have half-learned the other.
 *
 * <h2>Why this is Java and not JSON</h2>
 * Glyphs are datapack-defined (D13) and {@link GlyphManager} loads them from
 * {@code data/<ns>/glyphs/}. But items must be registered at mod construction, long
 * before any datapack is read, so an item per glyph needs the shipped lexicon to exist
 * in code. Rather than keep two copies in step by hand, <b>this class is the source of
 * truth and the JSON is generated from it</b> at {@code runData}. A datapack may still
 * override any of these files, add glyphs of its own, or replace the lot.
 *
 * <p>Frame glyphs ({@code FIAT}, {@code MERSIO}, {@code TACTVS}) are deliberately absent:
 * D11 is a leaning, not a decision, and minting them here would prejudge it.
 */
public final class Lexicon {

    /** The ✦ mark: this glyph may head a rune word ({@code docs/RUNES.md} §3.1). */
    private static final boolean HEAD = true;
    /** No ✦: this glyph may only qualify a head, never be one. */
    private static final boolean QUALIFIER = false;

    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Map<ResourceLocation, Glyph> BY_ID = new LinkedHashMap<>();

    private Lexicon() {
    }

    /** One shipped glyph, paired with the id it is registered and generated under. */
    public record Entry(ResourceLocation id, Glyph glyph) {
        /** The registry path of this glyph's item, e.g. {@code glyph_ignis}. */
        public String itemPath() {
            return "glyph_" + id.getPath();
        }
    }

    public static List<Entry> entries() {
        return Collections.unmodifiableList(ENTRIES);
    }

    public static Map<ResourceLocation, Glyph> byId() {
        return Collections.unmodifiableMap(BY_ID);
    }

    public static int size() {
        return ENTRIES.size();
    }

    // ------------------------------------------------------------------
    // The World
    // ------------------------------------------------------------------
    static {
        axis("heavens", GlyphCategory.WORLD, GlyphRarity.UNCOMMON,
                pole("caelvm", "CAELVM", "Heavens", HEAD,
                        "The above, and everything written across it."),
                pole("infernvs", "INFERNVS", "Hells", HEAD,
                        "The fire below, which was burning before anyone came to see it."));

        axis("extent", GlyphCategory.WORLD, GlyphRarity.RARE,
                pole("origo", "ORIGO", "Source", HEAD,
                        "The middle: where a thing began, and where it is at home."),
                pole("finis", "FINIS", "End", HEAD,
                        "The edge, past which is the beyond."));

        axis("light", GlyphCategory.WORLD, GlyphRarity.COMMON,
                pole("lvx", "LVX", "Light", QUALIFIER,
                        "What falls on a thing and lets it be read."),
                pole("tenebrae", "TENEBRAE", "Darkness", QUALIFIER,
                        "Not the absence of light but the presence of its opposite."));

        axis("pattern", GlyphCategory.WORLD, GlyphRarity.RARE,
                pole("ordo", "ORDO", "Order", QUALIFIER,
                        "Law, pattern, the arrangement a thing keeps when left alone."),
                pole("chaos", "CHAOS", "Ruin", QUALIFIER,
                        "The unmade. What order looks like once it has stopped holding."));

        axis("luminary", GlyphCategory.WORLD, GlyphRarity.COMMON,
                pole("sol", "SOL", "Sun", HEAD,
                        "The day's one light, and the hour everything is measured from."),
                pole("lvna", "LVNA", "Moon", HEAD,
                        "The night's light, which is never twice the same shape."));
    }

    // ------------------------------------------------------------------
    // The Elements
    // ------------------------------------------------------------------
    static {
        axis("heat", GlyphCategory.ELEMENT, GlyphRarity.COMMON,
                pole("ignis", "IGNIS", "Fire", HEAD,
                        "Heat, and the thing heat makes of what it touches."),
                pole("gelv", "GELV", "Ice", HEAD,
                        "Cold held still long enough to become a solid."));

        axis("state", GlyphCategory.ELEMENT, GlyphRarity.COMMON,
                pole("vnda", "VNDA", "Flow", HEAD,
                        "Liquid: what takes the shape of whatever holds it."),
                pole("saxvm", "SAXVM", "Stone", HEAD,
                        "Solid: what holds its own shape, and other things besides."));

        axis("ground", GlyphCategory.ELEMENT, GlyphRarity.COMMON,
                pole("ventvs", "VENTVS", "Air", HEAD,
                        "Wind, breath, the element that is only noticed while moving."),
                pole("terra", "TERRA", "Earth", HEAD,
                        "The ground, which everything is either standing on or buried in."));

        axis("fullness", GlyphCategory.ELEMENT, GlyphRarity.UNCOMMON,
                pole("plenvm", "PLENVM", "Full", QUALIFIER,
                        "Dense, packed, with no room left in it."),
                pole("vacvvm", "VACVVM", "Hollow", HEAD,
                        "Void and empty — and a hollow is a kind of thing, not only a lack."));
    }

    // ------------------------------------------------------------------
    // The Living
    // ------------------------------------------------------------------
    static {
        axis("quickness", GlyphCategory.LIVING, GlyphRarity.UNCOMMON,
                pole("vita", "VITA", "Living", QUALIFIER,
                        "Quick: still doing something of its own accord."),
                pole("mors", "MORS", "Dead", QUALIFIER,
                        "Still. What is left once a thing has finished being itself."));

        axis("growth", GlyphCategory.LIVING, GlyphRarity.COMMON,
                pole("germen", "GERMEN", "Seed", HEAD,
                        "Sprout and growth: the small thing that intends to be a large one."),
                pole("tabes", "TABES", "Rot", HEAD,
                        "Decay and blight — growth running the other way."));

        axis("body", GlyphCategory.LIVING, GlyphRarity.COMMON,
                pole("caro", "CARO", "Flesh", HEAD,
                        "The soft part, which does the living and the dying."),
                pole("ossa", "OSSA", "Bone", HEAD,
                        "The hard part, which outlasts the soft one."));

        axis("flora", GlyphCategory.LIVING, GlyphRarity.COMMON,
                pole("herba", "HERBA", "Green", HEAD,
                        "Leaf and blade: the living plant, while it is still growing."),
                pole("lignvm", "LIGNVM", "Wood", HEAD,
                        "Timber: the plant once it has stopped and gone hard."));

        axis("kind", GlyphCategory.LIVING, GlyphRarity.COMMON,
                pole("homo", "HOMO", "Folk", HEAD,
                        "The upright kind — those that build, trade and bear a grudge."),
                pole("bestia", "BESTIA", "Beast", HEAD,
                        "The other kind. Neither lesser nor safer."));
    }

    // ------------------------------------------------------------------
    // Matter
    // ------------------------------------------------------------------
    static {
        axis("metal", GlyphCategory.MATTER, GlyphRarity.COMMON,
                pole("ferrvm", "FERRVM", "Iron", HEAD,
                        "The working metal: common, hard, and asked to do everything."),
                pole("avrvm", "AVRVM", "Gold", HEAD,
                        "The precious metal, which is soft, and is wanted anyway."));

        axis("mineral", GlyphCategory.MATTER, GlyphRarity.UNCOMMON,
                pole("aes", "AES", "Copper", HEAD,
                        "The metal that ages, and shows you how long it has been out."),
                pole("adamas", "ADAMAS", "Diamond", HEAD,
                        "That which cannot be marred, and so records nothing."));

        axis("grain", GlyphCategory.MATTER, GlyphRarity.COMMON,
                pole("gemma", "GEMMA", "Gem", HEAD,
                        "Crystal: matter that grew into an order of its own."),
                pole("pvlvis", "PVLVIS", "Dust", HEAD,
                        "That which is ground down, and so made ready."));

        axis("refinement", GlyphCategory.MATTER, GlyphRarity.UNCOMMON,
                pole("candidvm", "CANDIDVM", "Refined", QUALIFIER,
                        "Pure and shining: the thing with everything else taken out."),
                pole("sordes", "SORDES", "Raw", QUALIFIER,
                        "Dross and ore: the thing with everything else still in."));
    }

    // ------------------------------------------------------------------
    // Making
    // ------------------------------------------------------------------
    static {
        axis("hand", GlyphCategory.MAKING, GlyphRarity.UNCOMMON,
                pole("opvs", "OPVS", "Wrought", QUALIFIER,
                        "Made by hand, and bearing the marks of the hand that made it."),
                pole("natvm", "NATVM", "Natural", QUALIFIER,
                        "Found so. Nobody meant it."));

        axis("age", GlyphCategory.MAKING, GlyphRarity.COMMON,
                pole("novvm", "NOVVM", "New", QUALIFIER,
                        "Fresh, and not yet marked by anything."),
                pole("senex", "SENEX", "Old", QUALIFIER,
                        "Aged and weathered — which is to say, written on by time."));

        axis("wholeness", GlyphCategory.MAKING, GlyphRarity.COMMON,
                pole("totvm", "TOTVM", "Whole", QUALIFIER,
                        "Uncut. Still the shape it came in."),
                pole("fractvm", "FRACTVM", "Cut", QUALIFIER,
                        "Broken or worked — the two are the same act with different intent."));

        axis("surface", GlyphCategory.MAKING, GlyphRarity.UNCOMMON,
                pole("tegmen", "TEGMEN", "Covering", HEAD,
                        "Wax, hide, shell: the layer between a thing and the weather."),
                pole("nvdvm", "NVDVM", "Bare", QUALIFIER,
                        "Exposed, with nothing between it and the weather."));
    }

    // ------------------------------------------------------------------
    // Form
    // ------------------------------------------------------------------
    static {
        axis("passage", GlyphCategory.FORM, GlyphRarity.UNCOMMON,
                pole("porta", "PORTA", "Gate", HEAD,
                        "A way through, which is to say a wall that gave up somewhere."),
                pole("vallvm", "VALLVM", "Wall", HEAD,
                        "Rampart and hold: what keeps one side of a thing from the other."));

        axis("relief", GlyphCategory.FORM, GlyphRarity.COMMON,
                pole("gradvs", "GRADVS", "Step", HEAD,
                        "A stair: height made climbable by being cut into pieces."),
                pole("aeqvvm", "AEQVVM", "Level", HEAD,
                        "Flat. The surface that has no opinion about which way you go."));
    }

    // ------------------------------------------------------------------
    // Will
    // ------------------------------------------------------------------
    static {
        axis("temper", GlyphCategory.WILL, GlyphRarity.UNCOMMON,
                pole("hostis", "HOSTIS", "Foe", QUALIFIER,
                        "Hostile. It has already decided about you."),
                pole("mitis", "MITIS", "Friend", QUALIFIER,
                        "Tame and gentle — which in most cases somebody had to make it."));

        axis("number", GlyphCategory.WILL, GlyphRarity.UNCOMMON,
                pole("vnicvm", "VNICVM", "One", QUALIFIER,
                        "Single, and therefore countable."),
                pole("grex", "GREX", "Throng", HEAD,
                        "Swarm and flock: many, behaving as one thing."));
    }

    // ------------------------------------------------------------------
    // Construction helpers
    // ------------------------------------------------------------------

    private record Pole(String path, String lemma, String gloss, boolean head, String description) {
    }

    private static Pole pole(String path, String lemma, String gloss, boolean head, String description) {
        return new Pole(path, lemma, gloss, head, description);
    }

    /**
     * Registers both poles of one axis, each pointing at the other. Rarity is a property
     * of the axis rather than the pole — the axis is itself a concept, and its two poles
     * hide in the same places.
     */
    private static void axis(String axisName, GlyphCategory category, GlyphRarity rarity, Pole a, Pole b) {
        // Epigraphy.MODID rather than Epigraphy.id(): a constant String is inlined at
        // compile time, so building the lexicon never has to initialise the mod class.
        ResourceLocation idA = new ResourceLocation(Epigraphy.MODID, a.path());
        ResourceLocation idB = new ResourceLocation(Epigraphy.MODID, b.path());
        add(idA, a, axisName, category, rarity, idB);
        add(idB, b, axisName, category, rarity, idA);
    }

    private static void add(ResourceLocation id, Pole pole, String axisName, GlyphCategory category,
                            GlyphRarity rarity, ResourceLocation opposite) {
        Glyph glyph = new Glyph(
                pole.lemma(),
                pole.gloss(),
                category,
                pole.head(),
                rarity,
                Optional.empty(),                 // sightings default from rarity
                Optional.of(axisName),
                Optional.of(opposite),
                Optional.of(pole.description()),
                Optional.empty(),                 // no mark override: all 52 marks are distinct
                Optional.empty(),                 // no authored pigment yet — hashed from the lemma
                Optional.empty()                  // no texture override: art is generated
        );
        Entry entry = new Entry(id, glyph);
        if (BY_ID.put(id, glyph) != null) {
            throw new IllegalStateException("Duplicate glyph id in the shipped lexicon: " + id);
        }
        ENTRIES.add(entry);
    }
}

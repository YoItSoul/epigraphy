package com.epigraphy.command;

import com.epigraphy.Epigraphy;
import com.epigraphy.registry.EpiItems;
import com.epigraphy.rune.Glyph;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * {@code /epigraphy runes} — the Phase 1 debug command ({@code docs/ROADMAP.md} §4):
 * what the loader actually accepted, and what it made of each glyph.
 */
public final class EpigraphyCommand {

    private static final SimpleCommandExceptionType UNKNOWN_GLYPH =
            new SimpleCommandExceptionType(Component.translatable("commands.epigraphy.unknown_glyph"));

    private EpigraphyCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("epigraphy")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("runes")
                        .executes(context -> list(context.getSource()))
                        .then(Commands.argument("glyph", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                        Epigraphy.glyphs().ids().stream().map(ResourceLocation::toString), builder))
                                .executes(context -> describe(
                                        context.getSource(), StringArgumentType.getString(context, "glyph"))))));
    }

    private static int list(CommandSourceStack source) {
        List<ResourceLocation> ids = Epigraphy.glyphs().ids().stream()
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .toList();

        source.sendSuccess(() -> Component.translatable("commands.epigraphy.runes.header", ids.size())
                .withStyle(ChatFormatting.GOLD), false);

        for (ResourceLocation id : ids) {
            Epigraphy.glyphs().get(id).ifPresent(glyph -> source.sendSuccess(() -> Component.literal(
                            "  " + glyph.lemma() + "  " + glyph.gloss()
                                    + (glyph.head() ? "  ✦" : "")
                                    + "  [" + id + "]")
                    .withStyle(glyph.head() ? ChatFormatting.WHITE : ChatFormatting.GRAY), false));
        }
        return ids.size();
    }

    private static int describe(CommandSourceStack source, String raw) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ResourceLocation id = ResourceLocation.tryParse(raw);
        Optional<Glyph> found = id == null ? Optional.empty() : Epigraphy.glyphs().get(id);
        if (found.isEmpty()) {
            throw UNKNOWN_GLYPH.create();
        }
        Glyph glyph = found.get();

        line(source, glyph.lemma() + " — " + glyph.gloss(), ChatFormatting.GOLD);
        line(source, "  category      " + glyph.category().getSerializedName(), ChatFormatting.GRAY);
        line(source, "  head          " + (glyph.head() ? "yes ✦ (may head a rune word)" : "no (qualifier only)"),
                ChatFormatting.GRAY);
        glyph.axis().ifPresent(axis -> line(source, "  axis          " + axis, ChatFormatting.GRAY));
        glyph.opposite().ifPresent(opposite -> line(source, "  opposite      " + opposite, ChatFormatting.GRAY));
        line(source, "  rarity        " + glyph.rarity().getSerializedName()
                + " (" + glyph.sightingsToLearn() + " sightings to learn)", ChatFormatting.GRAY);
        // (letter 1, letter 3, length tally) — everything that decides what the tile looks like.
        line(source, "  signature     " + glyph.signature(), ChatFormatting.GRAY);
        line(source, "  item          " + EpiItems.glyphItem(id)
                .map(item -> String.valueOf(ForgeRegistries.ITEMS.getKey(item)))
                .orElse("none — datapack glyph, carried by epigraphy:glyph with NBT"), ChatFormatting.GRAY);
        return 1;
    }

    private static void line(CommandSourceStack source, String text, ChatFormatting style) {
        source.sendSuccess(() -> Component.literal(text).withStyle(style), false);
    }
}

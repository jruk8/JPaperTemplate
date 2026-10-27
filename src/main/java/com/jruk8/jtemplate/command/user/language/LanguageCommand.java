package com.jruk8.jtemplate.command.user.language;

import com.jruk8.jtemplate.command.CommandContext;
import com.jruk8.jtemplate.command.Permissions;
import com.jruk8.jtemplate.message.Languages;
import com.jruk8.jtemplate.message.PlayerLanguage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * The {@code language} subcommand: bare shows the sender's language, a
 * code pins it, and {@code auto} clears the pin so the language keeps
 * following the client locale. Player-only: there is no server-wide
 * language.
 */
public class LanguageCommand {

    private final CommandContext ctx;
    private final PlayerLanguage languages;

    public LanguageCommand(CommandContext ctx, PlayerLanguage languages) {
        this.ctx = ctx;
        this.languages = languages;
    }

    /**
     * Builds the {@code language} Brigadier node, attached to the
     * {@code jtemplate} root by the registrar.
     *
     * @return the configured subcommand node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("language")
                .requires(CommandContext.requiresPermission(Permissions.LANGUAGE))
                .executes(context -> run(context.getSource(), null))
                .then(Commands.argument("language", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String prefix = builder.getRemainingLowerCase();
                            if ("auto".startsWith(prefix)) {
                                builder.suggest("auto");
                            }
                            for (String code : Languages.SUPPORTED) {
                                if (code.startsWith(prefix)) {
                                    builder.suggest(code);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> run(
                                context.getSource(), StringArgumentType.getString(context, "language"))));
    }

    private int run(CommandSourceStack source, String input) {
        if (!(source.getSender() instanceof Player player)) {
            ctx.error(source, m -> m.getCore().getPlayerOnly(), Map.of());
            return 0;
        }
        if (input == null || input.isBlank()) {
            return show(source, player);
        }
        if ("auto".equalsIgnoreCase(input.strip())) {
            return clear(source, player);
        }
        var code = Languages.matches(input);
        if (code.isEmpty()) {
            ctx.error(
                    source,
                    m -> m.getCore().getLanguageUnknown(),
                    Map.of("input", input.strip(), "codes", String.join(", ", Languages.SUPPORTED)));
            return 0;
        }
        languages.set(player.getUniqueId(), code.get());
        ctx.send(
                source,
                m -> m.getCore().getLanguageSet(),
                Map.of("name", Languages.label(code.get())));
        return Command.SINGLE_SUCCESS;
    }

    private int show(CommandSourceStack source, Player player) {
        String pin = languages.get(player.getUniqueId());
        if (pin != null) {
            ctx.send(
                    source,
                    m -> m.getCore().getLanguageShow(),
                    Map.of("name", Languages.label(pin)));
            return Command.SINGLE_SUCCESS;
        }
        ctx.send(
                source,
                m -> m.getCore().getLanguageShowAuto(),
                Map.of("name", Languages.label(Languages.normalize(localeTag(player)))));
        return Command.SINGLE_SUCCESS;
    }

    private int clear(CommandSourceStack source, Player player) {
        languages.clear(player.getUniqueId());
        ctx.send(
                source,
                m -> m.getCore().getLanguageAuto(),
                Map.of("name", Languages.label(Languages.normalize(localeTag(player)))));
        return Command.SINGLE_SUCCESS;
    }

    private static String localeTag(Player player) {
        return player.locale() == null ? null : player.locale().toLanguageTag();
    }
}

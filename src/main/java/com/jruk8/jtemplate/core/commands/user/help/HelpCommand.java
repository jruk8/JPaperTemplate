package com.jruk8.jtemplate.core.commands.user.help;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.List;

/**
 * Help subcommand that lists the plugin's commands, optionally filtered by a search query.
 */
public class HelpCommand {

    private static final List<HelpEntry> ENTRIES = List.of(
            new HelpEntry("/jtemplate help [query]", "Shows this help menu.", "jtemplate.user.help"),
            new HelpEntry("/jtemplate authors", "Shows the authors of the plugin.", "jtemplate.user.authors"),
            new HelpEntry("/jtemplate reload", "Reloads the plugin configuration.", "jtemplate.admin.reload")
    );

    private final CommandContext ctx;

    public HelpCommand(CommandContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Builds the {@code help} Brigadier node, attached to the {@code jtemplate} root by the registrar.
     *
     * @return the configured subcommand node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("help")
                .requires(CommandContext.requiresPermission("jtemplate.user", "jtemplate.user.help"))
                .executes(context -> {
                    showHelp(context.getSource(), null);
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("query", StringArgumentType.greedyString())
                        .executes(context -> {
                            showHelp(context.getSource(), StringArgumentType.getString(context, "query"));
                            return Command.SINGLE_SUCCESS;
                        }));
    }

    private void showHelp(CommandSourceStack sender, String query) {
        ctx.playSuccess(sender);
        ctx.sendMessage(sender, m -> m.getCore().getHelpHeader());
        HelpEntry.matching(ENTRIES, query).stream()
                .filter(entry -> sender.getSender().hasPermission(entry.permission()))
                .forEach(entry -> ctx.sendMessage(
                        sender,
                        m -> m.getCore().getHelpEntryFormat(),
                        Placeholder.parsed("syntax", entry.syntax()),
                        Placeholder.parsed("description", entry.description())
                ));
    }
}

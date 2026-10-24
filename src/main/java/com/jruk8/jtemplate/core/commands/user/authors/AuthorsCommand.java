package com.jruk8.jtemplate.core.commands.user.authors;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.List;

/**
 * Authors subcommand that displays the plugin version and authors.
 */
public class AuthorsCommand {

    private final AuthorFormatter authorFormatter;
    private final CommandContext ctx;
    private final PluginMeta pluginMeta;

    public AuthorsCommand(AuthorFormatter authorFormatter, CommandContext ctx, PluginMeta pluginMeta) {
        this.authorFormatter = authorFormatter;
        this.ctx = ctx;
        this.pluginMeta = pluginMeta;
    }

    /**
     * Builds the {@code authors} Brigadier node, attached to the {@code jtemplate} root by the registrar.
     *
     * @return the configured subcommand node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("authors")
                .requires(CommandContext.requiresPermission("jtemplate.user", "jtemplate.user.authors"))
                .executes(context -> {
                    onAuthors(context.getSource());
                    return Command.SINGLE_SUCCESS;
                });
    }

    public void onAuthors(CommandSourceStack sender) {
        String version = pluginMeta.getVersion();
        List<String> rawAuthors = pluginMeta.getAuthors();
        String authors = authorFormatter.formatAuthors(rawAuthors);

        ctx.success(
                sender,
                m -> m.getCore().getAuthorSuccess(),
                Placeholder.parsed("version", version),
                Placeholder.parsed("authors", authors)
        );
    }
}

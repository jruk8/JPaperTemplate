package com.jruk8.jtemplate.core.commands.user;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.user.authors.AuthorFormatter;
import com.jruk8.jtemplate.core.commands.user.authors.AuthorsCommand;
import com.jruk8.jtemplate.core.commands.user.help.HelpCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.configuration.PluginMeta;

/**
 * Registers all user-facing subcommands on the plugin's root command node.
 */
public class UserCommandsRegistrar implements Bootstrap {

    private final LiteralArgumentBuilder<CommandSourceStack> root;
    private final CommandContext ctx;
    private final PluginMeta pluginMeta;

    public UserCommandsRegistrar(LiteralArgumentBuilder<CommandSourceStack> root,
                                 CommandContext commandContext,
                                 PluginMeta pluginMeta) {
        this.root = root;
        this.ctx = commandContext;
        this.pluginMeta = pluginMeta;
    }

    @Override
    public void register() {
        root.then(new HelpCommand(ctx).node());
        root.then(new AuthorsCommand(new AuthorFormatter(), ctx, pluginMeta).node());
    }
}

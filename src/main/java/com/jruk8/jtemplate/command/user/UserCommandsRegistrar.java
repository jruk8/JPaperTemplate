package com.jruk8.jtemplate.command.user;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.command.CommandContext;
import com.jruk8.jtemplate.command.user.help.HelpCommand;
import com.jruk8.jtemplate.command.user.language.LanguageCommand;
import com.jruk8.jtemplate.command.user.version.AuthorFormatter;
import com.jruk8.jtemplate.command.user.version.VersionCommand;
import com.jruk8.jtemplate.message.PlayerLanguage;
import com.jruk8.jtemplate.placeholder.CommandUsageTracker;
import com.jruk8.jtemplate.update.UpdateCheckService;
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
    private final UpdateCheckService updateChecks;
    private final CommandUsageTracker usageTracker;
    private final HelpCommand help;
    private final PlayerLanguage playerLanguage;

    public UserCommandsRegistrar(LiteralArgumentBuilder<CommandSourceStack> root,
                                 CommandContext commandContext,
                                 PluginMeta pluginMeta,
                                 UpdateCheckService updateChecks,
                                 CommandUsageTracker usageTracker,
                                 HelpCommand help,
                                 PlayerLanguage playerLanguage) {
        this.root = root;
        this.ctx = commandContext;
        this.pluginMeta = pluginMeta;
        this.updateChecks = updateChecks;
        this.usageTracker = usageTracker;
        this.help = help;
        this.playerLanguage = playerLanguage;
    }

    @Override
    public void register() {
        root.then(help.node());
        root.then(new VersionCommand(new AuthorFormatter(), ctx, pluginMeta, updateChecks, usageTracker).node());
        root.then(new LanguageCommand(ctx, playerLanguage).node());
    }
}

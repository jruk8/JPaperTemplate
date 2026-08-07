package com.jruk8.jtemplate.core.commands;

import com.jruk8.jtemplate.core.configs.ConfigRegistrar;
import com.jruk8.jtemplate.core.messages.MessageConfig;
import com.jruk8.jtemplate.core.messages.Messenger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

/**
 * Handles the core commands for the plugin (for instance,
 * help, reload, etc.).
 */
public class CoreCommands {

    private final ConfigRegistrar config;
    private final Messenger messenger;

    public CoreCommands(ConfigRegistrar config, Messenger messenger) {
        this.config = config;
        this.messenger = messenger;
    }

    // Subcommand: /jtemplate help
    @Command("jtemplate|jt help")
    @Permission("jtemplate.use")
    public void onHelp(Source sender) {
        messenger.send(sender.source(), "<prefix> <green>JTemplate running smooth.");
    }

    // Subcommand: /jtemplate reload
    @Command("jtemplate|jt reload")
    @Permission("jtemplate.admin")
    public void onReload(Source sender) {
        long start = System.currentTimeMillis();

        config.reload();

        long time = System.currentTimeMillis() - start;
        MessageConfig messages = config.getMessagesConfig();

        this.messenger.send(
                sender.source(),
                messages.getReloadSuccess(),
                Placeholder.parsed("time", String.valueOf(time))
        );
    }
}
package com.jruk8.jtemplate.core.commands;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.admin.AdminCommandsRegistrar;
import com.jruk8.jtemplate.core.commands.user.UserCommandsRegistrar;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Registers all plugin commands through Paper's native Brigadier support.
 */
public class CommandsRegistrar implements Bootstrap {

    private final LifecycleEventManager<Plugin> lifecycle;
    private final Reloader reloader;
    private final CommandContext commandContext;
    private final PluginMeta pluginMeta;

    public CommandsRegistrar(LifecycleEventManager<Plugin> lifecycle,
                             Reloader reloader,
                             CommandContext commandContext,
                             PluginMeta pluginMeta) {
        this.lifecycle = lifecycle;
        this.reloader = reloader;
        this.commandContext = commandContext;
        this.pluginMeta = pluginMeta;
    }

    @Override
    public void register() {
        lifecycle.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var root = Commands.literal("jtemplate");
            new UserCommandsRegistrar(root, commandContext, pluginMeta).register();
            new AdminCommandsRegistrar(root, commandContext, reloader).register();
            event.registrar().register(root.build(), "JTemplate commands", List.of("jt"));
        });
    }
}

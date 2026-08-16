package com.jruk8.jtemplate.core;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.CommandsRegistrar;
import com.jruk8.jtemplate.core.configs.ConfigRegistrar;
import com.jruk8.jtemplate.core.messages.MessageBootstrap;
import com.jruk8.jtemplate.core.sounds.SoundPlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;

public final class JTemplatePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Set up all core modules
        setupCoreModules();
    }

    private void setupCoreModules() {
        var logger = getLogger();

        // Initialize reloader
        Reloader reloader = new Reloader(logger);

        // Initialize all configs
        var configRegistrar = new ConfigRegistrar(this);
        configRegistrar.register();
        reloader.register(configRegistrar);

        // Initialize messenger
        var messageRegistrar = new MessageBootstrap(configRegistrar);
        messageRegistrar.register();
        var messenger = messageRegistrar.getMessenger();

        // Initialize sound player
        var soundPlayer = new SoundPlayer(logger);

        // Initialize commands
        var commandManager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(this);
        var annotationParser = new AnnotationParser<>(commandManager, Source.class);
        var context = new CommandContext(
                configRegistrar.getMessagesConfig(),
                configRegistrar.getSoundsConfig(),
                messenger,
                soundPlayer
        );
        var pluginMeta = getPluginMeta();
        var commandRegistrar = new CommandsRegistrar(commandManager, annotationParser, reloader, context, pluginMeta);
        commandRegistrar.register();

        // Initialize bStats
        var metricsBootstrap = new MetricsBootstrap(this);
        metricsBootstrap.register();
    }
}
package com.jruk8.jtemplate.core;

import com.jruk8.jtemplate.core.commands.CommandRegistrar;
import com.jruk8.jtemplate.core.configs.ConfigRegistrar;
import com.jruk8.jtemplate.core.messages.MessageBootstrap;
import com.jruk8.jtemplate.core.messages.Messenger;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;

public final class JTemplatePlugin extends JavaPlugin {

    private ConfigRegistrar config;
    private Messenger messenger;

    @Override
    public void onEnable() {
        // Register all bootstraps
        registerBootstraps();

        // Register reloads
        Reloader reloader = new Reloader(getLogger());
        reloader.register(config);
    }

    private void registerBootstraps() {
        // Initialize config
        config = new ConfigRegistrar(this);
        config.register();

        // Initialize messages
        var messageRegistrar = new MessageBootstrap(config);
        messageRegistrar.register();
        messenger = messageRegistrar.getMessenger();

        // Initialize commands
        var commandManager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(this);
        var annotationParser = new AnnotationParser<>(commandManager, Source.class);
        var commandRegistrar = new CommandRegistrar(annotationParser, config, messenger);
        commandRegistrar.register();

        // Initialize bStats
        var metricsBootstrap = new MetricsBootstrap(this);
        metricsBootstrap.register();
    }
}
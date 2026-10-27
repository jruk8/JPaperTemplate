package com.jruk8.jtemplate;

import com.jruk8.jtemplate.command.CommandContext;
import com.jruk8.jtemplate.command.CommandsRegistrar;
import com.jruk8.jtemplate.config.ConfigRegistrar;
import com.jruk8.jtemplate.core.MetricsBootstrap;
import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.StartupBanner;
import com.jruk8.jtemplate.message.LanguageStore;
import com.jruk8.jtemplate.message.MessageBootstrap;
import com.jruk8.jtemplate.message.MessageBundles;
import com.jruk8.jtemplate.message.Messenger;
import com.jruk8.jtemplate.message.PlayerLanguage;
import com.jruk8.jtemplate.placeholder.CommandUsageTracker;
import com.jruk8.jtemplate.placeholder.PlaceholdersBootstrap;
import com.jruk8.jtemplate.sound.SoundPlayer;
import com.jruk8.jtemplate.storage.StorageBootstrap;
import com.jruk8.jtemplate.update.UpdateCheckerBootstrap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class JTemplatePlugin extends JavaPlugin {

    private StorageBootstrap storageBootstrap;

    @Override
    public void onEnable() {
        // Set up all core modules
        setupCoreModules();
    }

    @Override
    public void onDisable() {
        if (this.storageBootstrap != null) {
            this.storageBootstrap.shutdown();
        }
    }

    private void setupCoreModules() {
        var logger = getLogger();

        // Initialize reloader
        Reloader reloader = new Reloader(logger);

        // Initialize all configs
        var configRegistrar = new ConfigRegistrar(this);
        configRegistrar.register();
        reloader.register(configRegistrar);

        // Initialize storage first: languages and commands both need it
        CommandUsageTracker usageTracker = setupStorageAndPlaceholders(logger, reloader, configRegistrar);

        // Initialize per-language message bundles and the messenger
        var playerLanguage = new PlayerLanguage(new LanguageStore(this.storageBootstrap::getStorage));
        var bundles = messageBundles(reloader, configRegistrar, playerLanguage, logger);
        var messenger = messageRegistrar(configRegistrar, bundles).getMessenger();

        // Initialize sound player
        var soundPlayer = new SoundPlayer(logger);

        // Initialize update checker; the banner prints once the first check lands
        var updateChecker = new UpdateCheckerBootstrap(
                this,
                configRegistrar.getPluginConfig(),
                messenger,
                bundles,
                logger,
                this::printBanner
        );
        updateChecker.register();
        reloader.register(updateChecker);

        // Initialize commands
        setupCommands(
                reloader, configRegistrar, messenger, soundPlayer, updateChecker, usageTracker,
                bundles, playerLanguage);

        // Initialize bStats unless anonymous statistics are disabled
        boolean metricsEnabled = configRegistrar.getPluginConfig().getMetrics().isEnabled();
        var metricsBootstrap = new MetricsBootstrap(this, metricsEnabled);
        metricsBootstrap.register();
    }

    private void printBanner(boolean updateAvailable) {
        StartupBanner.print(
                Bukkit.getConsoleSender()::sendMessage,
                getName(),
                getPluginMeta().getVersion(),
                updateAvailable);
    }

    private MessageBundles messageBundles(
            Reloader reloader,
            ConfigRegistrar configRegistrar,
            PlayerLanguage playerLanguage,
            Logger logger) {
        var bundles = new MessageBundles(
                configRegistrar.getMessagesConfig(),
                playerLanguage::get,
                new File(getDataFolder(), "lang"),
                logger);
        reloader.register(bundles);
        return bundles;
    }

    private MessageBootstrap messageRegistrar(ConfigRegistrar configRegistrar, MessageBundles bundles) {
        var messageRegistrar = new MessageBootstrap(configRegistrar, bundles);
        messageRegistrar.register();
        return messageRegistrar;
    }

    private void setupCommands(
            Reloader reloader,
            ConfigRegistrar configRegistrar,
            Messenger messenger,
            SoundPlayer soundPlayer,
            UpdateCheckerBootstrap updateChecker,
            CommandUsageTracker usageTracker,
            MessageBundles bundles,
            PlayerLanguage playerLanguage
    ) {
        var context = new CommandContext(
                configRegistrar.getMessagesConfig(),
                configRegistrar.getSoundsConfig(),
                messenger,
                soundPlayer,
                bundles
        );
        var commandRegistrar = new CommandsRegistrar(
                getLifecycleManager(), reloader, context, getPluginMeta(),
                configRegistrar.getPluginConfig(), updateChecker.getService(), usageTracker,
                playerLanguage);
        commandRegistrar.register();
    }

    private CommandUsageTracker setupStorageAndPlaceholders(
            Logger logger, Reloader reloader, ConfigRegistrar configRegistrar) {
        // Initialize SQLite storage
        this.storageBootstrap = new StorageBootstrap(configRegistrar.getPluginConfig(), getDataFolder(), logger);
        this.storageBootstrap.register();
        reloader.register(this.storageBootstrap);
        var usageTracker = new CommandUsageTracker(this.storageBootstrap::getStorage);

        // Initialize PlaceholderAPI placeholders
        var placeholdersBootstrap = new PlaceholdersBootstrap(usageTracker, this, logger);
        placeholdersBootstrap.register();
        reloader.register(placeholdersBootstrap);
        return usageTracker;
    }
}

package com.jruk8.jtemplate.core.configs;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.Reloadable;
import com.jruk8.jtemplate.core.messages.MessageConfig;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigRegistrar implements Bootstrap, Reloadable {

    @Getter
    private PluginConfig pluginConfig;
    @Getter
    private MessageConfig messagesConfig;
    private final JavaPlugin plugin;

    public ConfigRegistrar(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        // Load plugin config
        this.pluginConfig = ConfigManager.create(PluginConfig.class, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(new File(plugin.getDataFolder(), "config.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        // Load messages
        this.messagesConfig = ConfigManager.create(MessageConfig.class, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(new File(plugin.getDataFolder(), "messages.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    @Override
    public void reload() {
        if (this.pluginConfig != null) {
            this.pluginConfig.load(true);
        }
        if (this.messagesConfig != null) {
            this.messagesConfig.load(true);
        }
    }
}

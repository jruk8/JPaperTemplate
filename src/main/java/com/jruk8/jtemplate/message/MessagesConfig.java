package com.jruk8.jtemplate.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

/**
 * Messages shown by the plugin.
 * <p>
 * Every message starts with {prefix} and uses the config.yml color palette:
 * {plain} for body text, {brand} for headers and the brand name, {hl} for
 * numbers, player names and similar values, {error} for errors. Commands
 * insert runtime values through {bracket} placeholders: {version},
 * {authors}, {time}, {syntax}, {description}, {latest}, {current}, {url},
 * {modrinth-url}, {permission}, {input}, {name}, {codes}, {modrinth},
 * {github}, {discord} and {donate}. Angle brackets are reserved for raw
 * MiniMessage, and \n breaks the line.
 */
@Getter
@Setter
public class MessagesConfig extends OkaeriConfig {

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Core extends OkaeriConfig {

        @Comment("Sent when a reload succeeds (use {time})")
        private String reloadSuccess = "\\n{prefix}{plain}Configuration reloaded in {hl}{time}ms{plain}.";

        @Comment("Sent when a reload fails (use {time})")
        private String reloadFailure = "{prefix}{error}Reload failed in {time}ms. "
                + "Check the console for details.";

        @Comment("Sent when changed labels need a restart")
        private String reloadRestartRequired = "{prefix}{hl}Reload complete. "
                + "{plain}Command label changes need a server restart.";

        @Comment("Help header (use {version})")
        private String helpHeader = "\\n{prefix}{brand}JTemplate {version} Help:";

        @Comment("One help entry (use {syntax} and {description})")
        private String helpEntryFormat = "{brand}» {hl}{syntax}{plain}: {description}";

        @Comment("Version command output (use {version} and {authors})")
        private String versionSuccess = "\n{prefix}{brand}Version {hl}{version}"
                + "\n{brand}» {hl}Authors: {plain}{authors}"
                + "\n{brand}» {hl}Links: {modrinth}{plain} | {github}{plain} | {donate}";

        @Comment("Update notice (use {latest}, {current}, {url} and {modrinth-url})")
        private String updateAvailable = "\\n{prefix}{hl}JTemplate {latest}{brand} is available"
                + " (running {hl}{current}{brand})."
                + "\\n{brand}» {hl}Update: {modrinth} {plain}| {github} {plain}| {discord}";

        @Comment("Sent when the sender lacks a permission (use {permission})")
        private String noPermission = "{prefix}{error}You do not have the permission "
                + "{hl}'{permission}'{error} to use this command.";

        @Comment("Sent when a target selector matched no players")
        private String noTargets = "{prefix}{error}No players matched that target selector.";

        @Comment("Sent when console runs a command without a target selector")
        private String consoleNeedsTarget = "{prefix}{error}Console must specify a target.";

        @Comment("Sent when console runs a player-only command")
        private String playerOnly = "{prefix}{error}Only players can use this command.";

        @Comment("Modrinth link button (use {url})")
        private String linkModrinth = "<click:open_url:'{url}'>"
                + "<hover:show_text:'<#5ae663>Open on Modrinth'><#5ae663>Modrinth</hover></click>";

        @Comment("GitHub link button (use {url})")
        private String linkGithub = "<click:open_url:'{url}'>"
                + "<hover:show_text:'<#5a88e6>Open on GitHub'><#5a88e6>GitHub</hover></click>";

        @Comment("Discord link button (use {url})")
        private String linkDiscord = "<click:open_url:'{url}'>"
                + "<hover:show_text:'<#6258f2>Join the Discord'><#6258f2>Discord</hover></click>";

        @Comment("Donate link button (use {url})")
        private String linkDonate = "<click:open_url:'{url}'>"
                + "<hover:show_text:'<#ff5e5b>Support on Ko-fi'><#ff5e5b>Donate</hover></click>";

        @Comment("Separator between version authors")
        private String versionComma = "<gray>,</gray>";

        @Comment("'and' word between version authors")
        private String versionAnd = "<gray>and</gray>";

        @Comment("Language shown when pinned (use {name})")
        private String languageShow = "{prefix}{plain}Language: {hl}{name}{plain}.";

        @Comment("Language shown when following the client (use {name})")
        private String languageShowAuto = "{prefix}{plain}Language follows your client: {hl}{name}{plain}.";

        @Comment("Sent when the language is pinned (use {name})")
        private String languageSet = "{prefix}{plain}Language set to {hl}{name}{plain}. "
                + "It will no longer follow your client.";

        @Comment("Sent when the pin is cleared (use {name})")
        private String languageAuto = "{prefix}{plain}Language follows your client again: {hl}{name}{plain}.";

        @Comment("Sent for an unknown language (use {input} and {codes})")
        private String languageUnknown = "{prefix}{error}Unknown language {hl}{input}{error}. "
                + "Use {hl}{codes}{error} or {hl}auto{error}.";
    }

    @Comment("Prefix prepended to every message, trailing space included")
    private String prefix = "<#a6a6a6>[<gradient:#8fdde3:#84b8db><bold>J</bold>Template</gradient>]</#a6a6a6> ";

    @Comment("Core plugin messages")
    private Core core = new Core();

    @Comment("Help menu command descriptions")
    private Help help = new Help();

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Help extends OkaeriConfig {

        @Comment("Help description of the help command")
        private String help = "Shows this help menu.";

        @Comment("Help description of the version command")
        private String version = "Shows the plugin version.";

        @Comment("Help description of the reload command")
        private String reload = "Reloads the plugin configuration.";

        @Comment("Help description of the language command")
        private String language = "Shows or sets your language.";
    }
}

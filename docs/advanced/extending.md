---
icon: material/wrench
---

# Extending the Template

This page explains where new code plugs in. Every module follows the same
shape: a top-level package under `com.jruk8.jtemplate`, its own config class
(or section), and a registrar implementing `Bootstrap` that is invoked from
`JTemplatePlugin.setupCoreModules()`.

Dependencies flow one way, constructor-injected: configs -> services ->
commands/placeholders. There are no singletons or static state.

## Add a Command

1. Create a package `command.user.<name>` (or `admin`) with a command
   class that builds a Brigadier node:

   ```java
   public class PingCommand {
       private final CommandContext ctx;

       public PingCommand(CommandContext ctx) { this.ctx = ctx; }

       public LiteralArgumentBuilder<CommandSourceStack> node() {
           return Commands.literal("ping")
                   .requires(CommandContext.requiresPermission(Permissions.PING))
                   .executes(context -> {
                       // Own text lives in MessagesConfig; {braces} resolve from the map.
                       ctx.sendMessage(context.getSource(),
                               messages -> messages.getCore().getHelpHeader(), Map.of());
                       return Command.SINGLE_SUCCESS;
                   });
       }
   }
   ```

   Commands are Paper-native Brigadier nodes registered through
   `LifecycleEvents.COMMANDS` (see `CommandsRegistrar`). The root literal
   (`jtemplate`, plus its configured aliases) carries a bare-root help
   fallback, so a new command only defines its own subcommand node.
   Messages are strictly MiniMessage: plugin placeholders use `{braces}`,
   angle brackets are raw MiniMessage.

   Commands that target players take a selector argument built with
   `PlayerSelectorArgument.players()` (a bare `ArgumentTypes.players()`
   is rejected at registration time), resolve it with
   `TargetSelector.fromSelector()` for the `other`-permission handling,
   and complete it with `TargetSelector.suggestTargets()`.

2. Register it in the matching registrar
   (`UserCommandsRegistrar.register()`):
   `root.then(new PingCommand(ctx).node());`

3. Declare the permission in `paper-plugin.yml` (with a constant in
   `Permissions`) and document the command in `docs/basics/commands.md`
   and `docs/advanced/permissions.md`. Add a `HelpEntry` in `HelpCommand`
   so `/jtemplate help` lists it.

## Add a Config Option

The config classes are okaeri-configs. For a small option, add a field to an
existing config class; for a coherent new section, add a nested static class
and give it a field in the parent (see `PluginConfig.Colors` for the
pattern). The section will be created in the yaml file automatically and
reloaded by `ConfigRegistrar` on `/jtemplate reload`.

## Add a Stat to the Database

`SqliteStorage` exposes six operations for generic key/value state:

- `increment(key, player)` and `incrementGlobal(key)` per-player / global
- `playerTotal(key, player)` and `globalTotal(key)` counter reads
- `setPlayerValue(key, player, value)` and `playerValue(key, player)`
  arbitrary per-player values

Give your feature its own stat key and a small service class around
`SqliteStorage` (like `CommandUsageTracker`), then inject that service where
state is produced (listener, command) and consumed (placeholder). Hold the
storage through a `Supplier<SqliteStorage>` (from
`StorageBootstrap::getStorage`): reload swaps the instance, so a stored
reference goes stale.

## Add a Placeholder

Add a branch to `UsageExpansion.onRequest()`:

```java
if ("my_stat".equals(params)) {
    return selfUses(player, tracker::playerUses);
}
```

Provide the value through an injected service and store it in SQLite if it
should survive restarts. Registered placeholders are announced to
PlaceholderAPI via `getPlaceholders()`.

## Remove the Placeholders Feature

If you do not need PAPI in your plugin:

1. Delete the `placeholder` package.
2. Remove the usage tracker wiring in `JTemplatePlugin`
   (`setupStorageAndPlaceholders()` and the `CommandsRegistrar` argument).
3. Remove `PlaceholderAPI` from `paper-plugin.yml` (`dependencies`), or keep
   it if your messages use `%otherplugin_...%` placeholders.

## Expose an API

Other plugins hook yours through Bukkit events plus a service looked up
from the services manager. Keep the public surface in an `api` package
that depends on nothing but Bukkit and Adventure: an interface with
queries and actions, an immutable model, and one event class per
lifecycle moment (extending Bukkit's `Event`, `Cancellable` where a veto
makes sense). Implement the interface next to the service it fronts and
register it in the plugin class:

```java
getServer().getServicesManager().register(MyApi.class, new MyApiImpl(...), this, ServicePriority.Normal);
```

Fire events from the service on every flow, including silent and bulk
ones, so listeners see the whole truth. Document the surface in a
`docs/advanced/api/` folder: an overview with a hookup example, an event
catalog with ordering guarantees, and an actions reference with the
threading rules.

## Modules Reference

| Package | Key classes |
| --- | --- |
| `command` | `CommandsRegistrar`, `UserCommandsRegistrar`, `AdminCommandsRegistrar`, `CommandContext`, `Permissions` |
| `command.selector` | `TargetSelector`, `SelectorSuggestions`, `ValueSuggestions`, `PlayerSelectorArgument` |
| `command.user` | `HelpCommand`, `HelpEntry`, `VersionCommand`, `AuthorFormatter`, `LanguageCommand` |
| `config` | `ConfigRegistrar`, `PluginConfig` |
| `message` | `MessageBootstrap`, `Messenger`, `MessageParser`, `MessageBundles`, `Languages`, `PlayerLanguage`, `LanguageStore` |
| `sound` | `SoundPlayer`, `SoundsConfig` |
| `placeholder` | `PlaceholdersBootstrap`, `CommandUsageTracker`, `UsageExpansion` |
| `storage` | `StorageBootstrap`, `SqliteStorage` |
| `update` | `UpdateCheckerBootstrap`, `UpdateCheckService`, `GitHubReleaseHttp`, `UpdateCheckNotifier`, `UpdateCheckJoinListener` |

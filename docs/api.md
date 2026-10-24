# Extending the Template

This page explains where new code plugs in. Every module follows the same
shape: a package under `com.jruk8.jtemplate.core`, its own config class (or
section), and a registrar implementing `Bootstrap` that is invoked from
`JTemplatePlugin.setupCoreModules()`.

Dependencies flow one way, constructor-injected: configs -> services ->
commands/placeholders. There are no singletons or static state.

## Add a Command

1. Create a package `core.commands.user.<name>` (or `admin`) with a command
   class that builds a Brigadier node:

   ```java
   public class PingCommand {
       private final CommandContext ctx;

       public PingCommand(CommandContext ctx) { this.ctx = ctx; }

       public LiteralArgumentBuilder<CommandSourceStack> node() {
           return Commands.literal("ping")
                   .requires(CommandContext.requiresPermission("jtemplate.user", "jtemplate.user.ping"))
                   .executes(context -> {
                       ctx.sendMessage(context.getSource(), "<green>Pong!</green>");
                       return Command.SINGLE_SUCCESS;
                   });
       }
   }
   ```

   The root literal (`jtemplate`, alias `jt`) is built by
   `CommandsRegistrar` and registered via `LifecycleEvents.COMMANDS`, so a
   new command only defines its own subcommand node.

2. Attach the node to the root in the matching registrar
   (`UserCommandsRegistrar.register()`):
   `root.then(new PingCommand(ctx).node());`

3. Document the command and permission in `docs/commands.md` and
   `docs/permissions.md`, and add a `HelpEntry` so it shows up in
   `/jtemplate help`.

## Add a Config Option

The config classes are okaeri-configs. For a small option, add a field to an
existing config class; for a coherent new section, add a nested static class
and give it a field in the parent (see `PluginConfig.Placeholders` for the
pattern). The section will be created in the yaml file automatically and
reloaded by `ConfigRegistrar` on `/jtemplate reload`.

## Add a Stat to the Database

`SqliteStorage` exposes four operations for generic key/value counters:

- `increment(key, player)` and `incrementGlobal(key)` per-player / global
- `playerTotal(key, player)` and `globalTotal(key)` reads

Give your feature its own stat key and a small service class around
`SqliteStorage` (like `CommandUsageTracker`), then inject that service where
state is produced (listener, command) and consumed (placeholder).

## Add a Placeholder

Add a switch case to `AuthorsCommandUsageExpansion.onRequest()`:

```java
case "my_stat" -> player == null ? null
        : String.valueOf(tracker.playerUses(player.getUniqueId()));
```

Provider the value through an injected service and store it in SQLite if it
should survive restarts. Registered placeholders are announced to
PlaceholderAPI via `getPlaceholders()`.

## Remove the Placeholders Feature

If you do not need PAPI in your plugin:

1. Delete the `core.placeholders` package.
2. Delete the `placeholders` section from `PluginConfig` and remove the wiring
   block in `JTemplatePlugin.setupStorageAndPlaceholders()`.
3. Remove `PlaceholderAPI` from `plugin.yml` (soft-depend) -- or keep it if
   your messages use `%otherplugin_...%` placeholders.

## Modules Reference

| Package | Key classes |
| --- | --- |
| `core.commands` | `CommandsRegistrar`, `UserCommandsRegistrar`, `AdminCommandsRegistrar`, `CommandContext` |
| `core.configs` | `ConfigRegistrar`, `PluginConfig` |
| `core.messages` | `MessageBootstrap`, `Messenger`, `MessageParser` |
| `core.sounds` | `SoundPlayer`, `SoundsConfig` |
| `core.placeholders` | `PlaceholdersBootstrap`, `CommandUsageTracker`, `AuthorsCommandUsageListener`, `AuthorsCommandUsageExpansion` |
| `core.storage` | `StorageBootstrap`, `SqliteStorage` |

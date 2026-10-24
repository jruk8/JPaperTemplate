# Permissions

## User Commands (`jtemplate.user`)

| Permission | Description | Default |
| --- | --- | --- |
| `jtemplate.user` | Parent node for user commands. | Everyone |
| `jtemplate.user.help` | Use `/jtemplate help`. | Everyone |
| `jtemplate.user.authors` | Use `/jtemplate authors`. | Everyone |

## Admin Commands (`jtemplate.admin`)

| Permission | Description | Default |
| --- | --- | --- |
| `jtemplate.admin` | Parent node for admin commands. | OP |
| `jtemplate.admin.reload` | Use `/jtemplate reload`. | OP |

Permissions are checked on the command nodes with Brigadier `requires()`
predicates (see `CommandContext.requiresPermission`); each subcommand
requires its parent node plus its own permission. See
[commands.md](commands.md).

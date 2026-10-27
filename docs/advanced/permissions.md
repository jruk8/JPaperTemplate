---
icon: material/key
---

# Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `jtemplate.self` | Act as yourself in commands that support selectors. | Everyone |
| `jtemplate.other` | Target other players with commands that support selectors (needs the matching command permission too). | OP |
| `jtemplate.reload` | Use `/jtemplate reload`. | OP |
| `jtemplate.help` | Use `/jtemplate help` (entries are permission-filtered). | Everyone |
| `jtemplate.version` | Use `/jtemplate version`. | Everyone |
| `jtemplate.language` | Use `/jtemplate language`. | Everyone |

Permissions are checked on the command nodes with Brigadier `requires()`
predicates (see `CommandContext.requiresPermission`); the node constants
live in `Permissions`. Commands that target other players check
`jtemplate.other` through `TargetSelector`. See
[commands.md](../basics/commands.md).

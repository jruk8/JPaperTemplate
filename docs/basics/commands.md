---
icon: material/console
---

# Commands

All commands are available under `/jtemplate` and its aliases (default
`/jt`, see `aliases` in [configuration.md](../configuration.md)). Running
the root with no arguments shows the help listing.

| Command | Description | Permission |
| --- | --- | --- |
| `/jtemplate help [query]` | Shows the in-game command list. | `jtemplate.help` |
| `/jtemplate version` | Shows the plugin version and authors. | `jtemplate.version` |
| `/jtemplate reload` | Reloads all configuration files. | `jtemplate.reload` |
| `/jtemplate language [language\|auto]` | Shows or sets your language. | `jtemplate.language` |

- **`help`** supports an optional query argument that filters the listed
  commands (`/jtemplate help reload`). Entries are filtered by permission,
  so every sender only sees what they may use.
- **`version`** prints the plugin's version and author list with links,
  and repeats a pending update notice to admins.
- **`reload`** re-reads `config.yml`, `messages.yml` and `sounds.yml`, then
  reopens the SQLite database and re-registers the placeholders with the new
  settings. Changed root labels need a restart; `reload` says so when it
  detects a change.
- **`language`** shows your language, pins one with a code or language
  name, or clears the pin with `auto` so the language follows your
  client locale again. Player-only: there is no server-wide language.

See [permissions.md](../advanced/permissions.md) for defaults.

## Adding a Command

1. Create a command class in a new subpackage of `command` (e.g.
   `command.user.fun`) that builds a Brigadier node with
   `Commands.literal(...)`, gates it with `requires()`, and runs its logic
   in `executes(...)`.
2. Attach the node to the root in the matching registrar
   (`UserCommandsRegistrar` or `AdminCommandsRegistrar`) with
   `root.then(new MyCommand(...).node())`, injecting only the services it
   needs (commonly `CommandContext`).
3. Declare the permission in `paper-plugin.yml` (with a constant in
   `Permissions`), document it in [permissions.md](../advanced/permissions.md)
   and add a `HelpEntry` so the command shows up in `/jtemplate help`.

See [extending.md](../advanced/extending.md) for the full walkthrough.

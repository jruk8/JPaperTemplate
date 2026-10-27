---
icon: material/tune
---

# Configuration

The plugin writes `config.yml`, `messages.yml` and `sounds.yml` into its data
folder (`plugins/JTemplate/`) on first start. Edit a file and run
`/jtemplate reload` to apply the changes without restarting the server.
Missing keys are re-added with defaults; unknown keys are removed.

## config.yml

```yaml
updateChecker:
  enabled: true
aliases:
  - jt
defaultLanguage: auto
storage:
  dbFile: jtemplate.db
colors:
  plainColor: '<#b8d8e0>'
  brandColor: '<#84b8db>'
  highlightColor: '<#ebf1f2>'
  errorColor: '<#415ad9>'
metrics:
  enabled: true
```

### `updateChecker`

- `enabled` » checks GitHub releases for new versions on enable and on
  reload. The check runs asynchronously; findings go to the console and
  in-game to players holding `jtemplate.reload` (on join and via
  `/jtemplate version`). Update notices link both the GitHub release and
  the Modrinth project page.

### `aliases`

Extra labels for the `/jtemplate` root command (`/jt` works out of the
box). Running the root with no arguments shows the help listing. Blanks,
duplicates and the `jtemplate` label itself are ignored. Applies on
restart; `/jtemplate reload` reports when a restart is needed.

### `defaultLanguage`

Default language for players without a language pin: `auto` follows
each player's client locale, any other [language key](#languages)
forces that language. Pins always win; unknown keys warn and behave
as `auto`. Takes effect on reload. Revoke `jtemplate.language` to
strictly force one language server-wide.

### `storage`

- `dbFile` » name of the SQLite database file created in the plugin's data
  folder. The database holds generic per-player and server-wide counters
  (tables `player_stats` and `global_stats`).

### `colors`

The message color palette. Every `messages.yml` entry uses these via
`{plain}`, `{brand}`, `{hl}` and `{error}`, resolved when the message is
rendered; editing a color and reloading is enough, the messages
themselves never hardcode colors.

### `metrics`

- `enabled` » sends anonymous usage statistics via bStats (open-source,
  no personal data or IP addresses). Read once at startup: disabling it
  needs a server restart to take effect.

## messages.yml

The plugin messages, always in MiniMessage format. Every message starts
with `{prefix}` (which includes its trailing space), and colors come
from the `config.yml` palette. Placeholders use `{braces}`; angle
brackets are raw MiniMessage, and `\n` breaks the line:

- `prefix` » prefix prepended via the `{prefix}` placeholder.
- `core.reloadSuccess` / `core.reloadFailure` » reload outcome
  (use `{time}`).
- `core.reloadRestartRequired` » shown when root labels changed and need
  a restart to apply.
- `core.helpHeader` / `core.helpEntryFormat` » help command layout
  (use `{version}` for the header; `{syntax}` and `{description}` per entry).
- `core.versionSuccess` » version command output (use `{version}` and
  `{authors}`; linked author names arrive pre-formatted).
- `core.updateAvailable` » update notice (use `{latest}`, `{current}`,
  `{url}` and `{modrinth-url}`).
- `core.noPermission` » the sender lacks a permission (use `{permission}`).
- `core.noTargets` » a target selector matched no players.
- `core.consoleNeedsTarget` » console ran a command without a target.
- `core.playerOnly` » console ran a player-only command.
- `core.linkModrinth` / `core.linkGithub` / `core.linkDiscord` /
  `core.linkDonate` » link button templates (use `{url}`); the
  messenger injects them per recipient so labels and tooltips follow
  the reader's language.
- `core.versionComma` / `core.versionAnd` » joining words between
  version authors.
- `core.languageShow` / `core.languageShowAuto` » the language listing
  (use `{name}`).
- `core.languageSet` / `core.languageAuto` » pin set and pin cleared
  (use `{name}`).
- `core.languageUnknown` » unknown language input (use `{input}` and
  `{codes}`).
- `help.help` / `help.version` / `help.reload` / `help.language` »
  help menu descriptions per command.

Messages can embed PlaceholderAPI placeholders (`%player_name%`) when
PlaceholderAPI is installed.

## Languages

Messages are per player, never per server: every recipient reads the
bundle matching their own language. Without a pin the language follows
the client's locale live; `/jtemplate language <language>` pins one
persistently, `/jtemplate language auto` clears the pin, and a bare
`/jtemplate language` shows the current one. English is the fallback
everywhere: console, unknown locales, missing files and missing keys
all read `messages.yml`. Each used language seeds its `lang/<code>.yml`
from the bundled translation on first use; the files stay
admin-editable and overlay the English schema, so deleting a key (or a
whole file) just reads English again.

The `defaultLanguage` key in `config.yml` decides what unpinned
players read: `auto` (the default) keeps following each player's
client locale, while any other key forces that language on everyone
without a pin; existing pins always win. Unknown keys warn in the
console and behave as `auto`. To strictly force one language (e.g.
for unique messages), set it there and revoke `jtemplate.language`
with your permissions plugin so nobody can pin anything else.

| Code | Language | Code | Language |
| --- | --- | --- | --- |
| `en` | English | `da` | Dansk |
| `fi` | Suomi | `cs` | Čeština |
| `de` | Deutsch | `hu` | Magyar |
| `es` | Español | `vi` | Tiếng Việt |
| `fr` | Français | `id` | Bahasa Indonesia |
| `zh_cn` | 简体中文 | `ar` | العربية |
| `ru` | Русский | `hi` | हिन्दी |
| `pt_br` | Português | `el` | Ελληνικά |
| `ja` | 日本語 | `sk` | Slovenčina |
| `pl` | Polski | `ro` | Română |
| `tr` | Türkçe | `bg` | Български |
| `it` | Italiano | `he` | עברית |
| `ko` | 한국어 | `th` | ไทย |
| `nl` | Nederlands | `ms` | Bahasa Melayu |
| `uk` | Українська | `lt` | Lietuvių |
| `sv` | Svenska | `lv` | Latviešu |
| `no` | Norsk | `et` | Eesti |
| `pt_pt` | Português | `hr` | Hrvatski |
| `zh_tw` | 繁體中文 | `sr` | Српски |
| `ca` | Català | `bs` | Bosanski |
| `gl` | Galego | `sl` | Slovenščina |
| `is` | Íslenska | `mk` | Македонски |
| `ga` | Gaeilge | `ka` | ქართული |
| `hy` | Հայերեն | `az` | Azərbaycanca |
| `kk` | Қазақша | `lol_us` | LOLCAT |
| `en_pt` | Pirate Speak | `qya_aa` | Quenya |

## sounds.yml

Sound effects for command feedback. Each entry supports `enabled`, a
namespaced `sound` key, `pitch` and `volume`
([sound explorer](https://jruk8.github.io/JSoundExplorer/)):

- `neutral` » neutral feedback for ordinary commands, heard by the executor
  and every affected player.
- `celebration` » played for reloads, help and version output.
- `angry` » played on failed commands (invalid input, missing permission,
  failed reload).

## Storage (SQLite)

The database file (default `jtemplate.db`) lives in the plugin's data folder
and is opened on enable, reloaded on `/jtemplate reload`, and closed on plugin
disable. The JDBC driver comes from the Minecraft server itself, so the plugin
jar stays small; if your server distribution does not bundle SQLite, the
plugin logs a warning and runs without state persistence.

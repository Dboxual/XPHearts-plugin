# XPHearts

A Paper 1.21 plugin for hybrid Java + Bedrock servers. Players earn extra hearts as their XP level grows, with a full XP economy layered on top.

Built for a private SMP. No external plugin dependencies required.

---

## Features

| Feature | Summary |
|---|---|
| **XP Hearts** | Extra hearts scale with XP level. Every 5 levels = +½ heart by default. Fully configurable. |
| **Grindstone XP Bottling** | Place an enchanted item in the top grindstone slot and a Glass Bottle in the bottom slot, then click the result to receive the disenchanted item plus a Bottle o' Enchanting. Shift-click a bottle into an open grindstone to auto-fill the bottom slot. |
| **XP Multiplier Charm** | Craft a charm, charge it by killing mobs, consume it to permanently gain +1× XP from kills. Stacks up to 10×. |
| **Rotten Flesh Smelting** | Smelt Rotten Flesh → Leather in any furnace, blast furnace, or smoker. |

---

## Commands

| Command | Description | Permission |
|---|---|---|
| `/xphearts reload` | Reload config and update all online players | `xphearts.reload` |
| `/xphearts check [player]` | Show level, hearts, and extra hearts | `xphearts.check` |
| `/xpmultiplier` | View your current XP multiplier | `xphearts.check` |
| `/xpmultiplier set <player> <n>` | Set a player's multiplier | `xphearts.admin` |
| `/xpmultiplier reset <player>` | Reset multiplier to 1× | `xphearts.admin` |

Aliases: `/xph`, `/xpm`

---

## Permissions

| Permission | Default | Description |
|---|---|---|
| `xphearts.reload` | op | Reload config |
| `xphearts.check` | op | Check hearts and multiplier |
| `xphearts.admin` | op | Manage player multipliers |

---

## Configuration

```yaml
# Hearts
half-hearts-enabled: true        # false = full hearts only (no 10.5, 11.5, etc.)
levels-per-half-heart: 5         # levels needed per +0.5 hearts
base-hearts: 10                  # starting hearts
max-hearts: 20                   # hard cap (20 hearts = 40 HP)

# Grindstone XP Bottling
grindstone-bottling:
  enabled: true
  bottles-per-dispatch: 1        # bottles given per use
  require-dispatch-xp: true      # only activate on enchanted items

# XP Multiplier Charm
multiplier:
  enabled: true
  max-multiplier: 10             # highest multiplier a player can reach
  charge-required: 100           # mob kills to fully charge a charm
  mob-kill-charge: 1             # charge gained per kill

# Rotten Flesh Smelting
rotten-flesh-smelting:
  enabled: true
  cooking-time: 200              # ticks in a standard furnace
  experience: 0.1                # XP rewarded on completion
```

All changes apply live with `/xphearts reload` — no restart needed.

---

## PlaceholderAPI

Optional. Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) to use these in scoreboards, tablist, chat, and holograms.

| Placeholder | Example | Notes |
|---|---|---|
| `%xphearts_multiplier%` | `3x` | |
| `%xphearts_multiplier_raw%` | `3` | Plain number |
| `%xphearts_hearts%` | `12` or `12.5` | Online players only |
| `%xphearts_extra_hearts%` | `+2` or `+2.5` | Online players only |

---

## XP Multiplier Charm Usage

**Crafting (default recipe):**
```
[ Amethyst ] [ Gold Ingot ] [ Amethyst ]
[ Gold Ingot ] [ Exp Bottle ] [ Gold Ingot ]
[ Amethyst ] [ Gold Ingot ] [ Amethyst ]
```
Recipe is configurable in `config.yml` under `multiplier.recipe`.

**Using:**
1. Hold the charm in your **offhand**.
2. Kill mobs to charge it (lore shows progress live).
3. When fully charged, **right-click** to consume it and gain +1× XP permanently.

---

## Building

```
./gradlew build          # Mac / Linux
gradlew.bat build        # Windows
```

Output: `build/libs/XPHearts-1.3.0.jar`

Requires JDK 21.

---

## Installing

1. Drop `XPHearts-1.3.0.jar` into your server's `plugins/` folder.
2. Restart the server.
3. Edit `plugins/XPHearts/config.yml`.
4. Run `/xphearts reload` to apply changes without restarting.

---

## Compatibility

- **Server:** Paper 1.21.11
- **Java:** 21
- **Bedrock:** Geyser compatible — no special configuration needed
- **PlaceholderAPI:** Optional soft-depend
- **Dependencies:** None

---

## Changelog

See [CHANGELOG.txt](CHANGELOG.txt) for full version history.

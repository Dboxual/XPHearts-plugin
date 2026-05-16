# XPHearts

A Paper 1.21 plugin for hybrid Java + Bedrock servers. Players earn extra hearts as their XP level grows, with a full XP economy layered on top.

Built for a private SMP. No external plugin dependencies required.

---

## Features

| Feature | Summary |
|---|---|
| **XP Hearts** | Extra hearts scale with XP level. Every 5 levels = +½ heart by default. Fully configurable. |
| **Grindstone XP Bottling** | Place an enchanted item in the top grindstone slot and a Glass Bottle in the bottom slot, then click the result to receive the disenchanted item plus a Bottle o' Enchanting. Shift-click a bottle into an open grindstone to auto-fill the bottom slot. |
| **Soul Bound Ledger** | Craft a Book and Quill charm, charge it by killing hostile mobs, then right-click (offhand) to consume it and permanently gain +0.5× XP from kills. Stacks up to 10×. Use `/withdraw` to extract multiplier as a tradeable NETHER_STAR token. |
| **Rotten Flesh Smelting** | Smelt Rotten Flesh → Leather in any furnace, blast furnace, or smoker. |

---

## Commands

| Command | Description | Permission |
|---|---|---|
| `/xphearts reload` | Reload config and update all online players | `xphearts.reload` |
| `/xphearts check [player]` | Show level, hearts, and extra hearts | `xphearts.check` |
| `/xpmultiplier` | View your current XP multiplier | `xphearts.check` |
| `/xpmultiplier set <player> <n>` | Set a player's multiplier (decimals allowed) | `xphearts.admin` |
| `/xpmultiplier reset <player>` | Reset multiplier to 1× | `xphearts.admin` |
| `/withdraw` | Withdraw 1.0× as a tradeable Multiplier Token (requires ≥ 2.0×) | `xphearts.withdraw` |

Aliases: `/xph`, `/xpm`

---

## Permissions

| Permission | Default | Description |
|---|---|---|
| `xphearts.reload` | op | Reload config |
| `xphearts.check` | op | Check hearts and multiplier |
| `xphearts.admin` | op | Manage player multipliers |
| `xphearts.withdraw` | true | Withdraw multiplier as a token |

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
  allow-passive-mobs: false      # true = any mob charges charm; false = hostile only
  withdraw-amount: 1.0           # multiplier removed (and stored in token) per /withdraw

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

## Soul Bound Ledger Usage

**Crafting (default recipe):**
```
[ Amethyst ] [ Gold Ingot ] [ Amethyst ]
[ Gold Ingot ] [ Exp Bottle ] [ Gold Ingot ]
[ Amethyst ] [ Gold Ingot ] [ Amethyst ]
```
Recipe is configurable in `config.yml` under `multiplier.recipe`.

**Using:**
1. Hold the Soul Bound Ledger in your **offhand**.
2. Kill hostile mobs to charge it — the lore shows progress live.
3. When fully charged, **right-click** to consume it and permanently gain +0.5× XP multiplier.

The book UI may open while it's in your hand — that's fine. Any text you type will not be saved. The item can never be signed or converted into a written book.

---

## Building

Gradle is not used for builds (incompatible with Java 25). See `CLAUDE.md` for the full manual javac build command.

Output: `build/releases/{version}/xphearts-{version}.jar`

Requires JDK 21.

---

## Installing

1. Drop `xphearts-1.4.0.jar` into your server's `plugins/` folder.
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

See [CHANGELOG.md](CHANGELOG.md) for full version history.

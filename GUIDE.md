# XPHearts

Earn extra hearts as you level up, bottle XP from the grindstone, and build a permanent XP multiplier through combat.

---

## Features

- **Extra hearts** that scale with your XP level
- **Grindstone XP bottling** — turn disenchanting XP into bottled XP
- **Soul Bound Ledger** — a chargeable charm that grants permanent XP multiplier
- **Multiplier Tokens** — withdraw and trade your multiplier with other players
- **Rotten Flesh smelting** — turn undead drops into leather

---

## How It Works

### Extra Hearts

The more XP levels you have, the more hearts you get.

- Every **5 levels** = **+0.5 hearts**
- Max is **20 hearts** (double your base health)
- Hearts update live as your level changes

### Grindstone XP Bottling

Instead of wasting XP when you disenchant something, bottle it.

1. Use a **Grindstone** to disenchant an item
2. Have a **Glass Bottle** in your offhand or inventory
3. Click the result slot — you receive a **Bottle o' Enchanting** instead of raw XP

### Soul Bound Ledger (XP Multiplier)

The **Soul Bound Ledger** is a **Wither Rose** charm that grants permanent XP multiplier when consumed.

**To use:**

1. Craft the Ledger (recipe below)
2. Hold it in your **offhand** while killing **hostile mobs**
3. Each kill charges it — it needs **100 kills** to fully charge
4. When fully charged, the name glows and reads **"❖ Fully charged!"**
5. **Right-click** to consume it — you permanently gain **+0.5x XP multiplier**

Each Ledger you consume stacks on top of your existing multiplier. The multiplier applies to all XP you earn from mob kills.

> Your multiplier never resets. It's permanent.

### Multiplier Tokens

You can extract part of your multiplier as a tradeable item.

- Use `/withdraw` to pull **1.0x** from your multiplier into a **Nether Star token**
- You must have at least **2.0x** to withdraw (you keep at least 1.0x)
- Anyone can apply a token by holding it in their **offhand and right-clicking**
- Tokens can be traded to other players freely

### Rotten Flesh → Leather

Smelt **Rotten Flesh** in any furnace, blast furnace, or smoker to get **Leather**. Simple as that.

---

## Soul Bound Ledger Recipe

```
A G A
G E G     A = Amethyst Shard  |  G = Gold Ingot  |  E = Bottle o' Enchanting
A G A
```

---

## Commands

| Command | Description |
|---|---|
| `/xphearts check` | Check your heart count and multiplier |
| `/xphearts reload` | Reload config (admin) |
| `/xpmultiplier` | View your current XP multiplier |
| `/withdraw` | Withdraw 1.0x multiplier as a tradeable token |

---

## Tips

- The Ledger only charges on **hostile mobs** — passive animals don't count
- You can have **multiple Ledgers** in progress at once (one in offhand, others in inventory don't charge)
- The enchantment glow on the Ledger is always visible so you can tell it apart from regular Wither Roses
- Your multiplier caps at **10x** — you cannot go above this
- Tokens are great for gifting or selling to other players

---

## Changelog Summary

**v1.4.2** — Soul Bound Ledger name changed to solid bright red bold text.

**v1.4.1** — Ledger item changed from a book to a **Wither Rose**. Cleaner, no book-editor UI.

**v1.4.0** — Introduced the Soul Bound Ledger system (replaced the old emerald charm).

**v1.3.4** — Configurable withdraw amount per token.

**v1.3.3** — `/withdraw` command added. Ledger now grants +0.5x per consume (was +1.0x). Passive mobs no longer charge the Ledger by default.

**v1.3.2** — Grindstone: enchanted books now correctly return a plain book when bottling.

**v1.3.1 and earlier** — Core system: XP hearts, grindstone bottling, charm system, rotten flesh smelting, PlaceholderAPI support.

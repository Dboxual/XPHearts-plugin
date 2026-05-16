# XPHearts Changelog

## v1.4.2

- **Soul Bound Ledger name is now all red bold** — changed from tricolor purple/magenta/aqua to solid `§c§l` (bright red bold) on both the uncharged and fully charged states. The gold ❖ on the charged state is unchanged.

---

## v1.4.1

- **Charm item is now `WITHER_ROSE`** — replaced `WRITABLE_BOOK` entirely. All book-specific handling removed: no migration logic, no `PlayerEditBookEvent` guard, no signing prevention.
- Uncharged rose right-clicks are now cancelled (prevents vanilla block-placement of the rose). Previously uncharged clicks were left uncancelled to allow the book UI to open.
- Enchantment glow remains on both charged and uncharged rose via `setEnchantmentGlintOverride(true)`.
- Kills charge the rose exactly as before. Fully charged right-click consumes and grants +0.5× multiplier.

---

## v1.4.0

- **Soul Bound Ledger** — the XP multiplier charm is now a `WRITABLE_BOOK` item called the Soul Bound Ledger. Bold colorful display name, flavour lore, enchantment glow. The book UI may open freely; saving or signing is permanently blocked so the item can never become a `WRITTEN_BOOK` and never loses its custom name, lore, or PDC data.
- Old EMERALD charm items automatically migrate to the new WRITABLE_BOOK format on first interaction, preserving their charge.
- Fully charged ledger right-click (offhand) cancels the interact event before consuming, so the book editor does not open in the same tick as the consume.
- Non-charged ledger right-clicks are no longer cancelled — the book UI can open for inspection.

---

## v1.3.4

- Configurable withdraw amount via `multiplier.withdraw-amount` in config.yml (default 1.0).
- Withdraw amount is stored in the token's PDC so tokens created with different settings carry their correct value.

---

## v1.3.3

- `/withdraw` command — extracts 1.0× multiplier as a tradeable NETHER_STAR Multiplier Token. Requires ≥ 2.0× to withdraw (so the player keeps at least 1.0×). Token can be applied by any player via offhand right-click.
- Charm now grants +0.5× per consume (was +1.0×).
- Charging restricted to hostile mobs by default (`multiplier.allow-passive-mobs: false`).

---

## v1.3.2

- Grindstone bottling: enchanted books now return a plain book alongside the XP bottle instead of disappearing.

---

## v1.3.1 and earlier

- XP-based extra hearts (configurable scaling, half-heart toggle, base/max heart caps).
- Grindstone XP bottling — place enchanted item + glass bottle, click result to bottle XP.
- XP multiplier charm system — craft, charge by killing mobs, consume for permanent multiplier boost.
- Rotten flesh → leather smelting in any furnace type.
- PlaceholderAPI support for multiplier, hearts, and extra hearts.
- `/xphearts reload` / `check`, `/xpmultiplier` view / set / reset commands.

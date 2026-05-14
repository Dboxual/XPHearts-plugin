package com.xphearts.feature;

import com.xphearts.XPHearts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CharmManager {

    private static final String CHARM_ID_VALUE = "xp_multiplier_charm";
    private static final String TOKEN_ID_VALUE  = "xp_multiplier_token";
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final XPHearts plugin;
    private final NamespacedKey charmIdKey;
    private final NamespacedKey charmChargeKey;
    private final NamespacedKey tokenIdKey;
    private final NamespacedKey tokenAmountKey;

    public CharmManager(XPHearts plugin) {
        this.plugin          = plugin;
        this.charmIdKey      = new NamespacedKey(plugin, "charm_id");
        this.charmChargeKey  = new NamespacedKey(plugin, "charm_charge");
        this.tokenIdKey      = new NamespacedKey(plugin, "token_id");
        this.tokenAmountKey  = new NamespacedKey(plugin, "token_amount");
        registerRecipe();
    }

    private static Component c(String text) {
        return LEGACY.deserialize(text);
    }

    // ── Charm ────────────────────────────────────────────────────────────────

    public ItemStack createCharm() {
        int required = plugin.getConfig().getInt("multiplier.charge-required", 100);
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(c("§5§lSoulbound Ledger"));
        meta.lore(List.of(
                c("§8The pages whisper quietly."),
                c("§7Souls cling to its binding."),
                c("§dMultiplier: §fx0/" + required)
        ));
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(charmIdKey, PersistentDataType.STRING, CHARM_ID_VALUE);
        meta.getPersistentDataContainer().set(charmChargeKey, PersistentDataType.INTEGER, 0);
        meta.setMaxStackSize(1);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isCharm(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        String id = item.getItemMeta().getPersistentDataContainer().get(charmIdKey, PersistentDataType.STRING);
        return CHARM_ID_VALUE.equals(id);
    }

    public int getCharge(ItemStack item) {
        if (!isCharm(item)) return 0;
        Integer charge = item.getItemMeta().getPersistentDataContainer().get(charmChargeKey, PersistentDataType.INTEGER);
        return charge != null ? charge : 0;
    }

    public void setCharge(ItemStack item, int charge) {
        if (!isCharm(item)) return;
        int required = plugin.getConfig().getInt("multiplier.charge-required", 100);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(charmChargeKey, PersistentDataType.INTEGER, charge);
        if (charge >= required) {
            meta.displayName(c("§5§lSoulbound Ledger §6❖ FULLY BOUND"));
            meta.lore(List.of(
                    c("§8The pages whisper quietly."),
                    c("§7Souls cling to its binding."),
                    c("§dMultiplier: §fx" + charge + "/" + required),
                    c("§6❖ Fully bound! §eRight-click to consume.")
            ));
        } else {
            meta.displayName(c("§5§lSoulbound Ledger"));
            meta.lore(List.of(
                    c("§8The pages whisper quietly."),
                    c("§7Souls cling to its binding."),
                    c("§dMultiplier: §fx" + charge + "/" + required)
            ));
        }
        item.setItemMeta(meta);
    }

    public boolean isFullyCharged(ItemStack item) {
        if (!isCharm(item)) return false;
        return getCharge(item) >= plugin.getConfig().getInt("multiplier.charge-required", 100);
    }

    // ── Withdraw Token ───────────────────────────────────────────────────────

    public ItemStack createWithdrawToken(double amount) {
        String label = fmt(amount);
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(c("§b+" + label + "x Multiplier Token"));
        meta.lore(List.of(
                c("§7Contains: §a+" + label + "x XP Multiplier"),
                c("§7Place in offhand and right-click to apply."),
                c("§7Can be traded to other players.")
        ));
        meta.getPersistentDataContainer().set(tokenIdKey,    PersistentDataType.STRING, TOKEN_ID_VALUE);
        meta.getPersistentDataContainer().set(tokenAmountKey, PersistentDataType.DOUBLE, amount);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isWithdrawToken(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        String id = item.getItemMeta().getPersistentDataContainer().get(tokenIdKey, PersistentDataType.STRING);
        return TOKEN_ID_VALUE.equals(id);
    }

    /** Returns the multiplier amount stored in a token. Falls back to 1.0 for tokens created before this was added. */
    public double getTokenAmount(ItemStack item) {
        if (!isWithdrawToken(item)) return 0.0;
        Double amount = item.getItemMeta().getPersistentDataContainer().get(tokenAmountKey, PersistentDataType.DOUBLE);
        return amount != null ? amount : 1.0;
    }

    /** Reads the configured withdraw-amount (default 1.0). */
    public double getWithdrawAmount() {
        return plugin.getConfig().getDouble("multiplier.withdraw-amount", 1.0);
    }

    private static String fmt(double v) {
        return v % 1 == 0 ? String.valueOf((int) v) : String.format("%.1f", v);
    }

    // ── Recipe ───────────────────────────────────────────────────────────────

    private void registerRecipe() {
        if (!plugin.getConfig().getBoolean("multiplier.recipe.enabled", true)) return;

        NamespacedKey key = new NamespacedKey(plugin, "xp_multiplier_charm");
        ShapedRecipe recipe = new ShapedRecipe(key, createCharm());

        List<String> shapeList = plugin.getConfig().getStringList("multiplier.recipe.shape");
        String[] shape = (shapeList.size() == 3)
                ? shapeList.toArray(new String[0])
                : new String[]{"AGA", "GEG", "AGA"};
        recipe.shape(shape);

        ConfigurationSection ingr = plugin.getConfig().getConfigurationSection("multiplier.recipe.ingredients");
        if (ingr != null) {
            for (String charKey : ingr.getKeys(false)) {
                String matName = ingr.getString(charKey, "");
                try {
                    recipe.setIngredient(charKey.charAt(0), Material.valueOf(matName.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Unknown material in charm recipe config: " + matName);
                }
            }
        } else {
            recipe.setIngredient('A', Material.AMETHYST_SHARD);
            recipe.setIngredient('G', Material.GOLD_INGOT);
            recipe.setIngredient('E', Material.EXPERIENCE_BOTTLE);
        }

        plugin.getServer().addRecipe(recipe);
    }
}

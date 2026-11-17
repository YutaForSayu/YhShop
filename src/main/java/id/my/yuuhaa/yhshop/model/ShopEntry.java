package id.my.yuuhaa.yhshop.model;

import id.my.yuuhaa.yhshop.util.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopEntry {
    private final String id;
    private String display;
    private double price;
    private int level;
    private int amount;
    private String icon;
    private int customModelData = -1;
    private boolean glow;
    private String tier;
    private List<String> lore;
    private String giveCommand;

    public ShopEntry(String id) {
        this.id = id;
        this.display = "&f" + id;
        this.price = 1000.0;
        this.level = 1;
        this.amount = 1;
        this.icon = "ENCHANTED_BOOK";
        this.customModelData = -1;
        this.glow = false;
        this.tier = "&7Baru";
        this.lore = new ArrayList<>();
        this.giveCommand = "";
    }

    public static ShopEntry from(String id, ConfigurationSection s) {
        ShopEntry e = new ShopEntry(id);
        e.display = s.getString("display", e.display);
        e.price = s.getDouble("price", e.price);
        e.level = s.getInt("level", e.level);
        e.amount = Math.max(1, s.getInt("amount", 1));
        e.icon = s.getString("icon", e.icon);
        e.customModelData = s.getInt("custom-model-data", -1);
        e.glow = s.getBoolean("glow", e.glow);
        e.tier = s.getString("tier", e.tier);
        e.lore = s.getStringList("lore");
        e.giveCommand = s.getString("give-command", "");
        return e;
    }

    public void saveTo(FileConfiguration cfg, String path) {
        cfg.set(path + ".display", display);
        cfg.set(path + ".price", price);
        cfg.set(path + ".level", level);
        cfg.set(path + ".amount", amount);
        cfg.set(path + ".icon", icon);
        if (customModelData > 0) cfg.set(path + ".custom-model-data", customModelData);
        cfg.set(path + ".glow", glow);
        cfg.set(path + ".tier", tier);
        cfg.set(path + ".lore", lore);
        cfg.set(path + ".give-command", giveCommand);
    }

    public ItemStack buildIcon(Economy eco, ShopType type) {
        Material mat = Material.matchMaterial(icon);
        if (mat == null) mat = type == ShopType.SPAWNER ? Material.SPAWNER : Material.ENCHANTED_BOOK;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(Chat.color(display));

        List<String> src = (lore == null || lore.isEmpty()) ? defaultLore(type) : lore;
        String priceText = eco == null ? String.valueOf(price) : eco.format(price);
        List<String> out = new ArrayList<>();
        for (String line : src) {
            out.add(Chat.color(line
                    .replace("%price%", priceText)
                    .replace("%id%", id)
                    .replace("%level%", String.valueOf(level))
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%tier%", tier == null ? "" : Chat.color(tier))));
        }
        meta.setLore(out);
        if (glow) {
            meta.addEnchant(Enchantment.INFINITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (customModelData > 0) meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        return item;
    }

    private List<String> defaultLore(ShopType type) {
        List<String> d = new ArrayList<>();
        d.add("&7Tier: &f%tier%");
        if (type == ShopType.ENCHANT) d.add("&7Level: &f%level%");
        d.add("&7Harga: &6%price%");
        d.add("&7Jumlah: &f%amount%");
        d.add("&7ID: &f%id%");
        d.add("");
        d.add("&eKlik &7untuk membeli!");
        return d;
    }

    public String resolveGiveCommand(Player p, String template) {
        String cmd = (giveCommand == null || giveCommand.isBlank()) ? template : giveCommand;
        return cmd.replace("%player%", p.getName())
                .replace("%id%", id)
                .replace("%level%", String.valueOf(level))
                .replace("%amount%", String.valueOf(amount));
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(1, amount);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public boolean isGlow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public String getGiveCommand() {
        return giveCommand;
    }

    public void setGiveCommand(String giveCommand) {
        this.giveCommand = giveCommand;
    }
}

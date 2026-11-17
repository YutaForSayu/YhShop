package id.my.yuuhaa.yhshop.gui;

import id.my.yuuhaa.yhshop.YhShop;
import id.my.yuuhaa.yhshop.util.Chat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class Items {
    private Items() {
    }

    public static NamespacedKey key(YhShop plugin, String k) {
        return new NamespacedKey(plugin, k);
    }

    public static ItemStack tag(ItemStack item, String key, String value) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key(YhShop.inst(), key), PersistentDataType.STRING, value);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static String tag(ItemStack item, String key) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(key(YhShop.inst(), key), PersistentDataType.STRING);
    }

    public static ItemStack glass(YhShop plugin) {
        String mat = plugin.getConfig().getString("gui.fill-item", "GRAY_STAINED_GLASS_PANE");
        String name = plugin.getConfig().getString("gui.fill-name", " ");
        Material m = Material.matchMaterial(mat);
        if (m == null) m = Material.GRAY_STAINED_GLASS_PANE;
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Chat.color(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack named(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Chat.color(name));
            if (lore != null) meta.setLore(Chat.colorList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}

package id.my.yuuhaa.yhshop.gui;

import id.my.yuuhaa.yhshop.YhShop;
import id.my.yuuhaa.yhshop.manager.InputManager;
import id.my.yuuhaa.yhshop.model.ShopEntry;
import id.my.yuuhaa.yhshop.model.ShopType;
import id.my.yuuhaa.yhshop.util.Chat;
import id.my.yuuhaa.yhshop.util.Msg;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AdminListGui extends AbstractGui {
    private static final int PAGE_SIZE = 45;

    private final ShopType type;
    private final int page;

    public AdminListGui(YhShop plugin, Player player, ShopType type, int page) {
        super(plugin, player);
        this.type = type;
        this.page = page;
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    public String title() {
        return "&c&lYhShop &8» &7Admin";
    }

    @Override
    public void build(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, Items.glass(plugin));

        List<ShopEntry> all = plugin.shops().entries(type);
        int maxPage = Math.max(1, (int) Math.ceil((double) all.size() / PAGE_SIZE));
        int from = page * PAGE_SIZE;
        for (int i = 0; i < PAGE_SIZE && from + i < all.size(); i++) {
            ShopEntry e = all.get(from + i);
            String cmd = e.getGiveCommand();
            inv.setItem(i, Items.tag(e.buildIcon(plugin.getEcon(), type), "entry_id", e.getId()));
            // lore entry admin ditambahkan di bawah lewat meta langsung
            ItemStack shown = inv.getItem(i);
            if (shown != null && shown.hasItemMeta()) {
                var meta = shown.getItemMeta();
                var lore = meta.hasLore() ? new java.util.ArrayList<>(meta.getLore()) : new java.util.ArrayList<String>();
                lore.add("");
                lore.add(Chat.color("&cKlik &7untuk edit entry ini"));
                meta.setLore(lore);
                shown.setItemMeta(meta);
            }
        }

        inv.setItem(45, Items.tag(Items.named(Material.ARROW,
                plugin.getConfig().getString("gui.back-name", "&cKembali"), null), "action", "back"));
        inv.setItem(46, Items.tag(Items.named(Material.ARROW,
                plugin.getConfig().getString("gui.prev-name", "&eSebelumnya"), null), "action", "prev"));
        inv.setItem(48, Items.tag(Items.named(Material.SPAWNER,
                "&b&lShop Spawner" + (type == ShopType.SPAWNER ? " &a✔" : ""),
                List.of("&7Lihat / edit entry spawner.")), "action", "toggle_spawner"));
        inv.setItem(49, Items.tag(Items.named(Material.LIME_DYE, "&a&l＋ Entry Baru",
                List.of("&7Tambah entry baru di shop ini.")), "action", "add"));
        inv.setItem(50, Items.tag(Items.named(Material.ENCHANTED_BOOK,
                "&d&lShop Enchant" + (type == ShopType.ENCHANT ? " &a✔" : ""),
                List.of("&7Lihat / edit entry enchant.")), "action", "toggle_enchant"));
        inv.setItem(52, Items.tag(Items.named(Material.ARROW,
                plugin.getConfig().getString("gui.next-name", "&eBerikutnya"), null), "action", "next"));
        inv.setItem(53, Items.named(Material.PAPER,
                plugin.getConfig().getString("gui.page-name", "&7Halaman %page%/%max%")
                        .replace("%page%", String.valueOf(page + 1))
                        .replace("%max%", String.valueOf(maxPage)), null));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        String action = Items.tag(item, "action");
        if (action != null) {
            switch (action) {
                case "prev" -> {
                    if (page > 0) new AdminListGui(plugin, player, type, page - 1).open();
                }
                case "next" -> {
                    if ((page + 1) * PAGE_SIZE < plugin.shops().entries(type).size()) {
                        new AdminListGui(plugin, player, type, page + 1).open();
                    }
                }
                case "back" -> new MainMenu(plugin, player).open();
                case "toggle_spawner" -> new AdminListGui(plugin, player, ShopType.SPAWNER, 0).open();
                case "toggle_enchant" -> new AdminListGui(plugin, player, ShopType.ENCHANT, 0).open();
                case "add" -> {
                    player.closeInventory();
                    InputManager.Session s = new InputManager.Session(type, InputManager.Field.NEW_ID, null);
                    plugin.inputs().start(player, s);
                    Msg.send(player, "input_new_id");
                }
            }
            return;
        }
        String id = Items.tag(item, "entry_id");
        if (id == null) return;
        new AdminDetailGui(plugin, player, type, id, page).open();
    }

}

package id.my.yuuhaa.yhshop.gui;

import id.my.yuuhaa.yhshop.YhShop;
import id.my.yuuhaa.yhshop.model.ShopEntry;
import id.my.yuuhaa.yhshop.model.ShopType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopGui extends AbstractGui {
    private static final int PAGE_SIZE = 45;

    private final ShopType type;
    private final int page;

    public ShopGui(YhShop plugin, Player player, ShopType type, int page) {
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
        return type == ShopType.SPAWNER ? "&b&lYhShop &8» &7Spawner" : "&d&lYhShop &8» &7Enchants";
    }

    @Override
    public void build(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, Items.glass(plugin));

        List<ShopEntry> all = plugin.shops().entries(type);
        int maxPage = Math.max(1, (int) Math.ceil((double) all.size() / PAGE_SIZE));
        int from = page * PAGE_SIZE;
        for (int i = 0; i < PAGE_SIZE && from + i < all.size(); i++) {
            ShopEntry e = all.get(from + i);
            inv.setItem(i, Items.tag(e.buildIcon(plugin.getEcon(), type), "entry_id", e.getId()));
        }

        inv.setItem(46, Items.tag(Items.named(Material.ARROW,
                plugin.getConfig().getString("gui.prev-name", "&eSebelumnya"), null), "action", "prev"));
        inv.setItem(48, Items.tag(Items.named(
                Material.matchMaterial(plugin.getConfig().getString("gui.back-item", "ARROW")),
                plugin.getConfig().getString("gui.back-name", "&cKembali"), null), "action", "back"));
        inv.setItem(50, Items.tag(Items.named(Material.ARROW,
                plugin.getConfig().getString("gui.next-name", "&eBerikutnya"), null), "action", "next"));
        inv.setItem(52, Items.named(Material.PAPER,
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
                    if (page > 0) new ShopGui(plugin, player, type, page - 1).open();
                }
                case "next" -> {
                    if ((page + 1) * PAGE_SIZE < plugin.shops().entries(type).size()) {
                        new ShopGui(plugin, player, type, page + 1).open();
                    }
                }
                case "back" -> new MainMenu(plugin, player).open();
            }
            return;
        }
        String id = Items.tag(item, "entry_id");
        if (id == null) return;
        ShopEntry entry = plugin.shops().get(type, id);
        if (entry == null) return;
        if (plugin.shops().buy(player, type, entry)) {
            new ShopGui(plugin, player, type, page).open();
        }
    }
}

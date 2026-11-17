package id.my.yuuhaa.yhshop.gui;

import id.my.yuuhaa.yhshop.YhShop;
import id.my.yuuhaa.yhshop.model.ShopType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MainMenu extends AbstractGui {
    public MainMenu(YhShop plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public String title() {
        return "&b&lYhShop &8| &7Menu";
    }

    @Override
    public void build(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, Items.glass(plugin));

        inv.setItem(11, Items.tag(Items.named(Material.SPAWNER, "&b&lSHOP SPAWNER", List.of(
                "&7Beli spawner mob dari",
                "&7SmartSpawner.",
                "",
                "&eKlik &7untuk membuka shop!"
        )), "action", "spawner"));

        inv.setItem(15, Items.tag(Items.named(Material.ENCHANTED_BOOK, "&d&lSHOP ENCHANTS", List.of(
                "&7Beli buku enchant dari",
                "&7ExcellentEnchants (80+ enchant).",
                "",
                "&eKlik &7untuk membuka shop!"
        )), "action", "enchant"));

        if (player.hasPermission("yhshop1.commands.admin")) {
            inv.setItem(13, Items.tag(Items.named(Material.COMMAND_BLOCK, "&c&lADMIN SHOP", List.of(
                    "&7Edit harga & entry",
                    "&7secara realtime di game."
            )), "action", "admin"));
        }
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        String action = Items.tag(e.getCurrentItem(), "action");
        if (action == null) return;
        switch (action) {
            case "spawner" -> new ShopGui(plugin, player, ShopType.SPAWNER, 0).open();
            case "enchant" -> new ShopGui(plugin, player, ShopType.ENCHANT, 0).open();
            case "admin" -> new AdminListGui(plugin, player, ShopType.SPAWNER, 0).open();
        }
    }
}

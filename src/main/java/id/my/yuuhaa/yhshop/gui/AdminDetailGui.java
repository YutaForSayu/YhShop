package id.my.yuuhaa.yhshop.gui;

import id.my.yuuhaa.yhshop.YhShop;
import id.my.yuuhaa.yhshop.manager.InputManager;
import id.my.yuuhaa.yhshop.model.ShopEntry;
import id.my.yuuhaa.yhshop.model.ShopType;
import id.my.yuuhaa.yhshop.util.Msg;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AdminDetailGui extends AbstractGui {
    private final ShopType type;
    private final String id;
    private final int page;

    public AdminDetailGui(YhShop plugin, Player player, ShopType type, String id, int page) {
        super(plugin, player);
        this.type = type;
        this.id = id;
        this.page = page;
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public String title() {
        return "&c&lYhShop &8» &7Edit Entry";
    }

    @Override
    public void build(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, Items.glass(plugin));
        ShopEntry e = plugin.shops().get(type, id);
        if (e == null) {
            player.closeInventory();
            Msg.send(player, "entry_not_found", "%id%", id);
            return;
        }

        inv.setItem(13, e.buildIcon(plugin.getEcon(), type));

        inv.setItem(10, Items.tag(Items.named(Material.PAPER, "&eEdit Harga", List.of(
                "&7Harga saat ini: &6" + plugin.getEcon().format(e.getPrice()),
                "",
                "&eKlik &7untuk mengubah di chat"
        )), "action", "edit_price"));

        if (type == ShopType.ENCHANT) {
            inv.setItem(11, Items.tag(Items.named(Material.NAME_TAG, "&eEdit Level", List.of(
                    "&7Level saat ini: &f" + e.getLevel(),
                    "",
                    "&eKlik &7untuk mengubah di chat"
            )), "action", "edit_level"));
        }

        inv.setItem(12, Items.tag(Items.named(Material.NAME_TAG, "&eEdit Nama Display", List.of(
                "&7Nama saat ini: &f" + e.getDisplay(),
                "",
                "&eKlik &7untuk mengubah di chat"
        )), "action", "edit_display"));

        String cmd = e.getGiveCommand();
        inv.setItem(16, Items.tag(Items.named(Material.COMMAND_BLOCK, "&eEdit Perintah Give", List.of(
                "&7Perintah: &f" + (cmd == null || cmd.isBlank()
                        ? "(template default: " + plugin.shops().defaultCommand(type) + ")"
                        : cmd),
                "",
                "&eKlik &7untuk mengubah di chat"
        )), "action", "edit_command"));

        inv.setItem(14, Items.tag(Items.named(Material.TNT, "&cHapus Entry", List.of(
                "&7Klik untuk menghapus entry ini.",
                "&cTidak bisa dibatalkan!"
        )), "action", "delete"));

        inv.setItem(22, Items.tag(Items.named(Material.ARROW,
                plugin.getConfig().getString("gui.back-name", "&cKembali"), null), "action", "back"));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        String action = Items.tag(item, "action");
        if (action == null) return;

        ShopEntry entry = plugin.shops().get(type, id);
        if (entry == null) {
            Msg.send(player, "entry_not_found", "%id%", id);
            return;
        }

        switch (action) {
            case "edit_price" -> {
                player.closeInventory();
                InputManager.Session s = new InputManager.Session(type, InputManager.Field.PRICE, id);
                plugin.inputs().start(player, s);
                Msg.send(player, "input_price", "%id%", id);
            }
            case "edit_level" -> {
                player.closeInventory();
                InputManager.Session s = new InputManager.Session(type, InputManager.Field.LEVEL, id);
                plugin.inputs().start(player, s);
                Msg.send(player, "input_level", "%id%", id);
            }
            case "edit_display" -> {
                player.closeInventory();
                InputManager.Session s = new InputManager.Session(type, InputManager.Field.DISPLAY, id);
                plugin.inputs().start(player, s);
                Msg.send(player, "input_display", "%id%", id);
            }
            case "edit_command" -> {
                player.closeInventory();
                InputManager.Session s = new InputManager.Session(type, InputManager.Field.COMMAND, id);
                plugin.inputs().start(player, s);
                Msg.send(player, "input_command", "%id%", id);
            }
            case "delete" -> {
                plugin.shops().removeEntry(type, id);
                Msg.send(player, "entry_deleted", "%id%", id);
                new AdminListGui(plugin, player, type, page).open();
            }
            case "back" -> new AdminListGui(plugin, player, type, page).open();
        }
    }
}

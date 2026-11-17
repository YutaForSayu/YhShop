package id.my.yuuhaa.yhshop.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface Gui {
    int size();

    String title();

    void build(Inventory inv);

    void onClick(InventoryClickEvent event);
}

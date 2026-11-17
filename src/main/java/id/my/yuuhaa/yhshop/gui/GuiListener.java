package id.my.yuuhaa.yhshop.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GuiListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getView().getTopInventory().getHolder() instanceof GuiHolder holder)) return;
        e.setCancelled(true);
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        holder.getGui().onClick(e);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof GuiHolder) {
            e.setCancelled(true);
        }
    }
}

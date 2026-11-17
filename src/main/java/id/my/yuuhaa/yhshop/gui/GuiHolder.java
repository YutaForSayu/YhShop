package id.my.yuuhaa.yhshop.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class GuiHolder implements InventoryHolder {
    private final Gui gui;
    private Inventory inventory;

    public GuiHolder(Gui gui) {
        this.gui = gui;
    }

    public Gui getGui() {
        return gui;
    }

    public void setInventory(Inventory inv) {
        this.inventory = inv;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}

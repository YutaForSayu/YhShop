package id.my.yuuhaa.yhshop.gui;

import id.my.yuuhaa.yhshop.YhShop;
import id.my.yuuhaa.yhshop.util.Chat;
import id.my.yuuhaa.yhshop.util.Sfx;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class AbstractGui implements Gui {
    protected final YhShop plugin;
    protected final Player player;

    protected AbstractGui(YhShop plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        GuiHolder holder = new GuiHolder(this);
        Inventory inv = Bukkit.createInventory(holder, size(), Chat.color(title()));
        holder.setInventory(inv);
        build(inv);
        player.openInventory(inv);
        Sfx.click(player, plugin.getConfig().getBoolean("sounds", true));
    }

    protected boolean soundsOn() {
        return plugin.getConfig().getBoolean("sounds", true);
    }
}

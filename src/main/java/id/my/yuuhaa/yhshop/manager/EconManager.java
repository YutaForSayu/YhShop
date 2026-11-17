package id.my.yuuhaa.yhshop.manager;

import id.my.yuuhaa.yhshop.YhShop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconManager implements Listener {
    private final YhShop plugin;
    private Economy econ;

    public EconManager(YhShop plugin) {
        this.plugin = plugin;
    }

    public void init() {
        RegisteredServiceProvider<Economy> rsp =
                plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) econ = rsp.getProvider();
    }

    @EventHandler
    public void onRegister(ServiceRegisterEvent e) {
        if (econ == null && e.getProvider() instanceof Economy ec) {
            econ = ec;
            plugin.getLogger().info("Economy terhubung (late): " + ec.getName());
        }
    }

    public Economy get() {
        return econ;
    }

    public boolean ok() {
        return econ != null;
    }
}

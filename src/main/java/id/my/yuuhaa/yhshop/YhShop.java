package id.my.yuuhaa.yhshop;

import id.my.yuuhaa.yhshop.command.YhShopCommand;
import id.my.yuuhaa.yhshop.gui.GuiListener;
import id.my.yuuhaa.yhshop.manager.EconManager;
import id.my.yuuhaa.yhshop.manager.InputManager;
import id.my.yuuhaa.yhshop.manager.ShopManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class YhShop extends JavaPlugin {
    private static YhShop inst;

    private ShopManager shopManager;
    private InputManager inputManager;
    private EconManager econManager;
    private FileConfiguration messages;

    @Override
    public void onEnable() {
        inst = this;

        saveDefaultConfig();
        saveResourceIfAbsent("messages.yml");
        saveResourceIfAbsent("spawners.yml");
        saveResourceIfAbsent("enchants.yml");
        loadMessages();

        this.shopManager = new ShopManager(this);
        this.shopManager.load();
        this.inputManager = new InputManager(this);
        this.econManager = new EconManager(this);

        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(this.inputManager, this);
        getServer().getPluginManager().registerEvents(this.econManager, this);

        var cmd = getCommand("yhshop");
        if (cmd != null) {
            YhShopCommand exec = new YhShopCommand(this);
            cmd.setExecutor(exec);
            cmd.setTabCompleter(exec);
        }

        econManager.init();
        if (!econManager.ok()) {
            getLogger().warning("Vault economy belum terpasang, coba cek lagi dalam 5 detik...");
            getServer().getScheduler().runTaskLater(this, () -> {
                econManager.init();
                if (!econManager.ok()) {
                    getLogger().severe("Economy tidak ditemukan! Pasang Vault + Essentials. Plugin dimatikan.");
                    getServer().getPluginManager().disablePlugin(this);
                } else {
                    getLogger().info("Economy terhubung (late): " + econManager.get().getName());
                }
            }, 100L);
        } else {
            getLogger().info("Economy terhubung: " + econManager.get().getName());
        }

        getLogger().info("YhShop: Spawner & Enchants Edition aktif!");
    }

    @Override
    public void onDisable() {
        if (this.inputManager != null) this.inputManager.clearAll();
        inst = null;
    }

    public void loadMessages() {
        File f = new File(getDataFolder(), "messages.yml");
        if (!f.exists()) saveResource("messages.yml", false);
        this.messages = YamlConfiguration.loadConfiguration(f);
    }

    private void saveResourceIfAbsent(String name) {
        File f = new File(getDataFolder(), name);
        if (!f.exists()) saveResource(name, false);
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public ShopManager shops() {
        return shopManager;
    }

    public InputManager inputs() {
        return inputManager;
    }

    public EconManager econManager() {
        return econManager;
    }

    public Economy getEcon() {
        return econManager == null ? null : econManager.get();
    }

    public static YhShop inst() {
        return inst;
    }
}

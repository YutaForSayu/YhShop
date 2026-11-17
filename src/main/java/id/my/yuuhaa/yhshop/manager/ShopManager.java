package id.my.yuuhaa.yhshop.manager;

import id.my.yuuhaa.yhshop.YhShop;
import id.my.yuuhaa.yhshop.model.ShopEntry;
import id.my.yuuhaa.yhshop.model.ShopType;
import id.my.yuuhaa.yhshop.util.Chat;
import id.my.yuuhaa.yhshop.util.Msg;
import id.my.yuuhaa.yhshop.util.Sfx;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShopManager {
    private final YhShop plugin;
    private final Map<ShopType, LinkedHashMap<String, ShopEntry>> entries = new EnumMap<>(ShopType.class);
    private File spawnersFile;
    private File enchantsFile;
    private FileConfiguration spawnersCfg;
    private FileConfiguration enchantsCfg;

    public ShopManager(YhShop plugin) {
        this.plugin = plugin;
    }

    public void load() {
        spawnersFile = new File(plugin.getDataFolder(), "spawners.yml");
        enchantsFile = new File(plugin.getDataFolder(), "enchants.yml");
        spawnersCfg = YamlConfiguration.loadConfiguration(spawnersFile);
        enchantsCfg = YamlConfiguration.loadConfiguration(enchantsFile);
        entries.clear();
        entries.put(ShopType.SPAWNER, loadSection(spawnersCfg, "spawners"));
        entries.put(ShopType.ENCHANT, loadSection(enchantsCfg, "enchants"));
        plugin.getLogger().info("Loaded " + entries.get(ShopType.SPAWNER).size() + " spawner(s), "
                + entries.get(ShopType.ENCHANT).size() + " enchant book(s).");
    }

    private LinkedHashMap<String, ShopEntry> loadSection(FileConfiguration cfg, String root) {
        LinkedHashMap<String, ShopEntry> map = new LinkedHashMap<>();
        ConfigurationSection sec = cfg.getConfigurationSection(root);
        if (sec == null) return map;
        for (String id : sec.getKeys(false)) {
            ConfigurationSection s = sec.getConfigurationSection(id);
            if (s == null) continue;
            map.put(id, ShopEntry.from(id, s));
        }
        return map;
    }

    public List<ShopEntry> entries(ShopType type) {
        return new ArrayList<>(entries.getOrDefault(type, new LinkedHashMap<>()).values());
    }

    public ShopEntry get(ShopType type, String id) {
        LinkedHashMap<String, ShopEntry> map = entries.get(type);
        return map == null ? null : map.get(id);
    }

    public void setEntry(ShopType type, String id, ShopEntry entry) {
        entries.computeIfAbsent(type, k -> new LinkedHashMap<>()).put(id, entry);
        save(type);
    }

    public void removeEntry(ShopType type, String id) {
        LinkedHashMap<String, ShopEntry> map = entries.get(type);
        if (map != null) map.remove(id);
        save(type);
    }

    public String defaultCommand(ShopType type) {
        return plugin.getConfig().getString("give-commands."
                + (type == ShopType.SPAWNER ? "spawner" : "enchant"));
    }

    public void save(ShopType type) {
        boolean spawner = type == ShopType.SPAWNER;
        File file = spawner ? spawnersFile : enchantsFile;
        FileConfiguration cfg = spawner ? spawnersCfg : enchantsCfg;
        String root = spawner ? "spawners" : "enchants";
        cfg.set(root, null);
        for (Map.Entry<String, ShopEntry> en : entries.get(type).entrySet()) {
            en.getValue().saveTo(cfg, root + "." + en.getKey());
        }
        try {
            cfg.save(file);
        } catch (IOException ex) {
            plugin.getLogger().severe("Gagal menyimpan " + file.getName() + ": " + ex.getMessage());
        }
    }

    public boolean buy(Player p, ShopType type, ShopEntry e) {
        Economy eco = plugin.getEcon();
        if (eco == null) {
            Msg.send(p, "no_eco");
            return false;
        }
        if (!eco.has(p, e.getPrice())) {
            Msg.send(p, "not_enough",
                    "%need%", eco.format(e.getPrice()),
                    "%have%", eco.format(eco.getBalance(p)));
            Sfx.error(p, plugin.getConfig().getBoolean("sounds", true));
            return false;
        }
        eco.withdrawPlayer(p, e.getPrice());
        String cmd = e.resolveGiveCommand(p, defaultCommand(type));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        Msg.send(p, "bought",
                "%item%", Chat.strip(e.getDisplay()),
                "%price%", eco.format(e.getPrice()));
        Sfx.buy(p, plugin.getConfig().getBoolean("sounds", true));
        return true;
    }
}

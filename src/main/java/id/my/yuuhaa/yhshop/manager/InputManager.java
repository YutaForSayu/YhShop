package id.my.yuuhaa.yhshop.manager;

import id.my.yuuhaa.yhshop.YhShop;
import id.my.yuuhaa.yhshop.gui.AdminDetailGui;
import id.my.yuuhaa.yhshop.gui.AdminListGui;
import id.my.yuuhaa.yhshop.model.ShopEntry;
import id.my.yuuhaa.yhshop.model.ShopType;
import id.my.yuuhaa.yhshop.util.Msg;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InputManager implements Listener {
    public enum Field { PRICE, COMMAND, DISPLAY, LEVEL, NEW_ID, NEW_PRICE, NEW_LEVEL }

    public static final class Session {
        public final ShopType shop;
        public Field field;
        public String id;
        public String newId;
        public double price;
        public int level = 1;

        public Session(ShopType shop, Field field, String id) {
            this.shop = shop;
            this.field = field;
            this.id = id;
        }
    }

    private final YhShop plugin;
    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    public InputManager(YhShop plugin) {
        this.plugin = plugin;
    }

    public void start(Player p, Session s) {
        sessions.put(p.getUniqueId(), s);
    }

    public void clearAll() {
        sessions.clear();
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        Session s = sessions.remove(e.getPlayer().getUniqueId());
        if (s == null) return;
        e.setCancelled(true);
        String text = MiniMessage.miniMessage().serialize(e.originalMessage()).trim();
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> apply(p, s, text));
    }

    private void apply(Player p, Session s, String text) {
        if (text.equalsIgnoreCase("batal")) {
            Msg.send(p, "cancelled");
            return;
        }
        ShopManager m = plugin.shops();
        switch (s.field) {
            case PRICE -> {
                ShopEntry en = m.get(s.shop, s.id);
                if (en == null) {
                    Msg.send(p, "entry_not_found", "%id%", s.id);
                    return;
                }
                try {
                    double v = Double.parseDouble(text);
                    if (v < 0) throw new NumberFormatException();
                    en.setPrice(v);
                    m.save(s.shop);
                    String priceText = plugin.getEcon() == null ? String.valueOf(v) : plugin.getEcon().format(v);
                    Msg.send(p, "price_set",
                            "%item%", en.getDisplay(),
                            "%price%", priceText);
                    new AdminDetailGui(plugin, p, s.shop, s.id, 0).open();
                } catch (NumberFormatException ex) {
                    Msg.send(p, "price_invalid", "%input%", text);
                }
            }
            case COMMAND -> {
                ShopEntry en = m.get(s.shop, s.id);
                if (en == null) {
                    Msg.send(p, "entry_not_found", "%id%", s.id);
                    return;
                }
                en.setGiveCommand(text);
                m.save(s.shop);
                Msg.send(p, "cmd_set", "%id%", s.id);
                new AdminDetailGui(plugin, p, s.shop, s.id, 0).open();
            }
            case DISPLAY -> {
                ShopEntry en = m.get(s.shop, s.id);
                if (en == null) {
                    Msg.send(p, "entry_not_found", "%id%", s.id);
                    return;
                }
                en.setDisplay(text);
                m.save(s.shop);
                Msg.send(p, "display_set", "%id%", s.id);
                new AdminDetailGui(plugin, p, s.shop, s.id, 0).open();
            }
            case LEVEL -> {
                ShopEntry en = m.get(s.shop, s.id);
                if (en == null) {
                    Msg.send(p, "entry_not_found", "%id%", s.id);
                    return;
                }
                try {
                    int lv = Integer.parseInt(text);
                    en.setLevel(lv);
                    m.save(s.shop);
                    Msg.send(p, "level_set", "%id%", s.id, "%level%", String.valueOf(lv));
                    new AdminDetailGui(plugin, p, s.shop, s.id, 0).open();
                } catch (NumberFormatException ex) {
                    Msg.send(p, "level_invalid", "%input%", text);
                }
            }
            case NEW_ID -> {
                if (m.get(s.shop, text) != null) {
                    Msg.send(p, "entry_exists", "%id%", text);
                    return;
                }
                s.newId = text;
                s.field = Field.NEW_PRICE;
                Msg.send(p, "input_new_price", "%id%", text);
            }
            case NEW_PRICE -> {
                try {
                    double v = Double.parseDouble(text);
                    if (v < 0) throw new NumberFormatException();
                    s.price = v;
                    if (s.shop == ShopType.ENCHANT) {
                        s.field = Field.NEW_LEVEL;
                        Msg.send(p, "input_new_level", "%id%", s.newId);
                    } else {
                        createEntry(p, s, 1);
                    }
                } catch (NumberFormatException ex) {
                    Msg.send(p, "price_invalid", "%input%", text);
                }
            }
            case NEW_LEVEL -> {
                try {
                    int lv = Integer.parseInt(text);
                    createEntry(p, s, lv);
                } catch (NumberFormatException ex) {
                    Msg.send(p, "level_invalid", "%input%", text);
                }
            }
        }
    }

    private void createEntry(Player p, Session s, int level) {
        ShopManager m = plugin.shops();
        ShopEntry e = new ShopEntry(s.newId);
        e.setPrice(s.price);
        e.setLevel(level);
        e.setIcon(s.shop == ShopType.SPAWNER ? "SPAWNER" : "ENCHANTED_BOOK");
        e.setGlow(s.shop == ShopType.ENCHANT);
        m.setEntry(s.shop, s.newId, e);
        Msg.send(p, "entry_added", "%id%", s.newId);
        new AdminListGui(plugin, p, s.shop, 0).open();
    }
}

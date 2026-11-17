package id.my.yuuhaa.yhshop.util;

import id.my.yuuhaa.yhshop.YhShop;
import org.bukkit.entity.Player;

public final class Msg {
    private Msg() {
    }

    public static String raw(String key) {
        String s = YhShop.inst().getMessages().getString(key);
        if (s == null) s = key;
        return Chat.color(s);
    }

    public static void send(Player p, String key, String... kv) {
        String m = raw(key);
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m = m.replace(kv[i], kv[i + 1]);
        }
        p.sendMessage(raw("prefix") + m);
    }
}

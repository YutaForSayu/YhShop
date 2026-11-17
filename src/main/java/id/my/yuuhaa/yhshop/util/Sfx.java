package id.my.yuuhaa.yhshop.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class Sfx {
    private Sfx() {
    }

    public static void click(Player p, boolean on) {
        if (on) p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
    }

    public static void buy(Player p, boolean on) {
        if (on) p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
    }

    public static void error(Player p, boolean on) {
        if (on) p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
    }
}

package id.my.yuuhaa.yhshop.util;

import org.bukkit.ChatColor;

public final class Chat {
    private Chat() {
    }

    public static String color(String s) {
        return s == null ? "" : ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String strip(String s) {
        return s == null ? "" : ChatColor.stripColor(color(s));
    }

    public static java.util.List<String> colorList(java.util.List<String> list) {
        if (list == null) return new java.util.ArrayList<>();
        return list.stream().map(Chat::color).toList();
    }
}

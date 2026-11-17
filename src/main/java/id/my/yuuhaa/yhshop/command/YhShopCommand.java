package id.my.yuuhaa.yhshop.command;

import id.my.yuuhaa.yhshop.YhShop;
import id.my.yuuhaa.yhshop.gui.AdminListGui;
import id.my.yuuhaa.yhshop.gui.MainMenu;
import id.my.yuuhaa.yhshop.gui.ShopGui;
import id.my.yuuhaa.yhshop.model.ShopEntry;
import id.my.yuuhaa.yhshop.model.ShopType;
import id.my.yuuhaa.yhshop.util.Chat;
import id.my.yuuhaa.yhshop.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YhShopCommand implements CommandExecutor, TabCompleter {
    private static final String[] SUBS = {"spawner", "enchant", "admin", "reload"};
    private static final String[] ADMIN_SUBS = {"gui", "setprice", "givecmd", "add", "remove"};

    private final YhShop plugin;

    public YhShopCommand(YhShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(Chat.color(Msg.raw("only_player")));
                return true;
            }
            if (!p.hasPermission("yhshop1.command.use")) {
                Msg.send(p, "no_permission");
                return true;
            }
            new MainMenu(plugin, p).open();
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "spawner" -> openShop(sender, ShopType.SPAWNER);
            case "enchant", "enchants" -> openShop(sender, ShopType.ENCHANT);
            case "admin" -> handleAdmin(sender, args);
            case "reload" -> {
                if (!checkPerm(sender)) return true;
                plugin.reloadConfig();
                plugin.loadMessages();
                plugin.shops().load();
                sender.sendMessage(Chat.color(Msg.raw("prefix") + Msg.raw("reloaded")));
            }
            default -> sender.sendMessage(Chat.color(Msg.raw("invalid_usage")));
        }
        return true;
    }

    private boolean checkPerm(CommandSender s) {
        if (!s.hasPermission("yhshop1.commands.admin")) {
            s.sendMessage(Chat.color(Msg.raw("prefix") + Msg.raw("no_permission")));
            return false;
        }
        return true;
    }

    private void openShop(CommandSender sender, ShopType type) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Chat.color(Msg.raw("only_player")));
            return;
        }
        if (!p.hasPermission("yhshop1.command.use")) {
            Msg.send(p, "no_permission");
            return;
        }
        new ShopGui(plugin, p, type, 0).open();
    }

    private void handleAdmin(CommandSender sender, String[] args) {
        if (!checkPerm(sender)) return;

        if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("gui"))) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(Chat.color(Msg.raw("only_player")));
                return;
            }
            new AdminListGui(plugin, p, ShopType.SPAWNER, 0).open();
            return;
        }
        if (args.length < 4) {
            sender.sendMessage(Chat.color(Msg.raw("admin_usage")));
            return;
        }

        String mode = args[1].toLowerCase();
        ShopType type = ShopType.fromString(args[2]);
        if (type == null) {
            sender.sendMessage(Chat.color(Msg.raw("invalid_usage")));
            return;
        }
        String id = args[3];
        ShopEntry entry = plugin.shops().get(type, id);

        switch (mode) {
            case "setprice" -> {
                if (entry == null) {
                    sender.sendMessage(Chat.color(Msg.raw("prefix") + Msg.raw("entry_not_found").replace("%id%", id)));
                    return;
                }
                if (args.length < 5) {
                    sender.sendMessage(Chat.color(Msg.raw("admin_usage")));
                    return;
                }
                try {
                    double v = Double.parseDouble(args[4]);
                    if (v < 0) throw new NumberFormatException();
                    entry.setPrice(v);
                    plugin.shops().save(type);
                    sender.sendMessage(Chat.color(Msg.raw("prefix") + Msg.raw("price_set")
                            .replace("%item%", entry.getDisplay())
                            .replace("%price%", plugin.getEcon() == null ? args[4] : plugin.getEcon().format(v))));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(Chat.color(Msg.raw("prefix")
                            + Msg.raw("price_invalid").replace("%input%", args[4])));
                }
            }
            case "givecmd" -> {
                if (entry == null) {
                    sender.sendMessage(Chat.color(Msg.raw("prefix") + Msg.raw("entry_not_found").replace("%id%", id)));
                    return;
                }
                String command = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
                entry.setGiveCommand(command);
                plugin.shops().save(type);
                sender.sendMessage(Chat.color(Msg.raw("prefix") + Msg.raw("cmd_set").replace("%id%", id)));
            }
            case "add" -> {
                if (plugin.shops().get(type, id) != null) {
                    sender.sendMessage(Chat.color(Msg.raw("prefix") + Msg.raw("entry_exists").replace("%id%", id)));
                    return;
                }
                try {
                    double v = Double.parseDouble(args[4]);
                    if (v < 0) throw new NumberFormatException();
                    int level = 1;
                    if (args.length >= 6) level = Integer.parseInt(args[5]);
                    ShopEntry e = new ShopEntry(id);
                    e.setPrice(v);
                    e.setLevel(level);
                    e.setIcon(type == ShopType.SPAWNER ? "SPAWNER" : "ENCHANTED_BOOK");
                    e.setGlow(type == ShopType.ENCHANT);
                    plugin.shops().setEntry(type, id, e);
                    sender.sendMessage(Chat.color(Msg.raw("prefix") + Msg.raw("entry_added").replace("%id%", id)));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(Chat.color(Msg.raw("prefix")
                            + Msg.raw("price_invalid").replace("%input%", args[4])));
                }
            }
            case "remove" -> {
                if (entry == null) {
                    sender.sendMessage(Chat.color(Msg.raw("prefix") + Msg.raw("entry_not_found").replace("%id%", id)));
                    return;
                }
                plugin.shops().removeEntry(type, id);
                sender.sendMessage(Chat.color(Msg.raw("prefix") + Msg.raw("entry_deleted").replace("%id%", id)));
            }
            default -> sender.sendMessage(Chat.color(Msg.raw("admin_usage")));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            for (String s : SUBS) if (s.startsWith(args[0].toLowerCase())) out.add(s);
            return out;
        }
        if (!args[0].equalsIgnoreCase("admin")) return out;
        if (!sender.hasPermission("yhshop1.commands.admin")) return out;
        if (args.length == 2) {
            for (String s : ADMIN_SUBS) if (s.startsWith(args[1].toLowerCase())) out.add(s);
        } else if (args.length == 3) {
            for (String s : new String[]{"spawner", "enchant"}) if (s.startsWith(args[2].toLowerCase())) out.add(s);
        } else if (args.length == 4 && !args[1].equalsIgnoreCase("add")) {
            ShopType t = ShopType.fromString(args[2]);
            if (t != null) {
                for (ShopEntry e : plugin.shops().entries(t)) {
                    if (e.getId().toLowerCase().startsWith(args[3].toLowerCase())) out.add(e.getId());
                }
            }
        }
        return out;
    }
}

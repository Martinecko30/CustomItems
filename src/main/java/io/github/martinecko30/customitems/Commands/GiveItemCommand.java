package io.github.martinecko30.customitems.Commands;

import io.github.martinecko30.customitems.CustomItems;
import io.github.martinecko30.customitems.Items.ItemsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class GiveItemCommand implements CommandExecutor, TabCompleter {

    private static FileConfiguration _config;

    public static void init() {
        File file = new File(CustomItems.getInstance().getDataFolder(), "messages.yml");
        if (!file.exists()) {
            try {
                CustomItems.getInstance().saveResource("messages.yml", false);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return;
            }
        }
        _config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You cannot give yourself custom items!");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("ci.use")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', _config.getString("no-permission")));
            return false;
        }

        if (args.length <= 0) {
            p.sendMessage("$a$lCustom Items");
            return true;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("give")) {
                if (!p.hasPermission("ci.give")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', _config.getString("no-permission")));
                    return false;
                }

                if (args.length < 2) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('@', _config.getString("no-item-name")));
                    return false;
                }

                String ItemName = args[1];

                if (ItemsManager.items.get(ItemName) == null) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('@', _config.getString("unknown-item")));
                    return false;
                }

                p.getInventory().addItem(ItemsManager.items.get(ItemName));

                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!p.hasPermission("vanish.reload")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', _config.getString("no-permission")));
                    return false;
                }
                CustomItems.getInstance().reloadConfig();
                CustomItems.getInstance().saveConfig();
                CustomItems.getInstance().saveResource("messages.yml", true);
                CustomItems.getInstance().saveResource("items.yml", false);
                GiveItemCommand.init();
                ItemsManager.init();
                p.sendMessage("§lCustomItems §a§lis reloaded!");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            commands.add("give");
            commands.add("reload");
            return commands;
        }
        if (args.length == 2) {
            if(args[1] == "reload")
               return null;
            List<String> items = new ArrayList<>();
            for(String itemName :ItemsManager.items.keySet()) {
                items.add(itemName.toLowerCase(Locale.ROOT));
            }
            return items;
        }
        return null;
    }
}
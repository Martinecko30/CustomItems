package io.github.martinecko30.customitems.Items;

import java.io.File;
import java.util.*;

import io.github.martinecko30.customitems.CustomItems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemsManager {
    private static FileConfiguration _config;
    public static Map<String, ItemStack> items = new HashMap<>();

    public static void init() {

        File file = new File(CustomItems.getInstance().getDataFolder(), "items.yml");
        if(!file.exists()) {
            try {
                CustomItems.getInstance().saveResource("items.yml", false);
            } catch(IllegalArgumentException e) {
                e.printStackTrace();
                return;
            }
        }
        _config = YamlConfiguration.loadConfiguration(file);

        createItems();
    }

    private static void createItems() {
        if(_config == null)
            return;

        for(String path : _config.getConfigurationSection("items.").getKeys(false)) {
            String rawPath = path;
            path = "items."+path;

            Material material = Material.BARRIER;
            if(_config.getString(path+".material") != null)
                material = Material.getMaterial(_config.getString(path+".material").toUpperCase(Locale.ROOT));

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            String name = _config.getString(path+".name");
            if(name != null) {
                name = ChatColor.translateAlternateColorCodes('&', name);
            }
            else {
                name = ChatColor.translateAlternateColorCodes('&', "&c&lMissing name");
            }

            meta.setDisplayName(name);

            List<String> lore = _config.getStringList(path+".lore");
            for(int i = 0; i < lore.size(); i++) {
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
            }

            if(_config.getInt("stats.durability") != 0)
                item.setDurability((short) _config.getInt("stats.durability"));

            if(_config.getStringList(path+".enchantments") != null)
                addEnchantments(meta, _config.getStringList(path+".enchantments"));

            if(_config.getStringList(path+".stats") != null)
                addStats((Damageable) meta, _config.getConfigurationSection(path+".stats"));

            item.setItemMeta(meta);
            items.put(rawPath, item);
        }
    }

    private static void addEnchantments(ItemMeta meta, List<String> enchantments) {
        for(String enchantmentName : enchantments) {
            String[] enchantData = enchantmentName.split(";");
            if(enchantData.length != 2) {
                CustomItems.getInstance().getLogger().severe(_config.getCurrentPath() + " enchantments have wrong value.");
                continue;
            }
            try {
                enchantData[0].toUpperCase(Locale.ROOT);
                Enchantment enchantment = Enchantment.getByName(enchantData[0]);

                if(enchantment == null)
                    continue;

                int level = Integer.parseInt(enchantData[1]);



                meta.addEnchant(enchantment, level, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void addStats(Damageable meta, ConfigurationSection stats) {
        for(String stat : stats.getKeys(false)) {
            if(stat.equalsIgnoreCase("durability")){
                meta.setDamage(stats.getInt(stat));
            }

            try {
                meta.addAttributeModifier(Attribute.valueOf(stat), new AttributeModifier(stat, stats.getInt(stat), AttributeModifier.Operation.ADD_NUMBER));
            } catch (IllegalArgumentException e) {
                CustomItems.getInstance().getLogger().warning("Unknown stat " + stat + " for weapon " + meta.getDisplayName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
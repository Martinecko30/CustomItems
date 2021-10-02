package io.github.martinecko30.customitems;

import io.github.martinecko30.customitems.Commands.GiveItemCommand;
import io.github.martinecko30.customitems.Items.ItemsManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class CustomItems extends JavaPlugin {

    public static CustomItems instance = null;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().log(Level.INFO,"Custom items loading!");
        initManagers();
        initCommands();
        getLogger().log(Level.INFO, "Custom items loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling Custom items!");
    }

    public static CustomItems getInstance(){
        return instance;
    }

    private void initCommands() {
        GiveItemCommand.init();
        getCommand("ci").setExecutor(new GiveItemCommand());
    }

    private void initManagers() {
        ItemsManager.init();
    }
}

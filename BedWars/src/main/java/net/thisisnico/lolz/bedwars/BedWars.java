package net.thisisnico.lolz.bedwars;

import net.thisisnico.lolz.bedwars.listeners.GameHandler;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedWars extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        BukkitUtils.instantiate(this);

        // To get instance:
        // BukkitUtils.getPlugin()

        BukkitUtils.registerListener(new GameHandler());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

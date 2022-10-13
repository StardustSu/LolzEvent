package net.thisisnico.lolz.buildbattle;

import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class BuildBattle extends JavaPlugin {

    @Override
    public void onEnable() {

        BukkitUtils.instantiate(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

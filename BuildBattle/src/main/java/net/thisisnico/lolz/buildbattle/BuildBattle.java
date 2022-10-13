package net.thisisnico.lolz.buildbattle;

import lombok.Getter;
import net.thisisnico.lolz.buildbattle.listeners.StreamHandler;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class BuildBattle extends JavaPlugin {

    @Getter
    private static BuildBattle instance;

    @Override
    public void onEnable() {
        instance = this;

        BukkitUtils.instantiate(this);
        BukkitUtils.registerListener(new StreamHandler());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

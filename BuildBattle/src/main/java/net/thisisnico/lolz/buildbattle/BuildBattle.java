package net.thisisnico.lolz.buildbattle;

import lombok.Getter;
import net.thisisnico.lolz.buildbattle.listeners.StreamHandler;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class BuildBattle extends JavaPlugin {

    @Getter
    private static BuildBattle instance;

    @Getter
    private static Logger log;

    @Override
    public void onEnable() {
        instance = this;

        log = getLogger();

        BukkitUtils.instantiate(this);
        BukkitUtils.registerListener(new StreamHandler());

        Game.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

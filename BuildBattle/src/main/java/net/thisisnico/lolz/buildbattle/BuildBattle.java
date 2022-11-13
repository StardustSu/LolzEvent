package net.thisisnico.lolz.buildbattle;

import lombok.Getter;
import net.thisisnico.lolz.buildbattle.listeners.GameHandler;
import net.thisisnico.lolz.buildbattle.listeners.StartHandler;
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
        BukkitUtils.registerListener(new GameHandler());
        BukkitUtils.registerListener(new StartHandler());

        Game.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

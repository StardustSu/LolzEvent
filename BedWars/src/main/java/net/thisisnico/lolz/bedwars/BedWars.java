package net.thisisnico.lolz.bedwars;

import lombok.Getter;
import net.thisisnico.lolz.bedwars.commands.ReloadArenaCommand;
import net.thisisnico.lolz.bedwars.commands.StartGameCommand;
import net.thisisnico.lolz.bedwars.listeners.GameHandler;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedWars extends JavaPlugin {

    @Getter
    private static BedWars instance;

    @Override
    public void onEnable() {
        BukkitUtils.instantiate(this);
        instance = this;
        BukkitUtils.registerListener(new GameHandler());
        Game.init();
        BukkitUtils.getAnnotationParser().parse(new ReloadArenaCommand());
        BukkitUtils.getAnnotationParser().parse(new StartGameCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Game.dispose();
    }
}

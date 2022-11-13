package net.thisisnico.lolz.tntrun;

import lombok.Getter;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.tntrun.commands.TestCommand;
import net.thisisnico.lolz.tntrun.listeners.GameListener;
import net.thisisnico.lolz.tntrun.listeners.StartHandler;
import net.thisisnico.lolz.tntrun.listeners.StreamHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class TntRun extends JavaPlugin {

    @Getter
    private static TntRun instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        BukkitUtils.instantiate(this);

        Game.init();

        BukkitUtils.registerListener(new GameListener());
        BukkitUtils.registerListener(new StreamHandler());
        BukkitUtils.registerListener(new StartHandler());

        BukkitUtils.getAnnotationParser().parse(new TestCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

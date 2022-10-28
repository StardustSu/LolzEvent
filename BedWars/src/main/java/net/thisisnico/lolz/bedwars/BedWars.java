package net.thisisnico.lolz.bedwars;

import lombok.Getter;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedWars extends JavaPlugin {

    @Getter
    private static BedWars instance;

    @Override
    public void onEnable() {
        BukkitUtils.instantiate(this);
        instance = this;
        Game.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Game.dispose();
    }
}

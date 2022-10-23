package net.thisisnico.lolz.bedwars;

import lombok.Getter;
import net.thisisnico.lolz.bedwars.classes.Arena;
import net.thisisnico.lolz.bedwars.listeners.GameHandler;
import net.thisisnico.lolz.bukkit.BukkitUtils;

public class Game {

    @Getter
    private static Arena arena;

    public static void init() {
        BukkitUtils.registerListener(new GameHandler());

        arena = new Arena(BukkitUtils.getPlugin().getServer().getWorlds().get(0));
    }

}

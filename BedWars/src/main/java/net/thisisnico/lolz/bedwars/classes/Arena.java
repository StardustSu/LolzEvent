package net.thisisnico.lolz.bedwars.classes;

import lombok.Getter;
import org.bukkit.World;

public class Arena {

    @Getter
    private final World world;

    public Arena(World world) {
        this.world = world;
    }

}

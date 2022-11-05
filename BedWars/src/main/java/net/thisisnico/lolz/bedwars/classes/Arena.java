package net.thisisnico.lolz.bedwars.classes;

import lombok.Getter;
import net.thisisnico.lolz.bedwars.WorldEditStuff;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;

public class Arena {

    @Getter
    private final World world;

    @Getter
    private final HashSet<Block> playerBlocks = new HashSet<>();

    public Arena(World world) {
        this.world = world;
    }

    public final Location getSpectatorSpawnLocation() {
        return world.getSpawnLocation();
    }

    public final void load() {
        WorldEditStuff.load("arena");
    }

}

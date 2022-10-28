package net.thisisnico.lolz.bedwars.classes;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

import java.util.HashSet;

public class Arena {

    @Getter
    private final HashSet<Block> playerBlocks = new HashSet<>();
    @Getter
    private final World world;
    @Getter
    private Location spectatorSpawnLocation;

    public Arena(World world) {
        this.world = world;
        for (ArmorStand entity : world.getEntitiesByClass(ArmorStand.class)) {
            if (entity.getScoreboardTags().contains("spec_spawn")) {
                spectatorSpawnLocation = entity.getLocation();
                break;
            }
        }
    }

//    public void


}

package net.thisisnico.lolz.bedwars.classes;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.title.Title;
import net.thisisnico.lolz.bedwars.Game;
import net.thisisnico.lolz.bukkit.utils.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Team {

    @Getter
    private final String name;
    @Getter
    private final TeamColor color;

    @Getter
    private final ArrayList<Player> players = new ArrayList<>();
    @Getter
    private boolean isBedDestroyed = false;

    @Getter @Setter
    private OfflinePlayer coolDudeWhoBrokeDaBed;
    @Getter
    private Location bedLocation;
    @Getter
    private Location spawnLocation;

    public Team(String name) {
        this.name = name;
        var color = TeamColor.getRandomColor();
        this.color = color;
        for (ArmorStand entity : Game.getArena().getWorld().getEntitiesByClass(ArmorStand.class)) {
            if (entity.getScoreboardTags().contains(color.name().toLowerCase()+"_spawn")) {
                this.spawnLocation = entity.getLocation();
                break;
            }
        }
        for (ArmorStand entity : Game.getArena().getWorld().getEntitiesByClass(ArmorStand.class)) {
            if (entity.getScoreboardTags().contains(color.name().toLowerCase()+"_bed")) {
                this.bedLocation = entity.getLocation();
                break;
            }
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void destroyBed() {
        isBedDestroyed = true;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
            if (players.contains(player))
                player.showTitle(Title.title(Component.color("&cКровать уничтожена!"), Component.color("&eВы больше не возродитесь.")));
        }
    }

    public boolean isPlayerInTeam(Player player) {
        return players.contains(player);
    }

    private boolean checkBedLocation(int x, int y, int z) {
        return x == bedLocation.getBlockX() &&
                y == bedLocation.getBlockY() &&
                z == bedLocation.getBlockZ();
    }

    public boolean checkBedLocation(Location l) {
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        if (checkBedLocation(x, y, z)) return true;
        if (checkBedLocation(x, y, z + 1)) return true;
        if (checkBedLocation(x, y, z - 1)) return true;
        if (checkBedLocation(x + 1, y, z)) return true;
        return checkBedLocation(x - 1, y, z);
    }

}

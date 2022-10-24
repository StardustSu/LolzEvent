package net.thisisnico.lolz.bedwars.classes;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Team {
    @Getter
    private final String name = "Test name";
    @Getter
    private Color color;

    @Getter
    private ArrayList<Player> players = new ArrayList<>();
    @Getter
    private boolean isBedDestroyed = false;
    @Getter
    private Location bedLocation;
    @Getter
    private Location spawnLocation;

    @Getter
    private int allivePlayers = players.size();

    // Надбавка баллов за:
    private int bedsDestroyed = 0; // Уничтожения кроватей
    private int playersFinalKilled = 0; // Финальные убийства игроков

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void destroyBed() {
        isBedDestroyed = true;
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

    public void addFinalKill() {
        playersFinalKilled++;
    }

    public void addBedsDestroyed() {
        bedsDestroyed++;
    }

    public int getPointsCount() {
        final int pointsPerBed = 5;
        final int pointsPerFinalKill = 3;
        return bedsDestroyed * pointsPerBed + playersFinalKilled * pointsPerFinalKill;
    }
}

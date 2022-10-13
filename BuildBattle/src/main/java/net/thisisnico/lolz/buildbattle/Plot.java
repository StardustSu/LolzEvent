package net.thisisnico.lolz.buildbattle;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;

public class Plot {

    @Getter
    private final OfflinePlayer owner;
    @Getter
    private final Location location;

    private final HashMap<Player, Integer> scores = new HashMap<>();

    public Plot(OfflinePlayer owner, Location location) {
        this.owner = owner;
        this.location = location;
    }

    public void addVote(Player player, int score) {
        if (scores.containsKey(player)) scores.replace(player, score);
        else scores.put(player, score);
    }

    public int getScore() {
        int score = 0;
        for (int i : scores.values()) score += i;
        return score;
    }
}

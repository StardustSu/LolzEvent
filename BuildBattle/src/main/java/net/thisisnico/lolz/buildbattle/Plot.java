package net.thisisnico.lolz.buildbattle;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.guieffect.qual.SafeEffect;

import java.util.HashMap;

public class Plot {
    @Getter
    private static final int maxHeight = 90;

    @Getter
    @Setter
    private String owner;
    @Getter
    private final Location location;

    private final HashMap<String, Integer> scores = new HashMap<>();

    public Plot(String owner, Location location) {
        this.owner = owner;
        this.location = location;
    }

    public void addVote(String player, int score) {
        if (scores.containsKey(player)) scores.replace(player, score);
        else scores.put(player, score);
    }

    public int getScore() {
        int score = 0;
        for (int i : scores.values()) score += i;
        return score;
    }
}

package net.thisisnico.lolz.buildbattle;

import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Plot {
    @Getter
    private static final int maxHeight = 90;

    @Getter
    public final String name;
    @Getter
    private final ArrayList<String> owners;
    @Getter
    private final Location location;

    @Getter
    private final HashMap<String, Integer> scores = new HashMap<>();

    public Plot(String name, Location location, String... owners) {
        this.name = name;
        this.owners = new ArrayList<>(List.of(owners));
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

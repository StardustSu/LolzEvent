package net.thisisnico.lolz.bukkit.utils;

import lombok.Getter;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class ScoreboardUtils {

//    private static final ArrayList<Scoreboard> scoreboards = new ArrayList<>();

    private static final ArrayList<Consumer<ScoreboardUtils>> updaters = new ArrayList<>();

    public static void runUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (ScoreboardUtils value : utils.values()) {
                    updaters.forEach(consumer -> consumer.accept(value));
                }
            }
        }.runTaskTimer(BukkitUtils.getPlugin(), 0, 20);
    }

    public static void registerUpdater(Consumer<ScoreboardUtils> updater) {
        updaters.add(updater);
    }

    private static final HashMap<Player, ScoreboardUtils> utils = new HashMap<>();
    public static ScoreboardUtils get(Player p) {
        if (!utils.containsKey(p)) utils.put(p, new ScoreboardUtils(p));
        return utils.get(p);
    }

    @Getter
    private final Player player;
    @Getter
    private final Scoreboard scoreboard;
    @Getter
    private final Objective sidebar;

    private final HashMap<Integer, String> lines = new HashMap<>();

    private ScoreboardUtils(Player p) {
        utils.put(p, this);
        this.player = p;
        scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        p.setScoreboard(scoreboard);
//        scoreboards.add(sb);
        sidebar = scoreboard.registerNewObjective("tc", Criteria.DUMMY, Component.color("Lolz Event"));
        sidebar.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

        setLine(1, "&0");
        setLine(8, "&fПри поддержке");
        setLine(9, "&alolz.guru &f| &eскамим.рф");
    }

    public void setLine(int line, String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        if (lines.containsKey(line)) {
            if (lines.get(line).equals(text)) return;
            sidebar.getScore(lines.get(line)).resetScore();
            lines.remove(line);
        }
        lines.put(line, text);
        sidebar.getScore(text).setScore(line*-1);
    }

    public void dispose() {
//        scoreboards.remove(sb);
        utils.remove(player);
    }

}

package net.thisisnico.lolz.bedwars;

import lombok.Getter;
import net.thisisnico.lolz.bedwars.classes.TeamColor;
import net.thisisnico.lolz.bedwars.commands.ReloadArenaCommand;
import net.thisisnico.lolz.bedwars.commands.StartGameCommand;
import net.thisisnico.lolz.bedwars.listeners.GameHandler;
import net.thisisnico.lolz.bedwars.listeners.StartHandler;
import net.thisisnico.lolz.bedwars.listeners.StreamListener;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.ScoreboardUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class BedWars extends JavaPlugin {

    @Getter
    private static BedWars instance;

    @Override
    public void onEnable() {
        BukkitUtils.instantiate(this);
        instance = this;
        BukkitUtils.registerListener(new GameHandler());
        BukkitUtils.registerListener(new StartHandler());
        BukkitUtils.registerListener(new StreamListener());
        Game.init();
        BukkitUtils.getAnnotationParser().parse(new ReloadArenaCommand());
        BukkitUtils.getAnnotationParser().parse(new StartGameCommand());

        ScoreboardUtils.registerUpdater(sb -> {
            if (Game.isRunning()) {
                sb.setLine(1, "&1");
                // █ ▓ ▒

                var teams = Game.getTeams();
                teams.sort((o1, o2) -> o2.getColor().ordinal() - o1.getColor().ordinal());
                var s = new ArrayList<String>();
                for (var color : TeamColor.values()) {
                    var team = teams.stream().filter(t -> t.getColor() == color).findFirst().orElse(null);
                    if (team == null) {
                        s.add("&"+color.getCode()+"▒ &f&l0");
                        continue;
                    }
                    var players = team.getPlayers().size();
                    s.add("&" + color.getCode() + (team.isBedDestroyed() ? "▓" : "█") + " &" +
                            (team.getPlayers().contains(sb.getPlayer().getName()) ? color.getCode() + "&n" : "f") + "&l"+players+"&r");
                }

                sb.setLine(2, s.stream().limit(4).collect(Collectors.joining(" ")));
                sb.setLine(3, s.stream().skip(4).collect(Collectors.joining(" ")));

                sb.setLine(4, "&4");

                if (Game.isPlayerInGame(sb.getPlayer())) sb.setLine(5, "Очков: &e"+Game.getTeam(sb.getPlayer()).getScore());

                sb.setLine(6, "&6");
            } else {
                sb.setLine(1, "&1");
                sb.setLine(2, "&2");
                sb.setLine(3, "&fОжидание игры...");
                sb.setLine(4, "&4");
                sb.setLine(5, "&5");
                sb.setLine(6, "&6");
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Game.dispose();
    }
}

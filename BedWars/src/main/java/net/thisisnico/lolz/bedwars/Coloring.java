package net.thisisnico.lolz.bedwars;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.thisisnico.lolz.bedwars.classes.TeamColor;
import net.thisisnico.lolz.bukkit.utils.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Coloring {

    public static void init(Player player) {
        var sb = player.getScoreboard();

        for (TeamColor value : TeamColor.values()) {
            var team = sb.getTeam(value.name());
            if (team == null) {
                team = sb.registerNewTeam(value.name());
            }
            team.prefix(Component.color(value.name().charAt(0) + " ").color(value.getColor()).decorate(TextDecoration.BOLD));
            team.color(value.getColor());
        }

        var admin = sb.getTeam("admin");
        if (admin == null) {
            admin = sb.registerNewTeam("admin");
        }
        admin.prefix(Component.color("&4&lA "));
        admin.color(NamedTextColor.DARK_RED);

        var spectator = sb.getTeam("spectator");
        if (spectator == null) {
            spectator = sb.registerNewTeam("spectator");
        }
        spectator.prefix(Component.color("&7&lSPEC "));
        spectator.color(NamedTextColor.DARK_GRAY);

        var playerTeam = sb.getTeam("player");
        if (playerTeam == null) {
            playerTeam = sb.registerNewTeam("player");
        }
        playerTeam.color(NamedTextColor.DARK_PURPLE);
    }

    @SuppressWarnings("ConstantConditions")
    public static void updateColors(Player player) {
        init(player);

        var sb = player.getScoreboard();

        if (!Game.isRunning()) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                var team = sb.getTeam(onlinePlayer.isOp() ? "admin" : "player");
                team.addPlayer(onlinePlayer);
            }
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!Game.isPlayerInGame(onlinePlayer)) {
                    var team = sb.getTeam("spectator");
                    team.addPlayer(onlinePlayer);
                } else {
                    var team = sb.getTeam(Game.getTeam(onlinePlayer).getColor().name());
                    team.addPlayer(onlinePlayer);
                }
            }
        }
    }

}

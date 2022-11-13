package net.thisisnico.lolz.lobby;

import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.bukkit.utils.ScoreboardUtils;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lobby extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic

        BukkitUtils.instantiate(this);
        BukkitUtils.registerListener(this);

        ScoreboardUtils.registerUpdater(sb -> {
            var clan = DatabaseAdapter.getClan(sb.getPlayer());
            sb.getSidebar().displayName(Component.color("&f&lСка&1&lмим&c&l.РФ"));
            sb.setLine(1, "&1");
            sb.setLine(2, "&fИгроков: &e&l" + getServer().getOnlinePlayers().size());
            sb.setLine(3, "&fКлан: &e" + (clan != null ? clan.getTag() : "&cнет"));
            sb.setLine(4, "&4");
        });

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (var player : Bukkit.getOnlinePlayers()) {
                var clan = DatabaseAdapter.getClan(player);

                for (var p1 : getServer().getOnlinePlayers()) {
                    var sb = ScoreboardUtils.get(p1).getScoreboard();
                    var team = sb.getTeam(clan.getTag());
                    if (team != null) {
                        team.suffix(Component.color(" &b["+clan.getTag()+"]"));
                    }
                }
            }
        }, 0, 3*20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        ScoreboardUtils.get(e.getPlayer());

        // get clan
        var clan = DatabaseAdapter.getClan(e.getPlayer());

        // for each player scoreboard
        for (var player : getServer().getOnlinePlayers()) {
            var sb = ScoreboardUtils.get(player).getScoreboard();
            // get team, if it doesn't exist create
            var team = sb.getTeam(clan.getTag());
            if (team == null) {
                team = sb.registerNewTeam(clan.getTag());
                team.suffix(Component.color(" &b["+clan.getTag()+"]"));
            }
            // add player to team
            team.addEntry(e.getPlayer().getName());
        }
    }

    @EventHandler
    void onLeave(PlayerQuitEvent e) {
        // get clan
        var clan = DatabaseAdapter.getClan(e.getPlayer());

        // for each player scoreboard
        for (var player : getServer().getOnlinePlayers()) {
            var sb = ScoreboardUtils.get(player).getScoreboard();
            // get team, if it doesn't exist create
            var team = sb.getTeam(clan.getTag());
            if (team == null) {
                team = sb.registerNewTeam(clan.getTag());
                team.suffix(Component.color(" &b["+clan.getTag()+"]"));
            }
            // remove player from team
            team.removeEntry(e.getPlayer().getName());

            // if team is empty, unregister
            if (team.getSize() == 0) {
                team.unregister();
            }
        }
    }
}

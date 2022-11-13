package net.thisisnico.lolz.bedwars.listeners;

import net.thisisnico.lolz.bedwars.Coloring;
import net.thisisnico.lolz.bedwars.Game;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.bukkit.utils.ScoreboardUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class StreamListener implements Listener {

    @EventHandler
    void onLogin(PlayerLoginEvent e) {
        if (Game.isRunning()) {
            if (!Game.isOfflinePlayerInGame(e.getPlayer())) {
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.color("&cИгра уже началась!"));
            }
        }
    }

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        if (Game.isRunning()) {
            if (Game.isPlayerInGame(e.getPlayer())) {
                Game.kill(e.getPlayer(), false);
            }
        }
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        ScoreboardUtils.get(e.getPlayer());
        if (Game.isRunning()) {
            if (Game.isPlayerInGame(e.getPlayer())) {
                Game.respawn(e.getPlayer(), 2);
            }
        } else {
            e.getPlayer().teleport(Game.getArena().getSpectatorSpawnLocation());
            e.getPlayer().setGameMode(GameMode.ADVENTURE);
            e.getPlayer().getInventory().setContents(new ItemStack[] {});
            e.getPlayer().setAllowFlight(true);
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Coloring.updateColors(onlinePlayer);
        }
    }

}

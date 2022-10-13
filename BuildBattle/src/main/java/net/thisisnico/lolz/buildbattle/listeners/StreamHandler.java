package net.thisisnico.lolz.buildbattle.listeners;

import net.thisisnico.lolz.buildbattle.Game;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class StreamHandler implements Listener {

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        if (Game.isStarted() && !Game.isPlayer(e.getPlayer())) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

        if (DatabaseAdapter.getUser(e.getPlayer()).isAdmin()) {
            e.getPlayer().getInventory().addItem(Game.getItem().getItemStack());
        }
    }

    @EventHandler
    void onItemDropped(PlayerDropItemEvent e) {
        if (!DatabaseAdapter.getUser(e.getPlayer()).isAdmin() && Game.isPlayer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
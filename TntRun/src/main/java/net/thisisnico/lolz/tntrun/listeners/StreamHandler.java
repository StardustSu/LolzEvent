package net.thisisnico.lolz.tntrun.listeners;

import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import net.thisisnico.lolz.tntrun.Game;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class StreamHandler implements Listener {

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        if (Game.isStarted()) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

        if (DatabaseAdapter.getUser(e.getPlayer()).isAdmin()) {
            e.getPlayer().getInventory().addItem(Game.getItem().getItemStack());
        }
    }

}

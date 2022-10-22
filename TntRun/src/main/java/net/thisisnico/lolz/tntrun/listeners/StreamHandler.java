package net.thisisnico.lolz.tntrun.listeners;

import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import net.thisisnico.lolz.tntrun.Game;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StreamHandler implements Listener {

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        e.getPlayer().teleport(Game.getSpawn());
        if (Game.isStarted()) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

        if (DatabaseAdapter.getUser(e.getPlayer()).isAdmin()) {
            e.getPlayer().getInventory().addItem(Game.getItem().getItemStack());
        }

        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
    }

}

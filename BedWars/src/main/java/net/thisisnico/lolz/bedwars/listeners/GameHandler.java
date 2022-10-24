package net.thisisnico.lolz.bedwars.listeners;

import net.thisisnico.lolz.bedwars.Game;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameHandler implements Listener {

    @EventHandler
    private void onPlayerBreakBlock(BlockBreakEvent event) {
        if (!Game.isRunning()) {
            event.setCancelled(true);
        }

        if (event.getBlock().getType().name().contains("BED")) {
            if (!Game.destroyBed(event.getBlock().getLocation(), event.getPlayer())) {
                event.setCancelled(true);
            }
        }

        // TODO: Add check for destroying block, which can not be placed by player
    }

    @EventHandler
    private void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (!Game.isRunning()) {
            event.setCancelled(true);
        }

        // TODO: Check if generator nearby or if it spawn territory
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (!Game.isRunning() && !DatabaseAdapter.getUser(event.getPlayer()).isAdmin()) {
            event.setCancelled(true);
        }

        // ПКМ по кровати
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().name().contains("BED") && event.getAction().name().contains("RIGHT")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onEntityInteract(PlayerInteractEntityEvent event) {
        if (!Game.isRunning()) {
            event.setCancelled(true);
        }

        if (!Game.isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        }

        if (!(event.getRightClicked() instanceof Villager || event.getRightClicked() instanceof NPC)) {
            event.setCancelled(true);
        }

        if (event.getRightClicked().getName().contains("Улучшения")) {
            // TODO: Open upgrades menu
        } else if (event.getRightClicked().getName().contains("Магазин")) {
            // TODO: Open shop menu
        } else {
            event.setCancelled(true);
        }
    }
}

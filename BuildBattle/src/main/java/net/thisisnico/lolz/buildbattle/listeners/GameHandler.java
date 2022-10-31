package net.thisisnico.lolz.buildbattle.listeners;

import net.thisisnico.lolz.buildbattle.Game;
import net.thisisnico.lolz.buildbattle.GameState;
import net.thisisnico.lolz.buildbattle.Plot;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class GameHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    void onJoin(PlayerJoinEvent e) {
        e.getPlayer().getInventory().clear();
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
        if (Game.isStarted() &&
                (!Game.isPlayer(e.getPlayer().getName()) || DatabaseAdapter.getUser(e.getPlayer()).isAdmin())) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

        if (e.getPlayer().isOp()) {
            e.getPlayer().getInventory().addItem(Game.getItem().getItemStack());
        }
    }

    @EventHandler
    void onItemDropped(PlayerDropItemEvent e) {
        if (!DatabaseAdapter.getUser(e.getPlayer()).isAdmin()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerMove(PlayerMoveEvent e) {
        e.setCancelled(check(e.getPlayer(), e.getTo()));
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerBreak(BlockBreakEvent e) {
        e.setCancelled(check(e.getPlayer(), e.getBlock().getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerPlace(BlockPlaceEvent e) {
        e.setCancelled(check(e.getPlayer(), e.getBlockPlaced().getLocation()));
    }

    boolean check(Player player, Location to) {
        if (Game.isStarted() && Game.isPlayer(player.getName())) {
            Plot plot = null;
            if (Game.getState() == GameState.BUILDING) {
                plot = Game.getPlot(player.getName());
            } else if (Game.getState() == GameState.VOTING) {
                plot = Game.getCurrentPlot();
            }

            if (plot == null)
                return false;

            Location loc = plot.getLocation();

            if (Math.abs(to.getX() - loc.getX()) > 16 || Math.abs(to.getZ() - loc.getZ()) > 16
                    || to.getY() < loc.getY() - 1 || to.getY() >= Plot.getMaxHeight()) {
                player.sendMessage("§cВы не можете выйти за пределы строительной площадки!");
                return true;
            }
        }
        return false;
    }
}
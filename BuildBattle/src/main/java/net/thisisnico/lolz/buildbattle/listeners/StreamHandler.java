package net.thisisnico.lolz.buildbattle.listeners;

import net.thisisnico.lolz.buildbattle.Game;
import net.thisisnico.lolz.buildbattle.GameState;
import net.thisisnico.lolz.buildbattle.Plot;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class StreamHandler implements Listener {

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        e.getPlayer().getInventory().clear();
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
        if (Game.isStarted() &&
                (!Game.isPlayer(e.getPlayer()) || DatabaseAdapter.getUser(e.getPlayer()).isAdmin())) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

//        if (DatabaseAdapter.getUser(e.getPlayer()).isAdmin()) {
        e.getPlayer().getInventory().addItem(Game.getItem().getItemStack());
//        }
    }

    @EventHandler
    void onItemDropped(PlayerDropItemEvent e) {
        if (!DatabaseAdapter.getUser(e.getPlayer()).isAdmin() && Game.isPlayer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    void onPlayerMove(PlayerMoveEvent e) {
        if (Game.isStarted() && Game.isPlayer(e.getPlayer())) {
            Plot plot = null;
            if (Game.getState() == GameState.BUILDING) {
                plot = Game.getPlot(e.getPlayer());
            } else if (Game.getState() == GameState.VOTING) {
                plot = Game.getCurrentPlot();
            }

            if (plot == null)
                return;

            Location to = e.getTo();
            Location loc = plot.getLocation();

            if (Math.abs(to.getX() - loc.getX()) > 16 || Math.abs(to.getZ() - loc.getZ()) > 16
                    || to.getY() < loc.getY() - 1 || to.getY() >= Plot.getMaxHeight()) {
                e.getPlayer().teleport(e.getFrom());
                e.getPlayer().sendMessage("§cВы не можете выйти за пределы строительной площадки!");
            }
        }
    }

    @EventHandler
    void onPlayerBreak(BlockBreakEvent e) {
        if (Game.isStarted() && Game.isPlayer(e.getPlayer())) {
            Plot plot = null;
            if (Game.getState() == GameState.BUILDING) {
                plot = Game.getPlot(e.getPlayer());
            } else if (Game.getState() == GameState.VOTING) {
                plot = Game.getCurrentPlot();
            }

            if (plot == null)
                return;

            Location to = e.getBlock().getLocation();
            Location loc = plot.getLocation();

            if (Math.abs(to.getX() - loc.getX()) > 16 || Math.abs(to.getZ() - loc.getZ()) > 16
                    || to.getY() < loc.getY() - 1 || to.getY() >= Plot.getMaxHeight()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("§cВы не можете выйти за пределы строительной площадки!");
            }
        }
    }

    @EventHandler
    void onPlayerPlace(BlockPlaceEvent e) {
        if (Game.isStarted() && Game.isPlayer(e.getPlayer())) {
            Plot plot = null;
            if (Game.getState() == GameState.BUILDING) {
                plot = Game.getPlot(e.getPlayer());
            } else if (Game.getState() == GameState.VOTING) {
                plot = Game.getCurrentPlot();
            }

            if (plot == null)
                return;

            Location to = e.getBlock().getLocation();
            Location loc = plot.getLocation();

            if (Math.abs(to.getX() - loc.getX()) > 16 || Math.abs(to.getZ() - loc.getZ()) > 16
                    || to.getY() < loc.getY() - 1 || to.getY() >= Plot.getMaxHeight()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("§cВы не можете выйти за пределы строительной площадки!");
            }
        }
    }
}
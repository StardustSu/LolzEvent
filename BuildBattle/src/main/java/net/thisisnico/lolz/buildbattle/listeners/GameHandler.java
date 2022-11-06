package net.thisisnico.lolz.buildbattle.listeners;

import net.thisisnico.lolz.buildbattle.BuildBattle;
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
        if (Game.isStarted()) {

            if (!Game.isPlayer(e.getPlayer().getName()) || DatabaseAdapter.getUser(e.getPlayer()).isAdmin()){
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
            else{
                e.getPlayer().setGameMode(GameMode.CREATIVE);
            }
        }

        if (e.getPlayer().isOp()) {
            e.getPlayer().getInventory().addItem(Game.getStartGameItem().getItemStack());
        }

        Game.getBar().addPlayer(e.getPlayer());
    }

    @EventHandler
    void onItemDropped(PlayerDropItemEvent e) {
        if (!DatabaseAdapter.getUser(e.getPlayer()).isAdmin()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerMove(PlayerMoveEvent e) {
        e.setCancelled(check(e.getPlayer(), e.getTo(), false));
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerBreak(BlockBreakEvent e) {
        e.setCancelled(check(e.getPlayer(), e.getBlock().getLocation(), true));
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerPlace(BlockPlaceEvent e) {
        e.setCancelled(check(e.getPlayer(), e.getBlockPlaced().getLocation(), true));
    }

    boolean check(Player player, Location to, boolean isBlockCheck) {
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

            var offset = Game.getState() == GameState.BUILDING ? 16 : 18;

            if (!isBlockCheck && Game.getState() == GameState.BUILDING) {
                loc = loc.clone();
                loc.add(0.5, 0.5, 0.5);
            }

            if (Math.abs(to.getX() - loc.getX()) > offset || Math.abs(to.getZ() - loc.getZ()) > offset
                    || to.getY() < loc.getY() - 1 || to.getY() >= Plot.getMaxHeight()) {
                player.sendMessage("§cВы не можете выйти за пределы строительной площадки!");
                return true;
            }
        }
        return false;
    }
}
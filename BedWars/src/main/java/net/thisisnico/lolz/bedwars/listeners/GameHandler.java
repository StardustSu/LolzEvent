package net.thisisnico.lolz.bedwars.listeners;

import net.thisisnico.lolz.bedwars.Const;
import net.thisisnico.lolz.bedwars.Game;
import net.thisisnico.lolz.bedwars.classes.ResourceGenerator;
import net.thisisnico.lolz.bedwars.classes.Team;
import net.thisisnico.lolz.bedwars.menu.ShopMenu;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GameHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().getName().equalsIgnoreCase("nicojs")) {
            if (e.getItemDrop().getItemStack().getType() == Material.GRASS_BLOCK) {
                Game.startTimer();
            }
        }
    }

    @EventHandler
    private void onPlayerBreakBlock(BlockBreakEvent event) {
        if (!Game.isRunning()) {
            event.setCancelled(true);
        }

        if (event.getBlock().getType().name().contains("BED")) {
            if (Game.destroyBed(event.getBlock().getLocation(), event.getPlayer())) {
                event.setCancelled(false);
                event.setDropItems(false);
                return;
            } else event.setCancelled(true);
        }

        if (!Game.getArena().getPlayerBlocks().contains(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onCraft(PrepareItemCraftEvent e) {
        e.getInventory().setResult(new ItemStack(Material.AIR));
    }

    @EventHandler
    private void onCraft(CraftItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerDieLikeASuckerWhatANerdBitchShitFuckHimHisMumIsStupidNobodyLikedHimAnyways(PlayerDeathEvent event) {
        event.setCancelled(true);

        event.getPlayer().teleport(Game.getArena().getSpectatorSpawnLocation());
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
        Team team = Game.getTeam(event.getPlayer());

        if (team == null) return;
        Bukkit.broadcast(Component.color(event.getPlayer().getName()).color(team.getColor().getColor())
                .append(Component.color(" &7умер")));

        event.getPlayer().getInventory().clear();
        event.getPlayer().setAllowFlight(true);
        event.getPlayer().setFlying(true);

        if (team.isBedDestroyed()) {
            Bukkit.broadcast(Component.color("&b&lFINAL KILL!"));
            Bukkit.broadcast(Component.color("Player pos: " + event.getEntity().getLocation().getBlockX() + " " + event.getEntity().getLocation().getBlockY() + " " + event.getEntity().getLocation().getBlockZ()));
            Bukkit.broadcast(Component.color("SpectatorSpawnLocation pos: " + Game.getArena().getSpectatorSpawnLocation().getBlockX() + " " + Game.getArena().getSpectatorSpawnLocation().getBlockY() + " " + Game.getArena().getSpectatorSpawnLocation().getBlockZ()));
            OfflinePlayer killer = event.getPlayer().getKiller();
            if (killer == null) killer = team.getCoolDudeWhoBrokeDaBed();
            if (killer != null) {
                var clan = DatabaseAdapter.getClan(killer);
                if (clan == null) {
                    if (killer.isOnline()) killer.getPlayer().sendMessage(Component.color("&cТы не в клане"));
                    return;
                }
                clan.givePoints(Const.POINTS_FOR_FINAL_KILL);
                if (killer.isOnline())
                    Bukkit.broadcast(Component.color("&e" + killer.getName() + " &aполучил &e" + Const.POINTS_FOR_FINAL_KILL + " &aочков за финальный убийство"));
            } else {
                Bukkit.broadcast(Component.color("&cНикто не убил последнего игрока"));
            }

            return;
        }

        final int[] i = {Const.RESPAWN_DELAY};
        Bukkit.getScheduler().runTaskTimer(BukkitUtils.getPlugin(), task -> {
            if (i[0] == 0) {
                event.getPlayer().teleport(team.getSpawnLocation());
                event.getPlayer().setGameMode(GameMode.SURVIVAL);
                event.getPlayer().setAllowFlight(false);
                event.getPlayer().setFlying(false);
                event.getPlayer().getInventory().setContents(new ItemStack[]{});
               task.cancel();
            } else {
                event.getPlayer().teleport(Game.getArena().getSpectatorSpawnLocation());
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                event.getPlayer().sendActionBar(Component.color("&cРеспавн через " + i[0] + " сек"));
                i[0]--;
            }
        }, 0L, 20L);
    }

    @EventHandler
    private void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (!Game.isRunning()) {
            event.setCancelled(true);
        }

        var loc = event.getBlockPlaced().getLocation();

        for (Team team : Game.getTeams()) {
            if (loc.distance(team.getSpawnLocation()) < 6) {
                event.setCancelled(true);
                return;
            }
        }

        for (ResourceGenerator generator : Game.getGenerators()) {
            if (loc.distance(generator.getLocation()) < 2) {
                event.setCancelled(true);
                return;
            }
        }

        Game.getArena().getPlayerBlocks().add(event.getBlockPlaced());

    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (!Game.isRunning() && !event.getPlayer().isOp()) {
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

        if (event.getRightClicked() instanceof Villager) {
            event.setCancelled(true);
            new ShopMenu(event.getPlayer());
        }
    }
}

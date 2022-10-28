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
            event.getPlayer().teleport(Game.getArena().getWorld().getSpawnLocation());
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            OfflinePlayer killer = event.getPlayer().getKiller();
            if (killer == null) killer = team.getCoolDudeWhoBrokeDaBed();
            if (killer != null) {
                var clan = DatabaseAdapter.getClan(event.getPlayer());
                if (clan == null) {
                    event.getPlayer().sendMessage(Component.color("&cТы не в клане"));
                    return;
                }
                clan.givePoints(Const.POINTS_FOR_FINAL_KILL);
            }
            return;
        }

//        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
//            event.getPlayer().teleport(team.getSpawnLocation());
//            Game.givePlayerStartItems(event.getPlayer(), team);
//            event.getPlayer().setGameMode(GameMode.SURVIVAL);
//            event.getPlayer().setAllowFlight(false);
//            event.getPlayer().setFlying(false);
//        }, RESPAWN_DELAY * 20L);

        final int[] i = {Const.RESPAWN_DELAY};
        Bukkit.getScheduler().runTaskTimer(BukkitUtils.getPlugin(), task -> {
            if(i[0] == 0) {
                event.getPlayer().teleport(team.getSpawnLocation());
                event.getPlayer().setGameMode(GameMode.SURVIVAL);
                event.getPlayer().setAllowFlight(false);
                event.getPlayer().setFlying(false);
                event.getPlayer().getInventory().setContents(new ItemStack[] {});
//                event.getPlayer().showTitle(Title.title(Component.color("&cВ бой!"), Component.color("&f"), Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
                task.cancel();
            } else {
                event.getPlayer().teleport(Game.getArena().getSpectatorSpawnLocation());
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                event.getPlayer().sendActionBar(Component.color("&cРеспавн через " + i[0] + " сек"));
//                event.getPlayer().showTitle(Title.title(Component.color("&c"+i[0]), Component.color("&aдо возрождения"), Title.Times.times(Duration.ZERO, Duration.ofMillis(1500), Duration.ZERO)));
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

        // SIDE NOTE: в классике нет улучшений
//        if (event.getRightClicked().getName().contains("Улучшения")) {
//
//        } else
    }
}

package net.thisisnico.lolz.bedwars.listeners;

import net.thisisnico.lolz.bedwars.Game;
import net.thisisnico.lolz.bedwars.classes.ResourceGenerator;
import net.thisisnico.lolz.bedwars.classes.Team;
import net.thisisnico.lolz.bedwars.menu.ShopMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
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
    private void onTnt(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> !Game.getArena().getPlayerBlocks().contains(block));
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
        if (Game.isRunning()) {
            Game.kill(event.getPlayer(), true);
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (e.getDamager() instanceof Player dmg) {
                var team = Game.getTeam(dmg);
                if (team != null) {
                    if (team.getPlayers().contains(p.getName())) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    private void onPickup(PlayerAttemptPickupItemEvent e) {
        if (e.getItem().getItemStack().getType().name().contains("BED")) {
            e.setCancelled(true);
            e.getItem().remove();
        }
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
            if (loc.distance(generator.getLocation()) < 3.5f) {
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

        var block = event.getClickedBlock();
        // ПКМ по кровати
        if (block != null && block.getType().name().contains("BED") && event.getAction().isRightClick()) {
            event.setCancelled(true);
        }

        if (block != null && (block.getType().name().contains("TRAPDOOR")
                || block.getType() == Material.CRAFTING_TABLE
                || block.getType() == Material.CHEST
                || block.getType() == Material.FURNACE)) {
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

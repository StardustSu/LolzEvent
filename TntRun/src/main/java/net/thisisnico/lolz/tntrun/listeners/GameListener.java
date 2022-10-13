package net.thisisnico.lolz.tntrun.listeners;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.tntrun.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GameListener implements Listener {

    private static final float blockStandingOffset = 0.3f; // То насколько игрок может выйти за блок на шифте
    private static final float blockCenter = 0.5f; // Центр блока ЛОГИЧНО БЛЯТЬ

    @EventHandler
    void onTick(ServerTickEndEvent e) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Game.isSuppress()) return;
            if (Game.isSpectator(player)) continue;

            if (!player.getAllowFlight() && !player.getLocation().clone().add(0,-1,0).getBlock().getType().isAir()) {
                player.setAllowFlight(true);
            } else if (player.isFlying()) {
                player.setFlying(false);
                player.setAllowFlight(false);
                player.setVelocity(player.getVelocity().multiply(1.4).setY(.8));
                player.playSound(player, Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
            }

            var block = player.getLocation().getBlock();
            if (block.getType() == Material.WATER) {
                Game.eliminate(player);
                continue;
            }

            if (player.getVelocity().getY() < .1f && isOnTnt(block)) {
                if (Game.isPlayer(player) && player.isFlying()) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    continue;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.getRelative(0,-2,0).setType(Material.AIR);
                        block.getWorld().spawnParticle(Particle.FALLING_DUST, block.getLocation().add(0,-1,0),
                                10, 0.5, 0.5, 0.5, 0.5, Material.TNT.createBlockData());
                        block.getRelative(0,-1,0).setType(Material.AIR);
                    }
                }.runTaskLater(BukkitUtils.getPlugin(), 8);
            }

            if (player.getVelocity().getY() < .2f && isOnAir(block)) {
                double playerPositionX = player.getLocation().getX(); // Координаты игрока, сам найдёшь какими-то бакитовскими функциями
                double playerPositionY = player.getLocation().getZ();

                double blockToDeletePosX = playerPositionX; // Координаты блока, с которого нужно начинать убирать блоки
                double blockToDeletePosY = playerPositionY;

                int blocksToDeleteOnX = 1;
                int blocksToDeleteOnY = 1;

                if (isPlayerStandingBetweenBlocks(playerPositionX)) {
                    blockToDeletePosX = Math.round(playerPositionX) - blockCenter;
                    blocksToDeleteOnX = 2;
                }
                if (isPlayerStandingBetweenBlocks(playerPositionY)) {
                    blockToDeletePosY = Math.round(playerPositionY) - blockCenter;
                    blocksToDeleteOnY = 2;
                }

                var flag = false;
                for (float x = (float) blockToDeletePosX-1; x < blockToDeletePosX + blocksToDeleteOnX-1; x++) {
                    for (float z = (float) blockToDeletePosY-1; z < blockToDeletePosY + blocksToDeleteOnY-1; z++) {
                        var blolck = block.getWorld().getBlockAt(Math.round(x), block.getY(), Math.round(z));
                        if (isOnTnt(blolck)) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    blolck.getRelative(0, -1, 0).setType(Material.AIR);
                                    blolck.getWorld().spawnParticle(Particle.FALLING_DUST, blolck.getLocation().add(0,-1,0),
                                            10, 0.5, 0.5, 0.5, 0.5, Material.COAL_BLOCK.createBlockData());
                                    blolck.getRelative(0, -2, 0).setType(Material.AIR);
                                }
                            }.runTaskLater(BukkitUtils.getPlugin(), 10);
                            flag = true;
                            break;
                        }
                    }
                    if (flag) break;
                }
            }
        }
    }

    @EventHandler
    void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            e.setCancelled(true);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(20);
        }
    }

    @EventHandler
    void onFood(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    static boolean isPlayerStandingBetweenBlocks(double playerPos) {
        return Math.abs(playerPos - Math.round(playerPos)) < blockStandingOffset;
    }

    private boolean isOnTnt(Block block) {
        return !block.getRelative(0,-1,0).getType().isAir()
                && block.getRelative(0,-2,0).getType() == Material.BARRIER;
    }

    private boolean isOnAir(Block block) {
        return block.getRelative(0,-1,0).getType().isAir()
                && block.getRelative(0,-2,0).getType().isAir();
    }

}

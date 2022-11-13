package net.thisisnico.lolz.bedwarshelper;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedwarsHelper extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic

        BukkitUtils.instantiate(this);
        BukkitUtils.registerListener(this);

        world = Bukkit.getWorlds().get(0);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    boolean show = false;
    World world;
    String team = null;

    @EventHandler
    void onTick(ServerTickEndEvent e) {
        var flag = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            var item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.STICK) {
                show = true;
                flag = true;
                break;
            }
        }
        if (!flag) show = false;

        for (ArmorStand entity : world.getEntitiesByClass(ArmorStand.class)) {
            if (show) {
                entity.setGlowing(true);
                entity.setSmall(true);
                entity.setCustomNameVisible(true);
                entity.customName(Component.color("&b"+entity.getScoreboardTags().toArray(String[]::new)[0]));
            } else {
                entity.setGlowing(false);
                entity.setSmall(true);
                entity.setCustomNameVisible(false);
            }
        }
    }

    @EventHandler
    void onScroll(PlayerItemHeldEvent e) {
        var count = e.getPreviousSlot()-e.getNewSlot();
        if (count == 0) return;
        var player = e.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (player.isSneaking() && item.getType() == Material.STICK) {
            e.setCancelled(true);
            item.setAmount(item.getAmount()+count);
        }
    }

    @EventHandler
    void onClick(PlayerInteractEvent e) {
        var item = e.getPlayer().getInventory().getItemInMainHand();
        var count = item.getAmount();
        var block = e.getClickedBlock();
        if (!e.getAction().isRightClick()) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.STICK) return;
        if (block != null) {
            var type = block.getType();
            if (type == Material.GOLD_BLOCK || type == Material.IRON_BLOCK || type == Material.BRICKS) {
                if (count > 0) {
                    e.setCancelled(true);
                    var stand = world.spawn(block.getLocation().add(0.5, 1.1, 0.5), ArmorStand.class);
                    stand.setInvulnerable(true);
                    stand.setGravity(false);
                    stand.setSmall(true);
                    stand.addScoreboardTag("generator_"+count);
                }
            } else if (type.name().endsWith("BED")) {
                e.setCancelled(true);
                team = type.name().split("_")[0].toLowerCase().replace("light", "aqua");
                var stand = world.spawn(block.getLocation().add(0.5, 0.5, 0.5), ArmorStand.class);
                stand.setInvulnerable(true);
                stand.setGravity(false);
                stand.setSmall(true);
                stand.addScoreboardTag(team+"_bed");
            } else if (team != null) {
                e.setCancelled(true);
                var yaw = e.getPlayer().getLocation().getYaw();
                if (yaw > 0) yaw -= 180;
                else yaw += 180;
                // round yaw
                yaw = Math.round(yaw/45)*45;
                var stand = world.spawn(block.getLocation().add(0.5, 1.1, 0.5), ArmorStand.class);
                stand.setInvulnerable(true);
                stand.setGravity(false);
                stand.setSmall(true);
                stand.addScoreboardTag(team+"_spawn");
                var loc = stand.getLocation();
                loc.setYaw(yaw);
                loc.setPitch(0);
                stand.teleport(loc);
                var helmet = new ItemStack(Material.LEATHER_HELMET, 1);
                var meta = (LeatherArmorMeta) helmet.getItemMeta();
                meta.setColor(switch (team) {
                    case "red" -> org.bukkit.Color.RED;
                    case "blue" -> org.bukkit.Color.BLUE;
                    case "green" -> org.bukkit.Color.GREEN;
                    case "yellow" -> org.bukkit.Color.YELLOW;
                    case "aqua" -> org.bukkit.Color.AQUA;
                    case "white" -> org.bukkit.Color.WHITE;
                    case "gray" -> org.bukkit.Color.GRAY;
                    case "pink" -> org.bukkit.Color.FUCHSIA;
                    default -> org.bukkit.Color.PURPLE;
                });
                helmet.setItemMeta(meta);
                stand.getEquipment().setHelmet(helmet);
                team = null;
            } else if (e.getPlayer().isSneaking()) {
                e.setCancelled(true);
                var yaw = e.getPlayer().getLocation().getYaw();
                if (yaw > 0) yaw -= 180;
                else yaw += 180;
                // round yaw
                yaw = Math.round(yaw/45)*45;
                var villager = world.spawn(block.getLocation().add(0.5, 1.01, 0.5), Villager.class);
                villager.setInvulnerable(true);
                villager.setGravity(false);
                villager.setCustomNameVisible(true);
                villager.setAI(false);
                villager.setSilent(true);
                villager.customName(Component.color("&eМагазин"));
                var loc = villager.getLocation();
                loc.setYaw(yaw);
                loc.setPitch(0);
                villager.teleport(loc);
            }
        }
    }

    @EventHandler
    void onClickEntity(PlayerInteractAtEntityEvent e) {
        var entity = e.getRightClicked();
        if (entity instanceof ArmorStand || entity instanceof Villager) {
            if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
                entity.remove();
            }
        }
    }
}

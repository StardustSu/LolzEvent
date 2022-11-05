package net.thisisnico.lolz.bedwars.classes;

import lombok.Getter;
import net.thisisnico.lolz.bedwars.Game;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ResourceGenerator {

    @Getter
    private final Location location;

    @Getter
    private final ItemStack resource;
    private final BukkitTask task;
    @Getter
    private final ArmorStand hologram;

    /**
     * Creates basic generator
     */
    public ResourceGenerator(Location location, ItemStack resource, int seconds) {
        this.location = location;
        this.resource = resource;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!Game.isRunning()) return;
                for (Item item : location.getNearbyEntitiesByType(Item.class, 1)) {
                    if (item.getItemStack().getType() == resource.getType()) {
                        if (item.getItemStack().getAmount() >= 64) {
                            return;
                        }
                    }
                }
                location.getWorld().dropItem(location, resource);
            }
        }.runTaskTimer(BukkitUtils.getPlugin(), 0, seconds * 20L);

        hologram = location.getWorld().spawn(location.clone().add(0, 2, 0), ArmorStand.class);
        hologram.setCustomNameVisible(true);
        hologram.customName(Component.color("&eГенератор ресурсов"));
        hologram.setGravity(false);
        hologram.setVisible(false);
        hologram.addScoreboardTag("generator");
    }

    public void dispose() {
        task.cancel();
        hologram.remove();
    }
}

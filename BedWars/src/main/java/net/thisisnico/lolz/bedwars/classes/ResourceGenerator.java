package net.thisisnico.lolz.bedwars.classes;

import lombok.Getter;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ResourceGenerator {

    private final Arena arena;

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
    public ResourceGenerator(Arena arena, Location location, ItemStack resource, int seconds) {
        this.arena = arena;
        this.location = location;
        this.resource = resource;
        task = new BukkitRunnable() {
            @Override
            public void run() {
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

}

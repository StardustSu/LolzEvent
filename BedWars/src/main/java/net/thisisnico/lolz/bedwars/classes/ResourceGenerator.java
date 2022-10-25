package net.thisisnico.lolz.bedwars.classes;

import lombok.Getter;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.Location;
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

    /**
     * Creates basic generator
     * TODO: Make it more FANCY
     */
    public ResourceGenerator(Arena arena, Location location, ItemStack resource, int seconds) {
        this.arena = arena;
        this.location = location;
        this.resource = resource;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                location.getWorld().dropItemNaturally(location, resource);
            }
        }.runTaskTimer(BukkitUtils.getPlugin(), 0, seconds * 20L);
    }

}

package net.thisisnico.lolz.bukkit.utils;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.TNTPrimed;

public class Fun {

    public static void tnt(Entity source, Location loc, int fuseTime, int power) {
        var world = loc.getWorld();
        var t = (TNTPrimed) world.spawnEntity(loc, EntityType.PRIMED_TNT);
        t.setYield(power);
        t.setFuseTicks(fuseTime);
        t.setSource(source);
    }

    public static void tnt(Location loc, int fuseTime, int power) {
        tnt(null, loc, fuseTime, power);
    }

    public static void firework(Location loc, int power, FireworkEffect... fwe) {
        var world = loc.getWorld();
        var fw = (Firework) world.spawnEntity(loc, EntityType.FIREWORK);
        var meta = fw.getFireworkMeta();

        meta.setPower(power);
        meta.addEffects(fwe);
        fw.setFireworkMeta(meta);
    }

}

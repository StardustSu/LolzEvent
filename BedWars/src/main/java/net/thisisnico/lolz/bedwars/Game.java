package net.thisisnico.lolz.bedwars;

import lombok.Getter;
import net.thisisnico.lolz.bedwars.classes.Arena;
import net.thisisnico.lolz.bedwars.classes.ResourceGenerator;
import net.thisisnico.lolz.bedwars.classes.Team;
import net.thisisnico.lolz.bedwars.listeners.GameHandler;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashSet;

public class Game {

    @Getter
    private static Arena arena;

    @Getter
    private static final ArrayList<Team> teams = new ArrayList<>();

    @Getter
    private static final ArrayList<ResourceGenerator> generators = new ArrayList<>();

    @Getter
    private static final HashSet<Block> playerBlocks = new HashSet<>();

    private static boolean isRunning = false;

    public static boolean isRunning() {
        return isRunning;
    }

    public static boolean isPlayerInGame(Player player) {
        for (Team team : teams) {
            if (team.getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    private static void broadcast(String message) {
//        for (Team team : teams) {
//            for (Player player : team.getPlayers()) {
//                player.sendMessage(message);
//            }
//        }

        Bukkit.getServer().forEachAudience(au -> {
            au.sendMessage(Component.color(message));
        });
    }

    public static boolean destroyBed(Location l, Player p) {
        for (Team team : teams) {
            if (team.checkBedLocation(l)) {
                if (team.isPlayerInTeam(p)) {
                    p.sendMessage("§cВы не можете уничтожить свою кровать!");
                    return false;
                }

                team.destroyBed();
                broadcast("Кровать команды " + team.getName() + " была уничтожена игроком " + p.getName() + "!");
                team.setCoolDudeWhoBrokeDaBed(p);

                var clan = DatabaseAdapter.getClan(p);
                if (clan == null) p.sendMessage(Component.color("&cТы не в клане"));
                else clan.givePoints(Const.POINTS_FOR_BED);

                return true;
            }
        }
        return false;
    }

    public static void init() {
        BukkitUtils.registerListener(new GameHandler());

        arena = new Arena(BukkitUtils.getPlugin().getServer().getWorlds().get(0));

        // TODO. Load arena
    }

    public static void dispose() {
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                player.teleport(arena.getWorld().getSpawnLocation());
            }
        }
        teams.clear();
        for (ResourceGenerator generator : generators) {
            generator.getHologram().remove();
        }
        for (Block playerBlock : playerBlocks) {
            playerBlock.setType(Material.AIR);
        }
        for (Entity entity : arena.getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                entity.remove();
            }
        }
    }

    public static void start() {

        

        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                player.teleport(team.getSpawnLocation());
                givePlayerStartItems(player, team);
            }
        }

        isRunning = true;
    }

    public static void givePlayerStartItems(Player player, Team team) {
        player.getInventory().clear();

        var is = new ItemStack(Material.LEATHER_HELMET);
        var meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(team.getColor());
        is.setItemMeta(meta);
        player.getInventory().setHelmet(is);

        is = new ItemStack(Material.LEATHER_CHESTPLATE);
        meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(team.getColor());
        is.setItemMeta(meta);
        player.getInventory().setChestplate(is);

        is = new ItemStack(Material.LEATHER_LEGGINGS);
        meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(team.getColor());
        is.setItemMeta(meta);
        player.getInventory().setLeggings(is);

        is = new ItemStack(Material.LEATHER_BOOTS);
        meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(team.getColor());
        is.setItemMeta(meta);
        player.getInventory().setBoots(is);
    }

    public static void stop() {
        isRunning = false;
    }

    public static Team getTeam(Player player) {
        for (Team team : teams) {
            if (team.getPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }
}

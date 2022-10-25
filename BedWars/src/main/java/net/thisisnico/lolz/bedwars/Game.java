package net.thisisnico.lolz.bedwars;

import lombok.Getter;
import net.thisisnico.lolz.bedwars.classes.Arena;
import net.thisisnico.lolz.bedwars.classes.Team;
import net.thisisnico.lolz.bedwars.listeners.GameHandler;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Objects;

public class Game {

    @Getter
    private static Arena arena;

    private static ArrayList<Team> teams = new ArrayList<>();

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
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                player.sendMessage(message);
            }
        }
    }

    public static boolean destroyBed(Location l, Player p) {
        for (Team team : teams) {
            if (team.checkBedLocation(l)) {
                if (team.isPlayerInTeam(p)) {
                    p.sendMessage("§cВы не можете уничтожить свою кровать!");
                    return false;
                }

                team.destroyBed();

                for (Team t : teams) {
                    if (t.isPlayerInTeam(p)) {
                        t.addBedsDestroyed();
                        broadcast("Кровать команды " + team.getName() + " была уничтожена игроком " + p.getName() + " из команды " + t.getName() + "!");
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static void init() {
        BukkitUtils.registerListener(new GameHandler());

        arena = new Arena(BukkitUtils.getPlugin().getServer().getWorlds().get(0));
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

    public static void givePlayerStartItems(Player player, Team team){
        player.getInventory().clear();

        player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));

        ((LeatherArmorMeta) Objects.requireNonNull(player.getInventory().getHelmet()).getItemMeta()).setColor(team.getColor());
        ((LeatherArmorMeta) Objects.requireNonNull(player.getInventory().getChestplate()).getItemMeta()).setColor(team.getColor());
        ((LeatherArmorMeta) Objects.requireNonNull(player.getInventory().getLeggings()).getItemMeta()).setColor(team.getColor());
        ((LeatherArmorMeta) Objects.requireNonNull(player.getInventory().getBoots()).getItemMeta()).setColor(team.getColor());
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

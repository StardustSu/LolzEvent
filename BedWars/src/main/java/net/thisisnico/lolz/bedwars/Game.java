package net.thisisnico.lolz.bedwars;

import lombok.Getter;
import net.thisisnico.lolz.bedwars.classes.Arena;
import net.thisisnico.lolz.bedwars.classes.ResourceGenerator;
import net.thisisnico.lolz.bedwars.classes.Team;
import net.thisisnico.lolz.bedwars.classes.TeamColor;
import net.thisisnico.lolz.bedwars.listeners.GameHandler;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import net.thisisnico.lolz.common.database.Clan;
import net.thisisnico.lolz.common.database.Database;
import net.thisisnico.lolz.common.database.User;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;

public class Game {

    @Getter
    private static Arena arena;

    @Getter
    private static final ArrayList<Team> teams = new ArrayList<>();

    @Getter
    private static final ArrayList<ResourceGenerator> generators = new ArrayList<>();

    private static boolean isRunning = false;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
        Bukkit.getServer().forEachAudience(au -> au.sendMessage(Component.color(message)));
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
    }

    public static void startTimer() {
        final int[] i = {10};
        Bukkit.getScheduler().runTaskTimer(BukkitUtils.getPlugin(), task -> {
            if (i[0] == 0) {
                start();
                task.cancel();
            } else {
                broadcast("&aИгра начнется через &c" + i[0] + " секунд!");
                if (i[0] < 6) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 1f, .5f);
                    }
                }
            }
            i[0]--;
        }, 0, 20);
    }

    public static void start() {
        var clans = new ArrayList<Clan>();
        Database.getClans().find().forEach(clans::add);

        var users = new ArrayList<User>();
        Database.getUsers().find().forEach(users::add);

        for (Player player : Bukkit.getOnlinePlayers()) {
            var user = users.stream().filter(u -> u.getName().equalsIgnoreCase(player.getName())).findFirst().orElse(null);
            assert user != null;
            Clan clan = null;
            if (user.hasClan()) clan = clans.stream().filter(c -> c.getTag().equalsIgnoreCase(user.getClan())).findFirst().orElse(null);
            if (clan == null) {
                player.kick(Component.color("&cТы не в клане"));
                continue;
            }

            Clan finalClan = clan;
            var team = teams.stream().filter(t -> t.getName().equalsIgnoreCase(finalClan.getTag())).findFirst().orElse(null);
            if (team == null) {
                team = new Team(clan.getTag());
                teams.add(team);
            }

            team.addPlayer(player);
        }

        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                player.teleport(team.getSpawnLocation());
                givePlayerStartItems(player, team);
            }
        }

        isRunning = true;
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
        for (Block playerBlock : arena.getPlayerBlocks()) {
            playerBlock.setType(Material.AIR);
        }
        for (Entity entity : arena.getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                entity.remove();
            }
        }
    }

    public static void givePlayerStartItems(Player player, Team team) {
        player.getInventory().clear();
        var color = Color.fromRGB(team.getColor().getColor().red(), team.getColor().getColor().green(), team.getColor().getColor().blue());
        var is = new ItemStack(Material.LEATHER_HELMET);
        var meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(color);
        is.setItemMeta(meta);
        player.getInventory().setHelmet(is);

        is = new ItemStack(Material.LEATHER_CHESTPLATE);
        meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(color);
        is.setItemMeta(meta);
        player.getInventory().setChestplate(is);

        is = new ItemStack(Material.LEATHER_LEGGINGS);
        meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(color);
        is.setItemMeta(meta);
        player.getInventory().setLeggings(is);

        is = new ItemStack(Material.LEATHER_BOOTS);
        meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(color);
        is.setItemMeta(meta);
        player.getInventory().setBoots(is);
    }

    public static void stop() {
        isRunning = false;
        TeamColor.clearTakenColors();

        // TODO. end da game
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

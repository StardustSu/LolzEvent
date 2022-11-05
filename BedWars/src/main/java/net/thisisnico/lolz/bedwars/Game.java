package net.thisisnico.lolz.bedwars;

import lombok.Getter;
import net.thisisnico.lolz.bedwars.classes.Arena;
import net.thisisnico.lolz.bedwars.classes.ResourceGenerator;
import net.thisisnico.lolz.bedwars.classes.Team;
import net.thisisnico.lolz.bedwars.classes.TeamColor;
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
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Game {

    @Getter
    private static Arena arena;

    @Getter
    private static final ArrayList<Team> teams = new ArrayList<>();

    @Getter
    private static final ArrayList<ResourceGenerator> generators = new ArrayList<>();

    private static final HashMap<ArmorStand, Location> tpArmorStandsBack = new HashMap<>();

    private static boolean isRunning = false;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isRunning() {
        return isRunning;
    }

    public static boolean isPlayerInGame(Player player) {
        for (Team team : teams) {
            if (team.getPlayers().contains(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOfflinePlayerInGame(OfflinePlayer player) {
        for (Team team : teams) {
            if (team.getPlayers().stream().anyMatch(name -> name.equalsIgnoreCase(player.getName()))) {
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

                var destroyTeam = Game.getTeam(p);
                if (destroyTeam == null) return false;
                team.destroyBed();
                Bukkit.broadcast(Component.color("&f&lУНИЧТОЖЕНИЕ КРОВАТИ > &7Кровать команды ")
                        .append(Component.color(team.getName()).color(team.getColor().getColor()))
                        .append(Component.color(" &7была уничтожена игроком "))
                        .append(Component.color(p.getName()).color(destroyTeam.getColor().getColor())));
                team.setCoolDudeWhoBrokeDaBed(p);

                // remove offline players from team
                team.getPlayers().removeIf(player -> Bukkit.getPlayerExact(player) == null);
                if (team.getPlayers().size() == 0) eliminateTeam(team);

                var clan = DatabaseAdapter.getClan(p);
                if (clan == null) p.sendMessage(Component.color("&cТы не в клане"));
                else {
                    destroyTeam.addScore(Const.POINTS_FOR_BED);
                    clan.givePoints(Const.POINTS_FOR_BED);
                }

                return true;
            }
        }
        return false;
    }

    public static void init() {
        arena = new Arena(BukkitUtils.getPlugin().getServer().getWorlds().get(0));
    }

    public static void startTimer() {
        for (ResourceGenerator generator : generators) {
            generator.dispose();
        }
        generators.clear();

        for (ArmorStand entity : arena.getWorld().getEntitiesByClass(ArmorStand.class)) {
            for (String tag : entity.getScoreboardTags()) {
                if (tag.startsWith("generator_")) {
                    var seconds = Integer.parseInt(tag.split("_")[1]);
                    var block = entity.getLocation().getBlock().getRelative(0,-1,0);
                    generators.add(new ResourceGenerator(entity.getLocation(), new ItemStack(switch (block.getType()) {
                        case BRICKS -> Material.BRICK;
                        case IRON_BLOCK -> Material.IRON_INGOT;
                        case GOLD_BLOCK -> Material.GOLD_INGOT;
                        case EMERALD_BLOCK -> Material.EMERALD;
                        case DIAMOND_BLOCK -> Material.DIAMOND;
                        default -> Material.FEATHER;
                    }, 1), seconds));
                }
            }
        }

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
        if (isRunning) return;

        for (Item entity : arena.getWorld().getEntitiesByClass(Item.class)) {
            if (entity.getItemStack().getType().name().contains("BED")) {
                entity.remove();
            }
        }

        var clans = new ArrayList<Clan>();
        Database.getClans().find().forEach(clans::add);

        var users = new ArrayList<User>();
        Database.getUsers().find().forEach(users::add);

        for (Player player : Bukkit.getOnlinePlayers()) {
            var user = users.stream().filter(u -> u.getName().equalsIgnoreCase(player.getName())).findFirst().orElse(null);
            assert user != null;

            if (user.isAdmin()) {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(arena.getSpectatorSpawnLocation());
                continue;
            }

            Clan clan = null;
            if (user.hasClan()) clan = clans.stream().filter(c -> c.getTag().equalsIgnoreCase(user.getClan())).findFirst().orElse(null);
            if (clan == null) {
                player.kick(Component.color("&cТы не в клане"));
                continue;
            }

            player.setFoodLevel(20);
            player.setHealth(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().setContents(new ItemStack[] {});

            Clan finalClan = clan;
            var team = teams.stream().filter(t -> t.getName().equalsIgnoreCase(finalClan.getTag())).findFirst().orElse(null);
            if (team == null) {
                team = new Team(clan.getTag());
                teams.add(team);
            }

            team.addPlayer(player);
            player.teleport(team.getSpawnLocation());
        }

        for (ArmorStand entity : arena.getWorld().getEntitiesByClass(ArmorStand.class)) {
            if (!entity.getScoreboardTags().contains("generator")) {
                tpArmorStandsBack.put(entity, entity.getLocation());
                entity.teleport(new Location(entity.getWorld(), 0, 0, 0));
            }
        }

        isRunning = true;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Coloring.updateColors(onlinePlayer);
        }
    }

    public static void dispose() {
        WorldEditStuff.load("arena");
        for (Team team : teams) {
            for (String player : team.getPlayers()) {
                if (Bukkit.getPlayerExact(player) != null)
                    Bukkit.getPlayerExact(player).teleport(arena.getWorld().getSpawnLocation());
            }
        }
        teams.clear();
        for (ResourceGenerator generator : generators) {
            generator.dispose();
        }
        generators.clear();
        for (Block playerBlock : arena.getPlayerBlocks()) {
            playerBlock.setType(Material.AIR);
        }
        for (Entity entity : arena.getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                if (entity.getScoreboardTags().contains("generator")) entity.remove();
            }
            if (entity instanceof Item) {
                entity.remove();
            }
        }

        for (Map.Entry<ArmorStand, Location> entry : tpArmorStandsBack.entrySet()) {
            entry.getKey().teleport(entry.getValue());
        }
        tpArmorStandsBack.clear();
    }
    
    public static void kill(Player player, boolean respawn) {
        Bukkit.getScheduler().runTaskLater(BedWars.getInstance(), () -> {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(Game.getArena().getSpectatorSpawnLocation());
        }, 1L);

        Team team = Game.getTeam(player);

        if (team == null) return;

        OfflinePlayer killer = player.getKiller();
        if (killer == null || killer == player) killer = team.getCoolDudeWhoBrokeDaBed();

        if (killer == null) Bukkit.broadcast(Component.color(player.getName()).color(team.getColor().getColor())
                .append(Component.color(" &7умер"))
                .append(Component.color(team.isBedDestroyed() ? " &b&lФИНАЛЬНОЕ УБИЙСТВО!" : "")));
        else Bukkit.broadcast(Component.color(player.getName()).color(team.getColor().getColor())
                .append(Component.color(" &7был убит игроком "))
                .append(Component.color(killer.getName()).color(Game.getTeam(killer).getColor().getColor()))
                .append(Component.color(team.isBedDestroyed() ? " &b&lФИНАЛЬНОЕ УБИЙСТВО!" : "")));

        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);

        if (team.isBedDestroyed()) {
            if (killer != null) {
                var clan = DatabaseAdapter.getClan(killer);
                if (clan == null) {
                    if (killer.isOnline()) Objects.requireNonNull(killer.getPlayer()).sendMessage(Component.color("&cТы не в клане"));
                    return;
                }
                clan.givePoints(Const.POINTS_FOR_FINAL_KILL);
                Game.getTeam(killer).addScore(Const.POINTS_FOR_FINAL_KILL);
            }

            team.getPlayers().remove(player.getName());
            eliminateTeam(team);
        }
        else if (respawn) respawn(player, 1);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Coloring.updateColors(onlinePlayer);
        }
    }

    public static void eliminateTeam(Team team) {
        var size_only_online_players = team.getPlayers().stream()
                .filter(player -> Bukkit.getPlayerExact(player) != null).count();
        if (size_only_online_players == 0) {
            Bukkit.broadcast(Component.color("&f&lУНИЧТОЖЕНИЕ КОМАНДЫ > ").append(Component.color(team.getName()).color(team.getColor().getColor())
                    .append(Component.color(" &cвыбывает из игры!"))));
            teams.remove(team);
        }

        if (teams.size() == 1) {
            for (String offline : teams.get(0).getPlayers()) {
                if (Bukkit.getPlayerExact(offline) != null) {
                    Bukkit.getPlayerExact(offline).getInventory().setArmorContents(new ItemStack[] {});
                }
            }
            Bukkit.broadcast(Component.color("&f"));
            Bukkit.broadcast(Component.color("&9&m========================="));
            Bukkit.broadcast(Component.color("&f"));
            Bukkit.broadcast(Component.color(teams.get(0).getName()).color(teams.get(0).getColor().getColor())
                    .append(Component.color(" &7 - &b&lПОБЕДИТЕЛЬ")));
            Bukkit.broadcast(Component.center("     &d(+"+(Const.POINTS_FOR_WIN + teams.get(0).getScore())+" очков)"));
            Bukkit.broadcast(Component.color("&f"));
            Bukkit.broadcast(Component.color("&9&m========================="));
            Bukkit.broadcast(Component.color("&f"));
            Clan.get(teams.get(0).getName()).givePoints(Const.POINTS_FOR_WIN);
            stop();
        }
    }

    public static void respawn(Player player, int k) {
        Team team = Game.getTeam(player);
        if (team == null) return;
        if (team.isBedDestroyed()) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(Game.getArena().getSpectatorSpawnLocation());
            player.sendMessage(Component.color("&cКровать твоей команды была сломана. Ты не возродишься."));
            return;
        }
        final int[] i = {Const.RESPAWN_DELAY * k};
        Bukkit.getScheduler().runTaskTimer(BukkitUtils.getPlugin(), task -> {
            if (!player.isOnline()) {
                task.cancel();
                return;
            }
            if (i[0] == 0) {
                player.teleport(team.getSpawnLocation());
                player.setGameMode(GameMode.SURVIVAL);
                player.setAllowFlight(false);
                player.setFlying(false);
                player.getInventory().setContents(new ItemStack[]{});
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setSaturation(10);
                for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                    player.removePotionEffect(activePotionEffect.getType());
                }
                task.cancel();
            } else {
                player.teleport(Game.getArena().getSpectatorSpawnLocation());
                player.setGameMode(GameMode.SPECTATOR);
                player.sendActionBar(Component.color("&cРеспавн через " + i[0] + " сек"));
                i[0]--;
            }
        }, 0L, 20L);
    }

    public static void stop() {
        isRunning = false;
        TeamColor.clearTakenColors();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Coloring.updateColors(onlinePlayer);
        }

        Bukkit.getScheduler().runTaskLater(BukkitUtils.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.isOp()) continue;
                onlinePlayer.kick(Component.color("&cИгра окончена"));
            }
            dispose();
        }, 20L * 10);

    }

    public static Team getTeam(OfflinePlayer player) {
        for (Team team : teams) {
            if (team.getPlayers().contains(player.getName())) {
                return team;
            }
        }
        return null;
    }
}

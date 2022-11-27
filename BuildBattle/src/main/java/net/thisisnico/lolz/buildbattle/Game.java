package net.thisisnico.lolz.buildbattle;

import lombok.Getter;
import net.kyori.adventure.title.Title;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.ClickableItem;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.bukkit.utils.ItemUtil;
import net.thisisnico.lolz.common.database.Clan;
import net.thisisnico.lolz.common.database.Database;
import net.thisisnico.lolz.common.database.User;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Game {
    @Getter
    private static final ClickableItem startGameItem = ClickableItem.of(ItemUtil.generate(Material.DIAMOND, 1,
            "&a&lBuildBattle", "&7Click to start the game!"), p -> start());

    @Getter
    private static GameState state = GameState.LOBBY;
    @Getter
    private static World world;
    @Getter
    private static Location spawn;

    private static String theme;

    @Getter
    private static boolean tournamentMode = true;

    @Getter
    private static BossBar bar;

    public static boolean isStarted() {
        return state != GameState.LOBBY;
    }

    @Getter
    private static final HashSet<String> players = new HashSet<>();

    private static final HashSet<Plot> plots = new HashSet<>();

    public static Plot getPlot(String player) {
        if (!isPlayer(player)) return null;
        if (!isStarted()) return null;

        for (Plot plot : plots) {
            if (plot.getOwners().contains(player)) {
                return plot;
            }
        }
        return null;
    }

    @Getter
    private static Plot currentPlot = null;

    public static void init() {
        world = Bukkit.getWorlds().get(0);
        spawn = world.getSpawnLocation();
        bar = Bukkit.createBossBar("Build Battle", BarColor.BLUE, BarStyle.SOLID);
        bar.setProgress(.0f);

        BukkitUtils.getPlugin().saveDefaultConfig();
        tournamentMode = BukkitUtils.getPlugin().getConfig().getBoolean("tournament-mode", false);
    }

    public static void start() {
        if (state != GameState.LOBBY) return;
        state = GameState.BUILDING;

        Location location = spawn.clone();

        location.add(0.5, 0, 50.5);

        theme = ThemeProvider.getInstance().getRandomTheme();
        bar.setTitle(theme);
        bar.setProgress(1.0f);

        List<Location> locations = new ArrayList<>();

        var users = new ArrayList<User>();
        Database.getUsers().find().forEach(users::add);

        for (Player player : Bukkit.getOnlinePlayers()) {
            var user = users.stream().filter(u -> u.getName().equalsIgnoreCase(player.getName())).findFirst().orElse(null);
            if (user == null) {
                player.kick(Component.color("&c&lWTF????"));
                continue;
            }

            if (tournamentMode && user.isAdmin()) {
                player.setGameMode(GameMode.SPECTATOR);
                continue;
            }

            Plot plot = tournamentMode ?
                    plots.stream().filter(p -> p.getName().equalsIgnoreCase(user.getClan())).findFirst().orElse(null) :
                    null;

            // Каждый плот размером 32 на 32 с запасом для красивых стен
            location.add(0, 0, 50);

            if (plot == null) {
                plot = new Plot(
                        tournamentMode ? user.getClan() : player.getName(),
                        location.clone(), player.getName());
                plots.add(plot);
                player.teleport(location);
            } else {
                plot.getOwners().add(player.getName());
                player.teleport(plot.getLocation());
            }

            players.add(player.getName());
            locations.add(location.clone());
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.setFlying(false);
            player.setHealth(20d);
            player.setFoodLevel(20);

            player.sendMessage("§a§lBuildBattle §7| §aУ вас есть 5 минут на то, чтобы сделать");
            player.sendMessage("§a§lBuildBattle §7| §aкрасивую постройку на тему \"" + theme + "\"!");

            player.showTitle(Title.title(Component.color("&a" + theme), Component.color("&eУ вас есть 5 минут на постройку!")));
        }

        WorldEditStuff.load("plot", locations.toArray(Location[]::new));

        final int time = 5 * 60 * 20;
        final int[] timer = {0};
        Bukkit.getScheduler().runTaskTimer(BuildBattle.getInstance(), task -> {
            if (state != GameState.BUILDING) {
                task.cancel();
                return;
            }
            timer[0]++;

            bar.setProgress(1 - ((double) timer[0] / time));

            if (timer[0] >= time) {
                task.cancel();
                Game.startVote();
            }
        }, 0, 1);
    }

    private static void givePlayerVoteItems(Player p) {
        p.setGameMode(GameMode.ADVENTURE);

        p.setAllowFlight(true);
        p.setFlying(true);

        Inventory i = p.getInventory();

        i.clear();

        i.addItem(redVoteItem.getItemStack());
        i.addItem(orangeVoteItem.getItemStack());
        i.addItem(yellowVoteItem.getItemStack());
        i.addItem(limeVoteItem.getItemStack());
        i.addItem(greenVoteItem.getItemStack());
        i.setItem(8, reportVoteItem.getItemStack());
    }

    private static void startVote() {
        state = GameState.VOTING;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (tournamentMode) {
                if (p.isOp()) givePlayerVoteItems(p);
                else p.setGameMode(GameMode.SPECTATOR);
                continue;
            }

            if (players.contains(p.getName())) {
                givePlayerVoteItems(p);
            } else {
                p.setGameMode(GameMode.SPECTATOR);
            }
        }

        BuildBattle.getLog().info("Vote started!");
        BuildBattle.getLog().info("Plots count: " + plots.size());

        var trashPlots = new HashSet<>(plots);
        var tempPlots = new HashSet<>(plots);

        var plotIter = trashPlots.iterator();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plotIter.hasNext()) {
                    BuildBattle.getLog().info("No plots left!");
                    cancel();
                    end();
                    return;
                }

                var plot = plotIter.next();

                currentPlot = plot;

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("§a§lBuildBattle §7| §aПостройка клана " + plot.getName());
                    p.teleport(currentPlot.getLocation().clone().add(0, 10, -16));
                }

                tempPlots.remove(plot);
            }
        }.runTaskTimer(BuildBattle.getInstance(), 0, 20 * 20);
    }

    public static void end() {
        var sortedPlots = new ArrayList<>(plots);
        sortedPlots.sort((o1, o2) -> o2.getScore() - o1.getScore());

        currentPlot = sortedPlots.get(0);

        // TODO: if (tournament) give reward
        // Показать победителя
        for (int i = 0; i < sortedPlots.size(); i++) {
            var plot = sortedPlots.get(i);
            var players = plot.getOwners().stream().map(Bukkit::getOfflinePlayerIfCached).toList();

            var score = plot.getScores().values().size() != 0 ? plot.getScore() / plot.getScores().values().size() : 0;
            if (score != 0) {
                for (OfflinePlayer player : players) {
                    if (player.getPlayer() != null) {
                        player.getPlayer().sendMessage("§a§lBuildBattle §7| §aВаша постройка заняла " + (i + 1) + " место!");
                        player.getPlayer().sendMessage("§a§lBuildBattle §7| §aСредняя оценка: " + score);
                    }
                }

                if (tournamentMode)
                    Clan.get(plot.getName()).givePoints(score * 10);
            } else {
                for (OfflinePlayer player : players) {
                    if (player.getPlayer() != null) {
                        player.getPlayer().sendMessage("§a§lBuildBattle §7| §aВаша постройка заняла " + (i + 1) + " место!");
                        player.getPlayer().sendMessage("§a§lBuildBattle §7| §aСредняя оценка: " + score);
                    }
                }
            }

            if (i < 4) {
                Bukkit.broadcast(Component.color("§a§lBuildBattle §7| §a" + (i + 1) + " место: " + plot.getName() + " (" + plot.getScore() + " очков)"));

                if (tournamentMode)
                    Clan.get(plot.getName()).givePoints((3 - i) * 10);
            }
        }

        // Показать плот победителя
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(currentPlot.getLocation().clone().add(0, 10, -16));
            player.getInventory().clear();
        }

        state = GameState.LOBBY;

        final int DELAY = 20 * 3;

        Bukkit.getScheduler().runTaskLater(BuildBattle.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(spawn);

                if (player.isOp()) {
                    player.getInventory().addItem(startGameItem.getItemStack());
                    player.setGameMode(GameMode.CREATIVE);
                    continue;
                }

                player.setGameMode(GameMode.ADVENTURE);
                player.setAllowFlight(false);
                player.setFlying(false);
                player.kick(Component.color("&cИгра окончена!"));
            }
        }, DELAY);

        players.clear();
        plots.clear();
        currentPlot = null;
    }

    public static boolean isPlayer(String p) {
        for (String player : players) {
            if (player.equals(p)) {
                for (Plot plot : plots) {
                    if (plot.getOwners().contains(p)) {
                        break;
                    }
                }
                players.remove(player);
                players.add(p);
                return true;
            }
        }
        return false;
    }

    // Voting items
    private static final ClickableItem greenVoteItem = ClickableItem.of(ItemUtil.generate(Material.GREEN_TERRACOTTA, 5, "&a&l5", "&7Click to vote for this plot!"), p -> {
        Game.voteForCurrentPlot(p, 5);
    });
    private static final ClickableItem limeVoteItem = ClickableItem.of(ItemUtil.generate(Material.LIME_TERRACOTTA, 4, "&a&l4", "&7Click to vote for this plot!"), p -> {
        Game.voteForCurrentPlot(p, 4);
    });
    private static final ClickableItem yellowVoteItem = ClickableItem.of(ItemUtil.generate(Material.YELLOW_TERRACOTTA, 3, "&e&l3", "&7Click to vote for this plot!"), p -> {
        Game.voteForCurrentPlot(p, 3);
    });
    private static final ClickableItem orangeVoteItem = ClickableItem.of(ItemUtil.generate(Material.ORANGE_TERRACOTTA, 2, "&6&l2", "&7Click to vote for this plot!"), p -> {
        Game.voteForCurrentPlot(p, 2);
    });
    private static final ClickableItem redVoteItem = ClickableItem.of(ItemUtil.generate(Material.RED_TERRACOTTA, 1, "&c&l1", "&7Click to vote for this plot!"), p -> {
        Game.voteForCurrentPlot(p, 1);
    });
    private static final ClickableItem reportVoteItem = ClickableItem.of(ItemUtil.generate(Material.BARRIER, 1, "&c&lCRINGE!", "&7Click to vote for this plot!"), p -> {
        Game.voteForCurrentPlot(p, -5);
    });

    private static void voteForCurrentPlot(Player player, int score) {
        if (score < -10 || score > 5) return;
        if (currentPlot == null) return;
        currentPlot.addVote(player.getName(), score);
        if (score < 0) player.playSound(player, Sound.ENTITY_CAT_BEG_FOR_FOOD, 1f, .1f);
        else player.playSound(player, Sound.ENTITY_CAT_BEG_FOR_FOOD, 1f, score / 5f);
    }

}

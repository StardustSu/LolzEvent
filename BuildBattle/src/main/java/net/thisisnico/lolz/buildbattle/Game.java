package net.thisisnico.lolz.buildbattle;

import lombok.Getter;
import net.kyori.adventure.title.Title;
import net.thisisnico.lolz.bukkit.utils.ClickableItem;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.bukkit.utils.ItemUtil;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;

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

    private static boolean isTournament = false;

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
            if (plot.getOwner().equals(player)) {
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
    }

    public static void start() {
        if (state != GameState.LOBBY) return;
        state = GameState.BUILDING;

        Location location = spawn.clone();

        location.add(0.5, 0, 50.5);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isTournament && DatabaseAdapter.getUser(player).isAdmin()) {
                player.setGameMode(GameMode.SPECTATOR);
                continue;
            }

            // Каждый плот размером 32 на 32 с запасом для красивых стен
            location.add(0, 0, 50);

            plots.add(new Plot(player.getName(), location.clone()));

            players.add(player.getName());
            player.teleport(location);
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.setFlying(false);
            player.setHealth(20d);
            player.setFoodLevel(20);

            player.sendMessage("§a§lBuildBattle §7| §aУ вас есть 5 минут на то, чтобы сделать");
            player.sendMessage("§a§lBuildBattle §7| §aкрасивую постройку на тему \"" + theme + "\"!");

            player.showTitle(Title.title(Component.color("&a" + theme), Component.color("&eУ вас есть 5 минут на постройку!")));
        }

        Bukkit.getScheduler().runTaskLater(BuildBattle.getInstance(), Game::startVote, 20 * 30);
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
            if (isTournament) {
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
                Player player = Bukkit.getPlayerExact(plot.getOwner());

                currentPlot = plot;

                for (Player p : Bukkit.getOnlinePlayers()) {
                    assert player != null;
                    p.sendMessage("§a§lBuildBattle §7| §aПостройка игрока " + player.getName());
                    p.teleport(plot.getLocation().clone().add(0, 10, -16));
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
        for (int i = 0; i < (sortedPlots.size() < 3 ? sortedPlots.size() : 3); i++) {
            var plot = sortedPlots.get(i);
            var player = Bukkit.getPlayerExact(plot.getOwner());

            assert player != null;
            Bukkit.broadcastMessage("§a§lBuildBattle §7| §a" + (i + 1) + " место: " + player.getName() + " (" + plot.getScore() + " очков)");
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
                    if (plot.getOwner().equals(p)) {
                        plot.setOwner(player);
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
        if (score < 0) player.playSound(player, Sound.ENTITY_CAT_PURREOW, 1f, .1f);
        else player.playSound(player, Sound.ENTITY_CAT_PURREOW, 1f, score / 5f);
    }

}

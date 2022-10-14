package net.thisisnico.lolz.buildbattle;

import lombok.Getter;
import net.thisisnico.lolz.bukkit.utils.ClickableItem;
import net.thisisnico.lolz.bukkit.utils.ItemUtil;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class Game {
    @Getter
    private static final ClickableItem item = ClickableItem.of(ItemUtil.generate(Material.DIAMOND, 1,
            "&a&lBuildBattle", "&7Click to start the game!"), p -> start());

    @Getter
    private static GameState state = GameState.LOBBY;
    @Getter
    private static World world;
    @Getter
    private static Location spawn;

    private static String theme;

    public static boolean isStarted() {
        return state != GameState.LOBBY;
    }

    @Getter
    private static final HashSet<Player> players = new HashSet<>();

    private static final HashSet<Plot> plots = new HashSet<>();

    public static Plot getPlot(Player player) {
        if(!isPlayer(player)) return null;
        if(!isStarted()) return null;

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

        location.add(0, 0, 50);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (DatabaseAdapter.getUser(player).isAdmin()) {
                player.setGameMode(GameMode.SPECTATOR);
                continue;
            }

            plots.add(new Plot(player, location));

            // Каждый плот размером 32 на 32 с запасом для красивых стен
            location.add(0, 0, 50);

            players.add(player);
            player.teleport(location);
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.setFlying(false);
            player.setHealth(20d);
            player.setFoodLevel(20);
        }

        Bukkit.getScheduler().runTaskLater(BuildBattle.getInstance(), Game::startVote, 20 * 60 * 5);
    }

    private static void startVote() {
        state = GameState.VOTING;

        for (Player p : players) {
            p.setGameMode(GameMode.ADVENTURE);
            p.setFlying(true);
            p.setAllowFlight(true);

            p.getInventory().addItem(redVoteItem.getItemStack());
            p.getInventory().addItem(orangeVoteItem.getItemStack());
            p.getInventory().addItem(yellowVoteItem.getItemStack());
            p.getInventory().addItem(limeVoteItem.getItemStack());
            p.getInventory().addItem(greenVoteItem.getItemStack());
            p.getInventory().addItem(reportVoteItem.getItemStack());
        }

        for (Plot plot : plots) {
            Player player = (Player) plot.getOwner();
            currentPlot = plot;

            Bukkit.getScheduler().runTaskTimer(BuildBattle.getInstance(), () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("§a" + player.getName() + "§7 построил §a" + theme);
                    p.teleport(plot.getLocation().clone().add(0, 10, -16));
                }
            }, 0, 20 * 20);
        }
    }

    public static void end() {
        state = GameState.LOBBY;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(spawn);

            if (DatabaseAdapter.getUser(player).isAdmin()) {
                player.setGameMode(GameMode.CREATIVE);
                continue;
            }

            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.getInventory().clear();
        }

        players.clear();
        plots.clear();
        currentPlot = null;
    }

    public static boolean isPlayer(Player p) {
        return players.contains(p);
    }

    public static boolean isSpectator(Player p) {
        return !isPlayer(p);
    }

    // Voting items
    private static final ClickableItem greenVoteItem = ClickableItem.of(ItemUtil.generate(Material.GREEN_TERRACOTTA, 5, "&a&l5", "&7Click to vote for this plot!"), p -> {
        Game.voteForeCurrentPlot(p, 5);
    });
    private static final ClickableItem limeVoteItem = ClickableItem.of(ItemUtil.generate(Material.LIME_TERRACOTTA, 4, "&a&l4", "&7Click to vote for this plot!"), p -> {
        Game.voteForeCurrentPlot(p, 4);
    });
    private static final ClickableItem yellowVoteItem = ClickableItem.of(ItemUtil.generate(Material.YELLOW_TERRACOTTA, 3, "&e&l3", "&7Click to vote for this plot!"), p -> {
        Game.voteForeCurrentPlot(p, 3);
    });
    private static final ClickableItem orangeVoteItem = ClickableItem.of(ItemUtil.generate(Material.ORANGE_TERRACOTTA, 2, "&6&l2", "&7Click to vote for this plot!"), p -> {
        Game.voteForeCurrentPlot(p, 2);
    });
    private static final ClickableItem redVoteItem = ClickableItem.of(ItemUtil.generate(Material.RED_TERRACOTTA, 1, "&c&l1", "&7Click to vote for this plot!"), p -> {
        Game.voteForeCurrentPlot(p, 1);
    });
    private static final ClickableItem reportVoteItem = ClickableItem.of(ItemUtil.generate(Material.BARRIER, 64, "&c&lCRINGE!", "&7Click to vote for this plot!"), p -> {
        Game.voteForeCurrentPlot(p, -5);
    });

    private static void voteForeCurrentPlot(Player player, int score) {
        if (score < -10 || score > 5) return;
        if (currentPlot == null) return;
        currentPlot.addVote(player, score);
    }

}

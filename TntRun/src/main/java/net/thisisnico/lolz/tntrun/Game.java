package net.thisisnico.lolz.tntrun;

import lombok.Getter;
import net.kyori.adventure.title.Title;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.ClickableItem;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.bukkit.utils.ItemUtil;
import net.thisisnico.lolz.bukkit.utils.ScoreboardUtils;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {

    @Getter
    private static final ClickableItem item = ClickableItem.of(ItemUtil.generate(Material.DIAMOND, 1,
            "&a&lTNT Run", "&7Click to start the game!"), p -> start());

    @Getter
    private static final HashMap<Player, AtomicInteger> players = new HashMap<>();

    @Getter
    private static final ClickableItem boost = ClickableItem.of(ItemUtil.generate(Material.FEATHER, 1, "&aДвойной прыжок"), p -> {
        if (!p.getAllowFlight()) return;
        if (players.get(p).decrementAndGet() <= 0) return;
        p.setVelocity(p.getVelocity().multiply(1.4).setY(.8));
        p.playSound(p, Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
        p.setAllowFlight(false);
    });

    @Getter
    private static World world;
    @Getter
    private static Location spawn;
    @Getter
    private static boolean started = false;

    @Getter
    private static boolean suppress = true;

    @Getter
    private static OfflinePlayer top3;

    private static BukkitTask task;

    @Getter
    private static boolean tournamentMode = true;

    public static void init() {
        world = Bukkit.getWorlds().get(0);
        spawn = world.getSpawnLocation();

        WorldEditStuff.load();

        ScoreboardUtils.registerUpdater(utils -> {
            var sb = utils.getSidebar();
            sb.displayName(Component.color("&c&lTNT Run"));
            if (!started) {
                utils.setLine(2, "&fОжидание игроков...");
                utils.setLine(3, "&a");
                utils.setLine(4, "&b");
                return;
            }
            utils.setLine(2, "&fИгроков: &a" + players.size());
            utils.setLine(3, "&a");
            utils.setLine(4, "&fДвойных прыжков: &a" + players.get(utils.getPlayer())
                    .updateAndGet(i -> Math.max(i, 0)));
        });

        BukkitUtils.getPlugin().saveDefaultConfig();
        tournamentMode = BukkitUtils.getPlugin().getConfig().getBoolean("tournament-mode", false);
    }

    public static void start() {
        if (started) return;
        players.clear();
        started = true;
        suppress = true;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (DatabaseAdapter.getUser(player).isAdmin()) {
                player.setGameMode(GameMode.SPECTATOR);
                continue;
            }
            players.put(player, new AtomicInteger(5));
            player.teleport(spawn.clone().add(0, -5, 0));
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setHealth(20d);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getInventory().addItem(boost.getItemStack());
        }

        if (players.size() < 3) {
            Bukkit.broadcast(Component.color("&c&lНедостаточно игроков для начала игры!"));
            end();
            return;
        }

        Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), () -> suppress = false, 5 * 20L);
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!started) {
                    cancel();
                    return;
                }

                if (tournamentMode) {
                    for (Player player : players.keySet()) {
                         DatabaseAdapter.getClan(player).givePoints(5);
//                        player.sendMessage(Component.color("Здесь должно было быть +5 поинтов, " +
//                                "но долбоеб на заказчике сказал убрать чтобы убрать \"дизбаланс\", " +
//                                "но он не понимает, что создает дизбаланс как раз убирая это, " +
//                                "потому что это наоборот добавляло баланса и шансов " +
//                                "победить."));
                    }
                }
            }
        }.runTaskTimer(BukkitUtils.getPlugin(), 15 * 20L, 15 * 20L);
    }

    public static void eliminate(Player p) {
        players.remove(p);
        p.setGameMode(GameMode.SPECTATOR);
        p.teleport(spawn);
        p.playSound(p, Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
        Bukkit.broadcast(Component.color("&fИгрок &c" + p.getName() + " &fвыбыл"));
        if (players.size() == 2) {
            top3 = p;
        }
        if (players.size() == 1) {
            Bukkit.broadcast(Component.center("&9&l&n================================"));
            Bukkit.broadcast(Component.center("&f"));

            suppress = true;
            Player winner = players.keySet().iterator().next();
            Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), Game::end, 5 * 20L);

            var clan1 = DatabaseAdapter.getClan(winner);
            if (clan1 != null) {
                if (tournamentMode)
                    clan1.givePoints(40);
                Bukkit.broadcast(Component.color("&c&l1. &b[&f" + clan1.getTag() + "&b] &a" + winner.getName() + " &6+40"));
            }

            var clan2 = DatabaseAdapter.getClan(p);
            if (clan2 != null) {
                if (tournamentMode)
                    clan2.givePoints(15);
                Bukkit.broadcast(Component.color("&c&l2. &b[&f" + clan2.getTag() + "&b] &a" + p.getName() + " &6+15"));
            }

            var clan3 = DatabaseAdapter.getClan(top3);
            if (clan3 != null) {
                if (tournamentMode)
                    clan3.givePoints(10);
                Bukkit.broadcast(Component.color("&c&l3. &b[&f" + clan3.getTag() + "&b] &a" + top3.getName() + " &6+10"));
            }

            Bukkit.broadcast(Component.center("&f"));
            Bukkit.broadcast(Component.center("&9&l&n================================"));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                player.showTitle(Title.title(Component.color("&b[" + (clan1 != null ? clan1.getTag() : "noname") + "]"),
                        Component.color("&a" + winner.getName() + " &fпринёс победу клану")));
            }
        }
    }

    public static void end() {
        task.cancel();
        started = false;
        WorldEditStuff.paste();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.teleport(spawn);
            player.getInventory().clear();
            if (tournamentMode && player.isOp()) {
                player.getInventory().addItem(Game.getItem().getItemStack());
            }
        }

        Bukkit.getScheduler().runTaskLater(BukkitUtils.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.isOp()) continue;
                onlinePlayer.kick(Component.color("&cИгра окончена"));
            }
        }, 20L * 5);
    }

    public static boolean isPlayer(Player p) {
        return players.containsKey(p);
    }

    public static boolean isSpectator(Player p) {
        return !isPlayer(p);
    }

}

package net.thisisnico.lolz.tntrun;

import lombok.Getter;
import net.thisisnico.lolz.bukkit.utils.ClickableItem;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.bukkit.utils.ItemUtil;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class Game {

    @Getter
    private static final ClickableItem item = ClickableItem.of(ItemUtil.generate(Material.DIAMOND, 1,
            "&a&lTNT Run", "&7Click to start the game!"), p -> start());

    @Getter
    private static final ClickableItem boost = ClickableItem.of(ItemUtil.generate(Material.FEATHER, 1, "&aДвойной прыжок"), p -> {
        if (!p.getAllowFlight()) return;
        p.setVelocity(p.getVelocity().multiply(1.4).setY(.8));
        p.playSound(p, Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
        p.setAllowFlight(false);
    });

    @Getter
    private static final HashSet<Player> players = new HashSet<>();
    @Getter
    private static World world;
    @Getter
    private static Location spawn;
    @Getter
    private static boolean started = false;

    @Getter
    private static boolean suppress = true;

    public static void init() {
        world = Bukkit.getWorlds().get(0);
        spawn = world.getSpawnLocation();
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
            players.add(player);
            player.teleport(spawn);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setHealth(20d);
            player.setFoodLevel(20);
            player.getInventory().addItem(boost.getItemStack());
        }

        Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), () -> suppress = false, 5 * 20L);
    }

    public static void eliminate(Player p) {
        players.remove(p);
        p.setGameMode(GameMode.SPECTATOR);
        p.teleport(spawn);
        if (players.size() == 1) {
            suppress = true;
            Player winner = players.iterator().next();
            Bukkit.broadcast(Component.color("&aПобедил " + winner.getName()));
            Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), Game::end, 5 * 20L);
        }
    }

    public static void end() {
        started = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.teleport(spawn);
            player.getInventory().remove(Material.FEATHER);
        }
    }

    public static boolean isPlayer(Player p) {
        return players.contains(p);
    }

    public static boolean isSpectator(Player p) {
        return !isPlayer(p);
    }

}

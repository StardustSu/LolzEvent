package net.thisisnico.lolz.buildbattle.listeners;

import net.thisisnico.lolz.buildbattle.Game;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class StartHandler implements Listener {

    private static int countdown = 0;
    private static int playersCount = 0;

    private static final int MAX_PLAYERS = 16;
    private static final int MIN_PLAYERS = 6;

    private static final BukkitRunnable COUNTDOWN = new BukkitRunnable() {
        @Override
        public void run() {
            if (Game.isTournamentMode())
                cancel();

            if (countdown == 0) {
                Game.start();
                cancel();
            } else {
                countdown--;
                COUNTDOWN.runTaskLater(BukkitUtils.getPlugin(), 20);
            }
        }
    };

    public static void startCountdown() {
        if (Game.isTournamentMode())
            return;

        countdown = 30;
        COUNTDOWN.run();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (Game.isTournamentMode())
            return;

        if (playersCount >= MAX_PLAYERS) {
            e.getPlayer().kick(Component.color("§cИгра уже заполнена!"));
        }

        playersCount++;

        if (playersCount >= MIN_PLAYERS) {
            startCountdown();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (Game.isTournamentMode())
            return;

        playersCount--;

        if (playersCount < MIN_PLAYERS) {
            COUNTDOWN.cancel();
        }
    }
}
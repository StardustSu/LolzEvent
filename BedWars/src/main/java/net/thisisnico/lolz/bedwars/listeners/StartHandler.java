package net.thisisnico.lolz.bedwars.listeners;

import net.thisisnico.lolz.bedwars.Game;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class StartHandler {
    private static int countdown = 0;
    private static int teamsCount = 0;
    private static int playersCount = 0;

    private static final int MAX_PLAYERS = 32;
    private static final int MAX_TEAMS = 8;
    private static final int MIN_PLAYERS = 8;

    private static final BukkitRunnable COUNTDOWN = new BukkitRunnable() {
        @Override
        public void run() {
            if(Game.tournamentMode)
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
        if(Game.tournamentMode)
            return;

        countdown = 60;
        COUNTDOWN.run();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(Game.tournamentMode)
            return;

        if (countdown == 0) {
            e.getPlayer().sendMessage("§cThe game is already starting!");
            e.getPlayer().kickPlayer("§cThe game is already starting!");
        }

        if (playersCount >= MAX_PLAYERS) {
            e.getPlayer().sendMessage("§cThe game is full!");
            e.getPlayer().kickPlayer("§cThe game is full!");
        }

        if(teamsCount > MAX_TEAMS){
            e.getPlayer().sendMessage("§cThe game is full!");
            e.getPlayer().kickPlayer("§cThe game is full!");
        }
        else if(teamsCount == MAX_TEAMS){
            // TODO: Check if the team is full
        }

        playersCount++;

        if (playersCount >= MIN_PLAYERS) {
            startCountdown();
        }

        e.getPlayer().sendMessage("§aYou joined the game! §7(" + playersCount + "/" + MAX_PLAYERS + ")");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(Game.tournamentMode)
            return;

        if (countdown == 0) {
            e.getPlayer().sendMessage("§cThe game is already starting!");
            e.getPlayer().kickPlayer("§cThe game is already starting!");
        }

        playersCount--;

        // TODO: Check if the team is empty
        // TODO: Decrease the teamsCount if the team is not empty

        if (playersCount < MIN_PLAYERS) {
            countdown = 60;
            COUNTDOWN.cancel();
        }
    }
}

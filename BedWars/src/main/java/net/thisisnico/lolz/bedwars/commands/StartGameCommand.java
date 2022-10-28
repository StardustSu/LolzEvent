package net.thisisnico.lolz.bedwars.commands;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.thisisnico.lolz.bedwars.Game;
import org.bukkit.entity.Player;

public class StartGameCommand {

    @CommandMethod("game start")
    @CommandPermission("op")
    public void startGame(final Player p) {
        Game.startTimer();
    }

}

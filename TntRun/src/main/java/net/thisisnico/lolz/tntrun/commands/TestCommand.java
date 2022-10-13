package net.thisisnico.lolz.tntrun.commands;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.thisisnico.lolz.tntrun.Game;
import org.bukkit.entity.Player;

public class TestCommand {

    @CommandMethod("start")
    @CommandPermission("op")
    public void start(final Player p) {
        Game.start();
    }

}

package net.thisisnico.lolz.bukkit.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.thisisnico.lolz.bukkit.menu.WarpMenu;
import org.bukkit.entity.Player;

public class WarpCommand {

    @CommandMethod("warp <count>")
    @CommandPermission("op")
    public void warp(final Player p, final @Argument("count") int count) {
        new WarpMenu(p, count);
    }

}

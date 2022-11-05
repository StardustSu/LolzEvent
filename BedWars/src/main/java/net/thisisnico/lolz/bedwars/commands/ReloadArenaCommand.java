package net.thisisnico.lolz.bedwars.commands;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.thisisnico.lolz.bedwars.Game;
import net.thisisnico.lolz.bukkit.utils.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReloadArenaCommand {

    @CommandMethod("arena reload")
    @CommandPermission("op")
    public void onReload(final Player player) {
        Game.init();
        Bukkit.broadcast(Component.color("&aArena has been reloaded by administrator!"));
    }

}

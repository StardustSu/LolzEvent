package net.thisisnico.lolz.bukkit.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.thisisnico.lolz.common.network.Sync;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.entity.Player;

public class DebugCommand {

    @CommandMethod("do_not_touch_debug points <player> <delta>")
    @CommandPermission("op")
    public void debugCoins(final Player admin, final @Argument("player") Player player, final @Argument("delta") int delta) {
        var clan = DatabaseAdapter.getClan(player);
        if (clan == null) {
            admin.sendMessage("§cThis player is not in a clan!");
            return;
        }

        clan.setPoints(clan.getPoints() + delta);
        clan.save();
        Sync.sendPointsUpdate(clan, delta);

        admin.sendMessage("§aUpdated points of §e" + clan.getTag() + "§a by §e" + delta);
    }

}

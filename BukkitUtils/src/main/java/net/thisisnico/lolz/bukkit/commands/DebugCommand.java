package net.thisisnico.lolz.bukkit.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.thisisnico.lolz.bukkit.BungeeUtils;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import net.thisisnico.lolz.common.network.Sync;
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

    @CommandMethod("legacy_server <name>")
    public void legacyServer(final Player player, final @Argument("name") String name) {
        BungeeUtils.sendPlayerToServer(player, name);
    }

}

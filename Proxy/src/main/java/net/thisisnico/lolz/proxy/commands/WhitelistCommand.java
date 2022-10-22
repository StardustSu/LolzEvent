package net.thisisnico.lolz.proxy.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.velocitypowered.api.proxy.Player;
import net.thisisnico.lolz.proxy.Proxy;

import static net.kyori.adventure.text.Component.text;

public class WhitelistCommand {

    @CommandMethod("whitelist add <player>")
    @CommandPermission("lolz.admin")
    public void addPlayer(final Player admin, final @Argument("player") String player) {
        if (Proxy.getWhitelist().contains(player)) {
            admin.sendMessage(text("§cThis player is already whitelisted!"));
            return;
        }

        Proxy.getWhitelist().add(player);
        admin.sendMessage(text("§aAdded §e" + player + "§a to the whitelist!"));
    }

    @CommandMethod("whitelist remove <player>")
    @CommandPermission("lolz.admin")
    public void removePlayer(final Player admin, final @Argument("player") String player) {
        if (!Proxy.getWhitelist().contains(player)) {
            admin.sendMessage(text("§cThis player is not whitelisted!"));
            return;
        }

        Proxy.getWhitelist().remove(player);
        admin.sendMessage(text("§aRemoved §e" + player + "§a from the whitelist!"));
    }

}

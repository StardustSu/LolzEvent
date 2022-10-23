package net.thisisnico.lolz.proxy.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.event.ClickEvent;
import net.thisisnico.lolz.common.adapters.ProxyDatabaseAdapter;
import net.thisisnico.lolz.common.database.Clan;
import net.thisisnico.lolz.common.database.User;
import net.thisisnico.lolz.proxy.Proxy;

import static net.kyori.adventure.text.Component.text;

public class ClanCommand {

    @CommandMethod("clan create <tag>")
    @CommandPermission("lolz.head")
    public void createClan(final Player player, final @Argument("tag") String tag) {
        var user = ProxyDatabaseAdapter.getUser(player);
        if (user.hasClan()) {
            player.sendMessage(text("§c● §fТы уже состоишь в клане!"));
            return;
        }

        if (tag.length() > 10 || tag.length() < 3) {
            player.sendMessage(text("§c● §fТэг клана должен быть от 3 до 10 символов !"));
            return;
        }

        if (Clan.get(tag) != null) {
            player.sendMessage(text("§c● §fКлан с таким тегом уже существует!"));
            return;
        }

        var clan = ProxyDatabaseAdapter.createClan(tag, player);
        user.setClan(tag);
        user.save();
        player.sendMessage(text("§a● §fТы создал клан §e" + clan.getTag() + "§f!"));
    }

    @CommandMethod("clan invite <player>")
    public void invitePlayer(final Player player, final @Argument("player") Player target) {
        var user = ProxyDatabaseAdapter.getUser(player);
        if (!user.hasClan()) {
            player.sendMessage(text("§c● §fТы не состоишь в клане!"));
            return;
        }

        var clan = ProxyDatabaseAdapter.getClan(player);
        if (!clan.getOwner().equals(user.getName())) {
            player.sendMessage(text("§c● §fТы не лидер клана!"));
            return;
        }

        if (clan.getMembers().size() >= 4) {
            player.sendMessage(text("§c● §fВ клане уже 4 участника!"));
            return;
        }

        var targetUser = ProxyDatabaseAdapter.getUser(target);
        if (targetUser.hasClan()) {
            player.sendMessage(text("§c● §fЭтот игрок уже состоит в клане!"));
            return;
        }

        clan.getInvites().add(targetUser.getName());
        clan.save();
        player.sendMessage(text("§a● §fТы пригласил §e" + targetUser.getName() + "§f в клан!"));

        target.sendMessage(text("§6●"));
        target.sendMessage(text("§6● §fТы был приглашен в клан §e" + clan.getTag() + "§f!"));
        target.sendMessage(text("§6● §a[ПРИНЯТЬ ПРИГЛАШЕНИЕ] §6●").clickEvent(ClickEvent.runCommand("/clan join " + clan.getTag())));

        Proxy.getInstance().getServer().getScheduler().buildTask(Proxy.getInstance(), () -> {
            if (clan.getInvites().contains(targetUser.getName())) {
                if (clan.getMembers().contains(targetUser.getName())) return;
                clan.getInvites().remove(targetUser.getName());
                clan.save();

                player.sendMessage(text("§c● §fПриглашение игрока §e" + targetUser.getName() + "§f истекло!"));
                target.sendMessage(text("§c● §fПриглашение в клан §e" + clan.getTag() + "§f истекло!"));
            }
        }).delay(30, java.util.concurrent.TimeUnit.SECONDS).schedule();
    }

    @CommandMethod("clan join <tag>")
    public void joinClan(final Player player, final @Argument("tag") String tag) {
        var user = ProxyDatabaseAdapter.getUser(player);
        if (user.hasClan()) {
            player.sendMessage(text("§c● §fТы уже состоишь в клане!"));
            return;
        }

        var clan = Clan.get(tag);
        if (clan == null) {
            player.sendMessage(text("§c● §fКлан с таким тегом не существует!"));
            return;
        }

        if (!clan.getInvites().contains(user.getName())) {
            player.sendMessage(text("§c● §fТы не приглашен в этот клан!"));
            return;
        }

        clan.getInvites().remove(user.getName());
        clan.getMembers().add(user.getName());
        clan.save();

        user.setClan(clan.getTag());
        user.save();

        player.sendMessage(text("§a● §fТы присоединился к клану §e" + clan.getTag() + "§f!"));

        // tell all clan members that a person joined
        clan.getMembers().forEach(member -> {
            var memberPlayer = Proxy.getInstance().getServer().getPlayer(member);
            memberPlayer.ifPresent(value -> value.sendMessage(text("§a● §fИгрок §e" + user.getName() + "§f присоединился к клану!")));
        });
    }

    @CommandMethod("clan leave")
    public void leaveClan(final Player player) {
        var user = ProxyDatabaseAdapter.getUser(player);
        if (!user.hasClan()) {
            player.sendMessage(text("§c● §fТы не состоишь в клане!"));
            return;
        }

        var clan = ProxyDatabaseAdapter.getClan(player);
        if (clan.getOwner().equals(user.getName())) {
            player.sendMessage(text("§c● §fТы не можешь покинуть клан, т.к. ты его лидер!"));
            return;
        }

        clan.getMembers().remove(user.getName());
        clan.save();

        user.setClan(null);
        user.save();

        player.sendMessage(text("§a● §fТы покинул клан §e" + clan.getTag() + "§f!"));

        // tell all clan members that a person left
        clan.getMembers().forEach(member -> {
            var memberPlayer = Proxy.getInstance().getServer().getPlayer(member);
            memberPlayer.ifPresent(value -> value.sendMessage(text("§a● §fИгрок §e" + user.getName() + "§f покинул клан!")));
        });
    }

    @CommandMethod("clan kick <player>")
    public void kickClan(final Player player, final @Argument("player") String target) {
        var user = ProxyDatabaseAdapter.getUser(player);
        if (!user.hasClan()) {
            player.sendMessage(text("§c● §fТы не состоишь в клане!"));
            return;
        }

        var clan = ProxyDatabaseAdapter.getClan(player);
        if (!clan.getOwner().equals(user.getName())) {
            player.sendMessage(text("§c● §fТы не можешь исключить игрока из клана, т.к. ты не его лидер!"));
            return;
        }

        var targetUser = User.get(target);
        if (!targetUser.hasClan()) {
            player.sendMessage(text("§c● §fЭтот игрок не состоит в клане!"));
            return;
        }

        if (!targetUser.getClan().equals(clan.getTag())) {
            player.sendMessage(text("§c● §fЭтот игрок не состоит в твоем клане!"));
            return;
        }

        clan.getMembers().remove(targetUser.getName());
        clan.save();

        targetUser.setClan(null);
        targetUser.save();

        player.sendMessage(text("§a● §fТы исключил игрока §e" + targetUser.getName() + "§f из клана!"));

        // tell all clan members that a person left
        clan.getMembers().forEach(member -> {
            var memberPlayer = Proxy.getInstance().getServer().getPlayer(member);
            memberPlayer.ifPresent(value -> value.sendMessage(text("§a● §fИгрок §e" + targetUser.getName() + "§f был исключен из клана!")));
        });
    }

}

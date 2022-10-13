package net.thisisnico.lolz.common.adapters;

import net.thisisnico.lolz.common.database.Clan;
import net.thisisnico.lolz.common.database.User;
import org.bukkit.OfflinePlayer;

public class DatabaseAdapter {

    public static User getUser(OfflinePlayer player) {
        return User.get(player.getName());
    }

    public static Clan getClan(OfflinePlayer player) {
        return Clan.get(getUser(player).getClan());
    }

    public static Clan getClan(String tag) {
        return Clan.get(tag);
    }

    public static Clan createClan(String tag, OfflinePlayer player) {
        var user = getUser(player);
        user.setClan(tag);
        user.save();
        return Clan.create(tag, player.getName());
    }

}

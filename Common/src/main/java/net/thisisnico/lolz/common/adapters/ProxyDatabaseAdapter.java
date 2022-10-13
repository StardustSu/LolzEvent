package net.thisisnico.lolz.common.adapters;

import com.velocitypowered.api.proxy.Player;
import net.thisisnico.lolz.common.database.Clan;
import net.thisisnico.lolz.common.database.User;

public class ProxyDatabaseAdapter {

    public static User getUser(Player player) {
        return User.get(player.getUsername());
    }

    public static Clan getClan(Player player) {
        return Clan.get(getUser(player).getClan());
    }

    public static Clan createClan(String tag, Player player) {
        var user = getUser(player);
        user.setClan(tag);
        user.save();
        return Clan.create(tag, player.getUsername());
    }

}

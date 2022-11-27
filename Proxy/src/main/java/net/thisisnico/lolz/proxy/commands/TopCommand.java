package net.thisisnico.lolz.proxy.commands;

import cloud.commandframework.annotations.CommandMethod;
import com.mongodb.client.model.Sorts;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.thisisnico.lolz.common.database.Database;

import java.util.ArrayList;

public class TopCommand {

    @CommandMethod("top")
    public void onCommand(final Player player) {
        final int[] i = {0};
        var clans = Database.getClans().find().sort(Sorts.descending("points")).limit(10)
                .map(clan -> {
                    i[0]++;
                    return "&f&l" + i[0] + ". &b" + clan.getTag() + " &d" + clan.getPoints() + " поинтов";
                }).into(new ArrayList<>());

        for (String clan : clans) {
            player.sendMessage(Component.text(clan.replaceAll("&", "§")));
        }
    }

}

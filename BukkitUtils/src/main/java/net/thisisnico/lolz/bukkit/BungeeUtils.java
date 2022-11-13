package net.thisisnico.lolz.bukkit;

import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

public class BungeeUtils {

    @SuppressWarnings("UnstableApiUsage")
    public static void sendPlayerToServer(Player player, String server) {
        var packet = ByteStreams.newDataOutput();
        packet.writeUTF("Connect");
        packet.writeUTF(server);
        player.sendPluginMessage(BukkitUtils.getPlugin(), "BungeeCord", packet.toByteArray());
    }

}

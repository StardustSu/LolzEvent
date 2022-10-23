package net.thisisnico.lolz.bedwars;

import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.bukkit.utils.ScoreboardUtils;
import org.bukkit.entity.Player;

public class README {

    /**
     * Эта хуйня написана специально для еблана с ником deshik91
     */

    @SuppressWarnings("all")
    public void a(Player player) {

        // Get plugin instance:
        BukkitUtils.getPlugin();

        // Scoreboard Utils:
        var sb = ScoreboardUtils.get(player);

        // Usage:
        sb.setLine(1, "&aGovno");
        sb.getSidebar().displayName(Component.color("&b&l [ TITLE ]"));

        // xz che eshe

    }

}

package net.thisisnico.lolz.lobby;

import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.BungeeUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.bukkit.utils.InventoryMenu;
import net.thisisnico.lolz.bukkit.utils.ItemUtil;
import net.thisisnico.lolz.bukkit.utils.ScoreboardUtils;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lobby extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic

        BukkitUtils.instantiate(this);
        BukkitUtils.registerListener(this);

        ScoreboardUtils.registerUpdater(sb -> {
            var clan = DatabaseAdapter.getClan(sb.getPlayer());
            sb.getSidebar().displayName(Component.color("&f&lСка&1&lмим&c&l.РФ"));
            sb.setLine(1, "&1");
            sb.setLine(2, "&fИгроков: &e&l" + getServer().getOnlinePlayers().size());
            sb.setLine(3, "&fКлан: &e" + (clan != null ? clan.getTag() : "&cнет"));
            sb.setLine(4, "&4");
        });

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (var player : Bukkit.getOnlinePlayers()) {
                var clan = DatabaseAdapter.getClan(player);

                for (var p1 : getServer().getOnlinePlayers()) {
                    var sb = ScoreboardUtils.get(p1).getScoreboard();
                    var team = sb.getTeam(clan.getTag());
                    if (team != null) {
                        team.suffix(Component.color(" &b["+clan.getTag()+"]"));
                    }
                }
            }
        }, 0, 3*20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    void onMove(PlayerMoveEvent e) {
        if (!e.hasChangedBlock()) return;
        var portal = e.getTo().getBlock().getType() == Material.NETHER_PORTAL;
        if (!portal) return;

        for (ArmorStand armorStand : e.getTo().getNearbyEntitiesByType(ArmorStand.class, 2)) {
            if (armorStand.getScoreboardTags().contains("tnt")) {
                new TeleportMenu(e.getPlayer(), "tnt");
                return;
            }
            if (armorStand.getScoreboardTags().contains("bw")) {
                new TeleportMenu(e.getPlayer(), "bw");
                return;
            }
            if (armorStand.getScoreboardTags().contains("bb")) {
                new TeleportMenu(e.getPlayer(), "bb");
                return;
            }
        }
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        ScoreboardUtils.get(e.getPlayer());

        // get clan
        var clan = DatabaseAdapter.getClan(e.getPlayer());

        // for each player scoreboard
        for (var player : getServer().getOnlinePlayers()) {
            var sb = ScoreboardUtils.get(player).getScoreboard();
            // get team, if it doesn't exist create
            var team = sb.getTeam(clan.getTag());
            if (team == null) {
                team = sb.registerNewTeam(clan.getTag());
                team.suffix(Component.color(" &b["+clan.getTag()+"]"));
            }
            // add player to team
            team.addEntry(e.getPlayer().getName());
        }

        e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation().clone().add(.5, 0, .5));
    }

    @EventHandler
    void onLeave(PlayerQuitEvent e) {
        // get clan
        var clan = DatabaseAdapter.getClan(e.getPlayer());

        // for each player scoreboard
        for (var player : getServer().getOnlinePlayers()) {
            var sb = ScoreboardUtils.get(player).getScoreboard();
            // get team, if it doesn't exist create
            var team = sb.getTeam(clan.getTag());
            if (team == null) {
                team = sb.registerNewTeam(clan.getTag());
                team.suffix(Component.color(" &b["+clan.getTag()+"]"));
            }
            // remove player from team
            team.removeEntry(e.getPlayer().getName());

            // if team is empty, unregister
            if (team.getSize() == 0) {
                team.unregister();
            }
        }
    }
    private static class TeleportMenu extends InventoryMenu {
        protected TeleportMenu(Player p, String mg) {
            super("Телепорт", 3, true);

            setItem(12, ItemUtil.generate(Material.BARRIER, 1, "&eАрена 1", "&7Нажмите чтобы перейти на арену"), _p -> {
                p.closeInventory();
                BungeeUtils.sendPlayerToServer(p, mg+"1");
            });

            setItem(14, ItemUtil.generate(Material.BARRIER, 1, "&eАрена 2", "&7Нажмите чтобы перейти на арену"), _p -> {
                p.closeInventory();
                BungeeUtils.sendPlayerToServer(p, mg+"2");
            });

            open(p);
        }
    }

}

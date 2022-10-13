package net.thisisnico.lolz.bukkit.utils;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;

public class ClickableItem {
    @Getter
    private static final HashMap<ItemStack, ClickableItem> items = new HashMap<>();

    @Getter
    private final ItemStack itemStack;
    @Getter
    private final Consumer<Player> consumer;

    public static ClickableItem of(ItemStack is, Consumer<Player> c) {
        return new ClickableItem(is, c);
    }

    private ClickableItem(ItemStack itemStack, Consumer<Player> consumer) {
        this.itemStack = itemStack;
        this.consumer = consumer;
        items.put(itemStack, this);
    }

}

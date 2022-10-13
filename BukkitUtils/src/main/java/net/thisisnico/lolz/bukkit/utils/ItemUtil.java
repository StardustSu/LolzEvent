package net.thisisnico.lolz.bukkit.utils;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtil {

    public static ItemStack generate(Material mat, int count, String name, String... lore) {
        var is = new ItemStack(mat, count);
        var meta = is.getItemMeta();
        meta.displayName(Component.color(name));
        meta.lore(Arrays.stream(lore)
                .map(Component::color).collect(Collectors.toCollection(ArrayList::new)));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack generate(Material mat, int count, String name, List<String> lore) {
        var is = new ItemStack(mat, count);
        var meta = is.getItemMeta();
        meta.displayName(Component.color(name));
        meta.lore(lore.stream()
                .map(Component::color).collect(Collectors.toCollection(ArrayList::new)));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack generate(Material mat, int count, boolean unbreakable, String name, String... lore) {
        var is = new ItemStack(mat, count);
        var meta = is.getItemMeta();
        meta.setUnbreakable(unbreakable);
        meta.displayName(Component.color(name));
        meta.lore(Arrays.stream(lore).map(Component::color).collect(Collectors.toCollection(ArrayList::new)));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack generate(Material mat, int count, boolean unbreakable, String name, Enchantment ench, int lvl, String... lore) {
        var is = new ItemStack(mat, count);
        var meta = is.getItemMeta();
        meta.setUnbreakable(unbreakable);
        meta.displayName(Component.color(name));
        meta.addEnchant(ench, lvl, true);
        meta.lore(Arrays.stream(lore).map(Component::color).collect(Collectors.toCollection(ArrayList::new)));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack addLore(ItemStack is, String... lore) {
        var i = is.clone();
        var meta = i.getItemMeta();
        meta.lore(Arrays.stream(lore).map(Component::color).collect(Collectors.toCollection(ArrayList::new)));
        i.clone().setItemMeta(meta);
        return i;
    }

    public static ItemStack enchantInvisible(ItemStack is) {
        var i = is.clone();
        var meta = i.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack addLoreLine(ItemStack is, String lore) {
        var i = is.clone();
        var meta = i.getItemMeta();
        var l = meta.lore();
        if (l == null) l = new ArrayList<>();
        l.add(Component.color(lore));
        meta.lore(l);
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack getPlayerHead(OfflinePlayer p) {
        var is = new ItemStack(Material.PLAYER_HEAD);
        var meta = (SkullMeta) is.getItemMeta();
        meta.displayName(Component.color("&e" + p.getName()));
        meta.setOwningPlayer(p);
        is.setItemMeta(meta);
        return is;
    }

    public static void clear(Player p, Material mat) {
        p.getInventory().remove(mat);
        if (p.getInventory().getItemInOffHand().getType() == mat) {
            p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    public static int getRows(int size) {
        if (size <= 9) return 1;
        if (size <= 18) return 2;
        if (size <= 27) return 3;
        if (size <= 36) return 4;
        if (size <= 45) return 5;
        return 6;
    }

    @SuppressWarnings("deprecation")
    public static boolean isSimilar(ItemStack is1, ItemStack is2) {
        return is1.getType() == is2.getType()
                && is1.hasItemMeta()
                && is2.hasItemMeta()
                && is1.getItemMeta().hasDisplayName()
                && is2.getItemMeta().hasDisplayName()
                && is1.getItemMeta().getDisplayName().equalsIgnoreCase(is2.getItemMeta().getDisplayName());
    }

}

package net.thisisnico.lolz.bedwars.menu;

import net.thisisnico.lolz.bukkit.utils.InventoryMenu;
import net.thisisnico.lolz.bukkit.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShopMenu extends InventoryMenu {
    public ShopMenu(Player p) {
        super("", 1, true);

        addItem(ItemUtil.generate(Material.DIAMOND_BLOCK, 1, "&fgovno"), _p -> {
            // executor
        });

        open(p);
    }
}

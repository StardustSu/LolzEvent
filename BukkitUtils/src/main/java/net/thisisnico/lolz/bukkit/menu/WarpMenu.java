package net.thisisnico.lolz.bukkit.menu;

import net.thisisnico.lolz.bukkit.utils.InventoryMenu;
import net.thisisnico.lolz.bukkit.utils.ItemUtil;
import net.thisisnico.lolz.common.database.Clan;
import net.thisisnico.lolz.common.database.Database;
import net.thisisnico.lolz.common.network.Sync;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WarpMenu extends InventoryMenu {
    public WarpMenu(final Player p, final int count) {
        super("Warp Menu", 6, true);

        var clans = Database.getClans().find();
        for (Clan clan : clans) {
            var item = ItemUtil.generate(Material.DIAMOND_BLOCK, 1, "&a"+clan.getTag(), clan.getMembers());
            this.addItem(item, _p -> Sync.sendClanRequest(_p.getName(), clan, count));
        }

        open(p);
    }
}

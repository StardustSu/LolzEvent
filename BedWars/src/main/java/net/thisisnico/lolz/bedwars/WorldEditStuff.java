package net.thisisnico.lolz.bedwars;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import net.thisisnico.lolz.bukkit.utils.Component;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WorldEditStuff {

    public static void load(String name) {
        Clipboard tmp;
        var dir = BukkitUtils.getPlugin().getDataFolder();
        dir.mkdirs();

        var file = new File(dir, name+".schem");
        if (!file.exists()) {
            Bukkit.broadcast(Component.color("&cФайл арены не найден"), "op");
            return;
        }

        try (ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(file))) {
            tmp = reader.read();
        } catch (IOException e) {
            Bukkit.broadcast(Component.color("&cError downloading schematic from db!"), "op");
            return;
        }

        paste(tmp);
    }

    public static void paste(Clipboard tmp) {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(
                BukkitAdapter.adapt(Game.getArena().getWorld()))) {
            Operation operation = new ClipboardHolder(tmp)
                    .createPaste(editSession)
                    .to(BlockVector3.at(0, 0, 0))
                    .build();
            try {
                Operations.complete(operation);
            } catch (WorldEditException e) {
                Bukkit.broadcast(Component.color("&cПроизошла ошибка вставления чанка (чо?)"),
                        "op");
            }
        }
    }

}

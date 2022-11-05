package net.thisisnico.lolz.buildbattle;

import lombok.Getter;
import net.thisisnico.lolz.bukkit.BukkitUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ThemeProvider {

    private static ThemeProvider instance;

    public static ThemeProvider getInstance() {
        if (instance == null) {
            instance = new ThemeProvider(BukkitUtils.getPlugin());
        }
        return instance;
    }

    private final JavaPlugin plugin;
    private final File dir;
    private final File themesFile;

    @Getter
    private final ArrayList<String> themes = new ArrayList<>();

    private ThemeProvider(JavaPlugin plugin) {
        this.plugin = plugin;
        dir = plugin.getDataFolder();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        themesFile = new File(dir, "themes.txt");
        if (!themesFile.exists()) {
            plugin.saveResource("themes.txt", false);
        }
        loadThemes();
    }

    private void loadThemes() {
        if (!themesFile.exists()) return;
        themes.clear();
        try (var stream = new BufferedReader(new FileReader(themesFile))) {
            stream.lines().forEach(themes::add);
        } catch (Exception e) {
            e.printStackTrace();
        }
        plugin.getLogger().info("Loaded " + themes.size() + " themes");
    }

    public String getRandomTheme() {
        return themes.get((int) (Math.random() * (themes.size()-1)));
    }

}

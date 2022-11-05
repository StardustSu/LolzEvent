package net.thisisnico.lolz.buildbattle;

public class ThemeProvider {

    private static ThemeProvider instance;

    public static ThemeProvider getInstance() {
        if (instance == null) {
            instance = new ThemeProvider();
        }
        return instance;
    }

    private ThemeProvider() {

    }

}

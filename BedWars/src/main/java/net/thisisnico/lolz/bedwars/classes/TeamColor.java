package net.thisisnico.lolz.bedwars.classes;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Random;

public enum TeamColor {
    RED    (NamedTextColor.RED         , 'c'),
    BLUE   (NamedTextColor.BLUE        , '9'),
    GREEN  (NamedTextColor.GREEN       , 'a'),
    YELLOW (NamedTextColor.YELLOW      , 'e'),
    AQUA   (NamedTextColor.AQUA        , 'b'),
    PINK   (NamedTextColor.LIGHT_PURPLE, 'd'),
    WHITE  (NamedTextColor.WHITE       , 'f'),
    GRAY   (NamedTextColor.DARK_GRAY   , '8');

    private static final ArrayList<TeamColor> takenColors = new ArrayList<>();
    private static final Random random = new Random();

    @Getter
    private final NamedTextColor color;

    @Getter
    private final char code;

    TeamColor(NamedTextColor color, char code) {
        this.color = color;
        this.code = code;
    }

    public static TeamColor getRandomColor() {
        TeamColor color;
        do {
            color = values()[random.nextInt(values().length)];
        } while (takenColors.contains(color));
        takenColors.add(color);
        return color;
    }

    public static void clearTakenColors() {
        takenColors.clear();
    }

}

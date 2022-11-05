package net.thisisnico.lolz.bedwars.classes;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Random;

public enum TeamColor {
    RED    (NamedTextColor.RED         ),
    BLUE   (NamedTextColor.BLUE        ),
    GREEN  (NamedTextColor.GREEN       ),
    YELLOW (NamedTextColor.YELLOW      ),
    AQUA   (NamedTextColor.AQUA        ),
    PINK   (NamedTextColor.LIGHT_PURPLE),
    WHITE  (NamedTextColor.WHITE       ),
    GRAY   (NamedTextColor.DARK_GRAY   );

    private static final ArrayList<TeamColor> takenColors = new ArrayList<>();
    private static final Random random = new Random();

    @Getter
    private final NamedTextColor color;

    TeamColor(NamedTextColor color) {
        this.color = color;
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

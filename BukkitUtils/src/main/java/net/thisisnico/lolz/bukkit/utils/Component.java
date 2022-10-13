package net.thisisnico.lolz.bukkit.utils;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.thisisnico.lolz.bukkit.classes.DefaultFontInfo;
import org.bukkit.ChatColor;

public class Component {
    private final static int CENTER_PX = 154;
    private final static PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();

    public static String parse(net.kyori.adventure.text.Component c, String d) {
        return serializer.serializeOr(c, d);
    }

    public static String parse(net.kyori.adventure.text.Component c) {
        return serializer.serialize(c);
    }

    public static net.kyori.adventure.text.Component color(String s) {
        return net.kyori.adventure.text.Component.text(
                ChatColor.translateAlternateColorCodes('&', s));
    }

    public static net.kyori.adventure.text.Component center(String text) {
        var message = ChatColor.translateAlternateColorCodes('&', text);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return net.kyori.adventure.text.Component.text(sb + message);
    }

    public static net.kyori.adventure.text.Component delimiter(String s) {
        return color(s.repeat(57));
    }

    public static net.kyori.adventure.text.Component delimiter() {
        return color("&9&l" + "=".repeat(57));
    }

}

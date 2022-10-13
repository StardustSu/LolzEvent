package net.thisisnico.lolz.bukkit.utils;

public class Formatter {

    public static String time(final int seconds) {
        var minutes = (int) Math.floor(seconds/60f);
        var sec = seconds - (minutes*60);

        var s_minutes = String.valueOf(minutes);
        var s_seconds = String.valueOf(sec);
        if (minutes < 10) s_minutes = "0"+s_minutes;
        if (sec < 10) s_seconds = "0"+s_seconds;

        return s_minutes+":"+s_seconds;
    }

}

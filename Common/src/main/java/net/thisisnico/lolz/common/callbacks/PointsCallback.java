package net.thisisnico.lolz.common.callbacks;

import net.thisisnico.lolz.common.database.Clan;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class PointsCallback {
    private static final ArrayList<BiConsumer<Clan, Integer>> addCallback = new ArrayList<>();
    private static final ArrayList<BiConsumer<Clan, Integer>> takeCallback = new ArrayList<>();

    public static void registerAdd(BiConsumer<Clan, Integer> callback) {
        addCallback.add(callback);
    }

    public static void registerTake(BiConsumer<Clan, Integer> callback) {
        takeCallback.add(callback);
    }

    public static void callAdd(Clan clan, int i) {
        addCallback.forEach(callback -> callback.accept(clan, i));
    }

    public static void callTake(Clan clan, int i) {
        takeCallback.forEach(callback -> callback.accept(clan, i));
    }
}

package net.thisisnico.lolz.common.network;

import net.thisisnico.lolz.common.database.Clan;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class Sync {

    private static final Jedis redis = new Jedis("localhost", 6379, 0);

    // ArrayList of BiConsumer<Clan, Integer>
    private static final ArrayList<BiConsumer<Clan, Integer>> points = new ArrayList<>();

    // ArrayList of BiConsumer<String, Clan>
    private static final ArrayList<BiConsumer<String, Clan>> clans = new ArrayList<>();

    // thread
    private static Thread thread;

    public static void sendPointsUpdate(Clan clan, int delta) {
        redis.publish("lolz:points", clan.getTag() + ":" + delta);
    }

    public static void sendClanRequest(String admin, Clan clan, int count) {
        redis.publish("lolz:send", admin+":"+count+":"+clan.getTag());
    }

    public static void registerPointsUpdate(BiConsumer<Clan, Integer> callback) {
        redis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {

                var s = message.split(":");
                var tag = s[0];
                var delta = Integer.parseInt(s[1]);
                var clan = Clan.get(tag);
                callback.accept(clan, delta);

            }
        }, "lolz:points");
    }

    public static void registerPointsUpdateAsync(BiConsumer<Clan, Integer> callback) {
        points.add(callback);
    }

    public static void registerClanRequest(BiConsumer<String, Clan> callback) {
        redis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("Received WARP: " + message);

                var s = message.split(":");
                var adminAndCount = s[0]+":"+s[1];
                var tag = s[2];
                var clan = Clan.get(tag);
                callback.accept(adminAndCount, clan);

            }
        }, "lolz:send");
    }

    public static void startThread() {
        if (thread == null) {
            thread = new Thread(Sync::use);
            thread.start();
        }
    }

    public static void registerClanRequestAsync(BiConsumer<String, Clan> callback) {
        clans.add(callback);
    }

    private static void use() {
        registerPointsUpdate((clan, delta) -> {
            for (BiConsumer<Clan, Integer> callback : points) {
                callback.accept(clan, delta);
            }
        });
        registerClanRequest((adminAndCount, clan) -> {
            for (BiConsumer<String, Clan> callback : clans) {
                callback.accept(adminAndCount, clan);
            }
        });
    }

}

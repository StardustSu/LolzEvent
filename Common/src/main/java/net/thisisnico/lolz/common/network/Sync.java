package net.thisisnico.lolz.common.network;

import net.thisisnico.lolz.common.database.Clan;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.function.BiConsumer;

public class Sync {

    private static final Jedis redis = new Jedis();

    public static void sendPointsUpdate(Clan clan, int delta) {
        redis.publish("lolz:points", clan.getTag() + ":" + delta);
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

}

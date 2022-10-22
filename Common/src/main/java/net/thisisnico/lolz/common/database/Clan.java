package net.thisisnico.lolz.common.database;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import net.thisisnico.lolz.common.network.Sync;

import java.util.ArrayList;

public class Clan {
    public Clan() {}

    @Getter @Setter
    private String tag;

    @Getter @Setter
    private String owner;

    @Getter @Setter
    private ArrayList<String> members = new ArrayList<>();

    @Getter
    private int points = 0;

    public void givePoints(int points) {
        this.points += points;
        save();
        Sync.sendPointsUpdate(this, points);
    }

    public void takePoints(int points) {
        this.points -= points;
        save();
        Sync.sendPointsUpdate(this, -points);
    }

    public void setPoints(int points) {
        Sync.sendPointsUpdate(this, this.points - points);
        this.points = points;
        save();
    }

    public void save() {
        Database.getClans().replaceOne(Filters.eq("tag", tag), this, new ReplaceOptions().upsert(true));
    }

    public static Clan get(String tag) {
        var cursor = Database.getClans().find(Filters.eq("tag", tag));
        return cursor.first();
    }

    public static Clan create(String tag, String owner) {
        var clan = new Clan();
        clan.tag = tag;
        clan.owner = owner;
        clan.members.add(owner);
        clan.save();
        return clan;
    }

}

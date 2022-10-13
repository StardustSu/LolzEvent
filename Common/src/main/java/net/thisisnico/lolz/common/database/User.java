package net.thisisnico.lolz.common.database;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;

public class User {
    public User() {}

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String clan;

    // УРОВЕНЬ ЗАЩИТЫ: 100000
    @Getter @Setter
    private boolean admin;

    public boolean hasClan() {
        return clan != null;
    }

    public void save() {
        Database.getUsers().replaceOne(Filters.eq("name", name), this,
                new ReplaceOptions().upsert(true));
    }

    public static User get(String name) {
        var cursor = Database.getUsers().find(Filters.eq("name", name));
        var user = cursor.first();
        if (user == null) return create(name);
        return user;
    }

    public static User create(String name) {
        var user = new User();
        user.name = name;
        user.save();
        return user;
    }

}

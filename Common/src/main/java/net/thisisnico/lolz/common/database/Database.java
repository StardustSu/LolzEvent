package net.thisisnico.lolz.common.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Database {

    private static final CodecRegistry pojo = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    private static final MongoClientSettings settings = MongoClientSettings.builder()
            .codecRegistry(pojo)
            .applyConnectionString(new ConnectionString("mongodb://localhost/?maxPoolSize=15"))
            .build();

    private static final MongoClient client = MongoClients.create(settings);
    private static final MongoDatabase db = client.getDatabase("LolzEvent").withCodecRegistry(pojo);

    @Getter
    private static final MongoCollection<User> users = db.getCollection("users", User.class);

    @Getter
    private static final MongoCollection<Clan> clans = db.getCollection("clans", Clan.class);


    private Database() {}

}

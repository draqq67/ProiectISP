package com.booking;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;

public class MongoConfig {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String CONNECTION_STRING = dotenv.get("MONGODB_URI");
    private static final String DB_NAME = dotenv.get("MONGODB_DB");

    private static final MongoClient client = MongoClients.create(CONNECTION_STRING);

    public static MongoDatabase getDatabase() {
        return client.getDatabase(DB_NAME);
    }
}

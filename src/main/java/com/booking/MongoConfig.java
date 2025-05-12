package com.booking;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;

public class MongoConfig {
    private static final String CONNECTION_STRING = "mongodb+srv://admin:admin@platformabooking.hnrptph.mongodb.net/";
    private static final String DB_NAME = "Booking";

    private static final MongoClient client = MongoClients.create(CONNECTION_STRING);

    public static MongoDatabase getDatabase() {
        return client.getDatabase(DB_NAME);
    }
}

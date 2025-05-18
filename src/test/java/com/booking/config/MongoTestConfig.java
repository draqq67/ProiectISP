package com.booking.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoTestConfig {
    private static final String CONNECTION_STRING = "mongodb+srv://admin:admin@platformabooking.hnrptph.mongodb.net/";
    private static final String DB_NAME = "BookingTest"; // aceasta este baza de test

    private static final MongoClient client = MongoClients.create(CONNECTION_STRING);

    public static MongoDatabase getDatabase() {
        return client.getDatabase(DB_NAME);
    }
}

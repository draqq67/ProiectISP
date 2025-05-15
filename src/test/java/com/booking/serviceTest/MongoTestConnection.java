package com.booking.serviceTest;

import com.booking.config.MongoTestConfig;
import org.bson.Document;
import org.junit.jupiter.api.Test;

public class MongoTestConnection {

    @Test
    public void testCreeazaBaza() {
        var db = MongoTestConfig.getDatabase();
        db.getCollection("users").insertOne(new Document("debug", true));
        System.out.println("Am inserat un document în baza BookingTest!");
    }
}

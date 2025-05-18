package com.booking.repository;

import com.booking.models.BookingItem;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class BookingItemRepository {
    private final MongoCollection<Document> collection;

    public BookingItemRepository(MongoDatabase db) {
        this.collection = db.getCollection("bookingItem");
    }

    public List<Document> save(List<BookingItem> items) {
        List<Document> docs = new ArrayList<>();
        for (BookingItem item : items) {
            Document d = new Document("id", item.getId())
                    .append("cameraId", item.getCameraId())
                    .append("cantitate", item.getCantitate())
                    .append("pretTotal", item.getPretTotal());
            docs.add(d);
        }
        return docs;
    }

    public List<BookingItem> parseBookingItems(List<Document> docs) {
        List<BookingItem> items = new ArrayList<>();
        for (Document d : docs) {
            items.add(new BookingItem(
                    d.getObjectId("id"),
                    d.getObjectId("cameraId"),
                    d.getInteger("cantitate"),
                    d.getDouble("pretTotal").floatValue()
            ));
        }
        return items;
    }



}

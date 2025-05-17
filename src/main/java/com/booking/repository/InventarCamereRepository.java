package com.booking.repository;

import com.booking.models.InventarCamera;
import com.booking.models.Hotel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class InventarCamereRepository {
    private final MongoCollection<Document> collection;

    public InventarCamereRepository(MongoDatabase db) {
        this.collection = db.getCollection("inventarCamere");
    }

    public void save(InventarCamera inventar) {
        Document doc = new Document("hotelId", inventar.getHotelId())
                .append("cameraId", inventar.getCameraId())
                .append("numarCamere", inventar.getNumarCamere());

        collection.insertOne(doc);
    }

    public List<InventarCamera> findByHotelId(ObjectId hotelId) {
        List<InventarCamera> lista = new ArrayList<>();
        for (Document doc : collection.find(new Document("hotelId", hotelId))) {
            ObjectId cameraId = doc.getObjectId("cameraId");
            int numarCamere = doc.getInteger("numarCamere");
            lista.add(new InventarCamera(hotelId, cameraId, numarCamere));
        }
        return lista;
    }

    public void update(InventarCamera inventar) {
        Document query = new Document("hotelId", inventar.getHotelId())
                .append("cameraId", inventar.getCameraId());

        Document updated = new Document("hotelId", inventar.getHotelId())
                .append("cameraId", inventar.getCameraId())
                .append("numarCamere", inventar.getNumarCamere());

        collection.replaceOne(query, updated);
    }

    public void delete(ObjectId hotelId, ObjectId cameraId) {
        Document query = new Document("hotelId", hotelId).append("cameraId", cameraId);
        collection.deleteOne(query);
    }
}
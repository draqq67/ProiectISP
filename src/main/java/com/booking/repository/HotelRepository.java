package com.booking.repository;

import com.booking.models.Hotel;
import com.booking.models.InventarCamera;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class HotelRepository {
    private final MongoCollection<Document> collection;

    public HotelRepository(MongoDatabase db) {
        this.collection = db.getCollection("hotels");
    }

    public void save(Hotel hotel) {
        List<Document> camereDocs = new ArrayList<>();
        for (InventarCamera inventar : hotel.getCamere()) {
            camereDocs.add(new Document("cameraId", inventar.getCameraId())
                                  .append("numarCamere", inventar.getNumarCamere()));
        }

        Document doc = new Document("nume", hotel.getNume())
                .append("locatie", hotel.getLocatie())
                .append("managerId", hotel.getManagerId())
                .append("camere", camereDocs);

        if (hotel.getId() != null) {
            doc.append("_id", hotel.getId());
        }

        collection.insertOne(doc);
    }

    public void update(Hotel hotel) {
        List<Document> camereDocs = new ArrayList<>();
        for (InventarCamera inventar : hotel.getCamere()) {
            camereDocs.add(new Document("cameraId", inventar.getCameraId())
                                  .append("numarCamere", inventar.getNumarCamere()));
        }

        Document updatedDoc = new Document("nume", hotel.getNume())
                .append("locatie", hotel.getLocatie())
                .append("managerId", hotel.getManagerId())
                .append("camere", camereDocs);

        collection.replaceOne(new Document("_id", hotel.getId()), updatedDoc);
    }

    public List<Hotel> findByManagerId(ObjectId managerId) {
        List<Hotel> hoteluri = new ArrayList<>();
        for (Document doc : collection.find(new Document("managerId", managerId))) {
            Hotel hotel = new Hotel();
            hotel.setId(doc.getObjectId("_id"));
            hotel.setNume(doc.getString("nume"));
            hotel.setLocatie(doc.getString("locatie"));
            hotel.setManagerId(doc.getObjectId("managerId"));

            List<InventarCamera> camere = new ArrayList<>();
            List<Document> camereDocs = (List<Document>) doc.get("camere");
            if (camereDocs != null) {
                for (Document cam : camereDocs) {
                    ObjectId cameraId = cam.getObjectId("cameraId");
                    int numarCamere = cam.getInteger("numarCamere");
                    camere.add(new InventarCamera(hotel.getId(), cameraId, numarCamere));
                }
            }
            hotel.setCamere(camere);
            hoteluri.add(hotel);
        }
        return hoteluri;
    }

    public Hotel findById(ObjectId id) {
        Document doc = collection.find(new Document("_id", id)).first();
        if (doc == null) return null;

        Hotel hotel = new Hotel();
        hotel.setId(doc.getObjectId("_id"));
        hotel.setNume(doc.getString("nume"));
        hotel.setLocatie(doc.getString("locatie"));
        hotel.setManagerId(doc.getObjectId("managerId"));

        List<InventarCamera> camere = new ArrayList<>();
        List<Document> camereDocs = (List<Document>) doc.get("camere");
        if (camereDocs != null) {
            for (Document cam : camereDocs) {
                ObjectId cameraId = cam.getObjectId("cameraId");
                int numarCamere = cam.getInteger("numarCamere");
                camere.add(new InventarCamera(hotel.getId(), cameraId, numarCamere));
            }
        }
        hotel.setCamere(camere);

        return hotel;
    }

    public List<Hotel> findAll() {
        List<Hotel> hoteluri = new ArrayList<>();
        for (Document doc : collection.find()) {
            hoteluri.add(findById(doc.getObjectId("_id")));
        }
        return hoteluri;
    }

    

    public void deleteById(ObjectId id) {
        collection.deleteOne(new Document("_id", id));
    }
}
package com.booking.repository;

import com.booking.models.Camera;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CameraRepository {
    private final MongoCollection<Document> collection;

    public CameraRepository(MongoDatabase db) {
        this.collection = db.getCollection("cameras");
    }

    public void save(Camera camera) {
        Document doc = new Document("denumire", camera.getDenumire())
                .append("capacitate", camera.getCapacitate())
                .append("pretPeNoapte", camera.getPretPeNoapte())
                .append("dateIndisponibile", camera.getDateIndisponibile());

        if (camera.getId() != null) {
            doc.append("_id", camera.getId());
        }

        collection.insertOne(doc);
    }

    public Camera findById(ObjectId id) {
        Document doc = collection.find(new Document("_id", id)).first();
        if (doc == null) return null;

        Camera camera = new Camera();
        camera.setId(doc.getObjectId("_id"));
        camera.setDenumire(doc.getString("denumire"));
        camera.setCapacitate(doc.getInteger("capacitate"));
        camera.setPretPeNoapte(doc.getDouble("pretPeNoapte").floatValue());

        List<String> dateList = (List<String>) doc.get("dateIndisponibile");
        if (dateList != null) {
            Set<LocalDate> dates = dateList.stream().map(LocalDate::parse).collect(Collectors.toSet());
            camera.setDateIndisponibile(dates);
        }

        return camera;
    }

    public List<Camera> findAll() {
        List<Camera> camere = new ArrayList<>();
        for (Document doc : collection.find()) {
            camere.add(findById(doc.getObjectId("_id")));
        }
        return camere;
    }

    public void update(Camera camera) {
        Document updated = new Document("denumire", camera.getDenumire())
                .append("capacitate", camera.getCapacitate())
                .append("pretPeNoapte", camera.getPretPeNoapte())
                .append("dateIndisponibile", camera.getDateIndisponibile());

        collection.replaceOne(new Document("_id", camera.getId()), updated);
    }

    public void deleteById(ObjectId id) {
        collection.deleteOne(new Document("_id", id));
    }
}

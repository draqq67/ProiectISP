package com.booking.repository;

import com.booking.models.IndisponibilitateCamera;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IndisponibilitateCameraRepository {
    private final MongoCollection<Document> collection;

    public IndisponibilitateCameraRepository(MongoDatabase db) {
        this.collection = db.getCollection("indisponibilitateCamera");
    }

    public void save(IndisponibilitateCamera indisponibilitate) {
        Document doc = new Document("cameraId", indisponibilitate.getCameraId())
                .append("dataStart", indisponibilitate.getDataStart().toString())
                .append("dataEnd", indisponibilitate.getDataEnd().toString());

        if (indisponibilitate.getId() != null) {
            doc.append("_id", indisponibilitate.getId());
        }

        collection.insertOne(doc);
    }

    public List<IndisponibilitateCamera> findByCameraId(ObjectId cameraId) {
        List<IndisponibilitateCamera> lista = new ArrayList<>();
        for (Document doc : collection.find(new Document("cameraId", cameraId))) {
            IndisponibilitateCamera i = new IndisponibilitateCamera();
            i.setId(doc.getObjectId("_id"));
            i.setCameraId(doc.getObjectId("cameraId"));
            i.setDataStart(LocalDate.parse(doc.getString("dataStart")));
            i.setDataEnd(LocalDate.parse(doc.getString("dataEnd")));
            lista.add(i);
        }
        return lista;
    }

    public List<IndisponibilitateCamera> findAll() {
        List<IndisponibilitateCamera> lista = new ArrayList<>();
        for (Document doc : collection.find()) {
            IndisponibilitateCamera i = new IndisponibilitateCamera();
            i.setId(doc.getObjectId("_id"));
            i.setCameraId(doc.getObjectId("cameraId"));
            i.setDataStart(LocalDate.parse(doc.getString("dataStart")));
            i.setDataEnd(LocalDate.parse(doc.getString("dataEnd")));
            lista.add(i);
        }
        return lista;
    }

    public void update(IndisponibilitateCamera indisponibilitate) {
        Document updated = new Document("cameraId", indisponibilitate.getCameraId())
                .append("dataStart", indisponibilitate.getDataStart().toString())
                .append("dataEnd", indisponibilitate.getDataEnd().toString());

        collection.replaceOne(new Document("_id", indisponibilitate.getId()), updated);
    }

    public void deleteById(ObjectId id) {
        collection.deleteOne(new Document("_id", id));
    }


    /**
     * Returnează lista cu ID-urile camerelor disponibile într-un anumit interval.
     * @param startDate Data de început a intervalului (inclusiv)
     * @param endDate Data de sfârșit a intervalului (inclusiv)
     * @param allCameraIds Lista cu toate camerele din sistem
     * @return Lista cu ObjectId-urile camerelor disponibile (fără suprapuneri)
     */
    public List<ObjectId> findAvailableCameraIds(LocalDate startDate, LocalDate endDate, List<ObjectId> allCameraIds) {
        List<ObjectId> indisponibile = new ArrayList<>();

        for (Document doc : collection.find()) {
            LocalDate dataStart = LocalDate.parse(doc.getString("dataStart"));
            LocalDate dataEnd = LocalDate.parse(doc.getString("dataEnd"));

            boolean overlap = !dataEnd.isBefore(startDate) && !dataStart.isAfter(endDate);
            if (overlap) {
                indisponibile.add(doc.getObjectId("cameraId"));
            }
        }

        List<ObjectId> disponibile = new ArrayList<>();
        for (ObjectId cameraId : allCameraIds) {
            if (!indisponibile.contains(cameraId)) {
                disponibile.add(cameraId);
            }
        }

        return disponibile;
    }
}
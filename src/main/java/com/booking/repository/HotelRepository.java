package com.booking.repository;

import com.booking.models.Camera;
import com.booking.repository.IndisponibilitateCameraRepository;
import java.time.LocalDate;


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

    public List<Hotel> findByLocatie(String locatie) {
        List<Hotel> hoteluri = new ArrayList<>();
        for (Document doc : collection.find(new Document("locatie", locatie))) {
            hoteluri.add(findById(doc.getObjectId("_id")));
        }
        return hoteluri;
    }

    /**
     * Găsește hotelurile disponibile într-o locație dată, pentru un număr de persoane și un interval de timp.
     *
     * @param locatie Locația dorită
     * @param startDate Data de început a intervalului
     * @param endDate Data de sfârșit a intervalului
     * @param nrPersoane Numărul de persoane
     * @param indisponibilitateRepo Repository pentru camere indisponibile
     * @param toateCamerele Lista tuturor camerelor
     * @return Lista hotelurilor disponibile
     */
    public List<Hotel> findHoteluriDisponibile(String locatie, LocalDate startDate, LocalDate endDate, int nrPersoane,
                                               IndisponibilitateCameraRepository indisponibilitateRepo,
                                               List<Camera> toateCamerele) {
        List<Hotel> toate = findByLocatie(locatie);
        List<Hotel> disponibile = new ArrayList<>();

        for (Hotel hotel : toate) {
            int totalCapacitate = 0;

            for (InventarCamera inv : hotel.getCamere()) {
                Camera camera = toateCamerele.stream()
                        .filter(c -> c.getId().equals(inv.getCameraId()))
                        .findFirst().orElse(null);

                if (camera != null) {
                    List<ObjectId> camereDisponibile = indisponibilitateRepo.findAvailableCameraIds(startDate, endDate,
                            List.of(inv.getCameraId()));

                    if (!camereDisponibile.isEmpty()) {
                        totalCapacitate += camera.getCapacitate() * inv.getNumarCamere();
                    }
                }
            }

            if (totalCapacitate >= nrPersoane) {
                disponibile.add(hotel);
            }
        }

        return disponibile;
    }
}
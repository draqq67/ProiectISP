package com.booking.repository;

import com.booking.MongoConfig;
import com.booking.model.Rezervare;
import com.booking.model.Servicii;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class RezervareRepository {
    private final MongoCollection<Document> colectie;

    public RezervareRepository() {
        MongoDatabase db = MongoConfig.getDatabase();
        if (!db.listCollectionNames().into(new ArrayList<>()).contains("rezervari")) {
            db.createCollection("rezervari");
        }
        colectie = db.getCollection("rezervari");
    }

    public void adaugaRezervare(Rezervare rezervare) {
        List<String> serviciiDocs = new ArrayList<>();
        for (Servicii serviciu : rezervare.getServicii()) {
            serviciiDocs.add(serviciu.name()); // Storing the name of the enum
        }

        Document doc = new Document("clientId", rezervare.getClientId())
                .append("hotelId", rezervare.getHotelId())
                .append("camera", rezervare.getCamera())
                .append("servicii", serviciiDocs) // Storing a list of enum names
                .append("data", rezervare.getData());
        colectie.insertOne(doc);
    }

    public List<Rezervare> obtineRezervariPentruClient(String clientId) {
        List<Rezervare> lista = new ArrayList<>();
        for (Document doc : colectie.find(new Document("clientId", clientId))) {
            Rezervare rezervare = new Rezervare();
            rezervare.setClientId(doc.getString("clientId"));
            rezervare.setHotelId(doc.getString("hotelId"));
            rezervare.setCamera(doc.getString("camera"));
            rezervare.setData(doc.getString("data"));

            // Converting the string values back to enum
            List<Servicii> servicii = new ArrayList<>();
            for (String serviciuStr : (List<String>) doc.get("servicii")) {
                servicii.add(Servicii.valueOf(serviciuStr)); // Converting string to enum
            }
            rezervare.setServicii(servicii);

            lista.add(rezervare);
        }
        return lista;
    }
}

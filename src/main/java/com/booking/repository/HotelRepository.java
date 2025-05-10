package com.booking.repository;

import com.booking.MongoConfig;
import com.booking.model.Hotel;
import com.booking.model.Servicii;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class HotelRepository {
    private final MongoCollection<Document> colectie;

    public HotelRepository() {
        MongoDatabase db = MongoConfig.getDatabase();
        if (!db.listCollectionNames().into(new ArrayList<>()).contains("hoteluri")) {
            db.createCollection("hoteluri");
        }
        colectie = db.getCollection("hoteluri");
    }

    public void adaugaHotel(Hotel hotel) {
        List<String> serviciiDocs = new ArrayList<>();
        for (Servicii serviciu : hotel.getListaServicii()) {
            serviciiDocs.add(serviciu.name()); // Storing the name of the enum
        }

        Document doc = new Document("tipCamera", hotel.getTipCamera())
                .append("listaServicii", serviciiDocs) // Storing a list of enum names
                .append("nrCamere", hotel.getNrCamere())
                .append("partenerId", hotel.getPartenerId());
        colectie.insertOne(doc);
    }

    public List<Hotel> obtineHoteluriCuPartenerId(String partenerId) {
        List<Hotel> lista = new ArrayList<>();
        for (Document doc : colectie.find(new Document("partenerId", partenerId))) {
            Hotel hotel = new Hotel(
                    doc.getString("tipCamera"),
                    new ArrayList<>(),
                    doc.getInteger("nrCamere"),
                    doc.getString("partenerId")
            );

            // Converting the string values back to enum
            List<Servicii> servicii = new ArrayList<>();
            for (String serviciuStr : (List<String>) doc.get("listaServicii")) {
                servicii.add(Servicii.valueOf(serviciuStr)); // Converting string to enum
            }
            hotel.setListaServicii(servicii);

            lista.add(hotel);
        }
        return lista;
    }
}

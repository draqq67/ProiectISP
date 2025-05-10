package com.booking.repository;

import com.booking.MongoConfig;
import com.booking.model.Utilizator;
import com.booking.model.Rol;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UtilizatorRepository {
    private final MongoCollection<Document> colectie;

    public UtilizatorRepository() {
        MongoDatabase db = MongoConfig.getDatabase();
        if (!db.listCollectionNames().into(new ArrayList<>()).contains("utilizatori")) {
            db.createCollection("utilizatori");
        }
        colectie = db.getCollection("utilizatori");
    }

    public void adaugaUtilizator(Utilizator u) {
        Document doc = new Document("nume", u.getNume())
                .append("prenume", u.getPrenume())
                .append("email", u.getMail())
                .append("rol", u.getRol().name());
        colectie.insertOne(doc);
    }

    public List<Utilizator> obtineUtilizatoriCuRol(Rol rol) {
        List<Utilizator> lista = new ArrayList<>();
        for (Document doc : colectie.find(new Document("rol", rol.name()))) {
            Utilizator u = new Utilizator(
                    doc.getString("nume"),
                    doc.getString("prenume"),
                    doc.getString("email"),
                    Rol.valueOf(doc.getString("rol"))
            );
            lista.add(u);
        }
        return lista;
    }
}

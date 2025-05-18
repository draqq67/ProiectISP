package com.booking.repository;

import com.booking.models.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final MongoCollection<Document> collection;

    public UserRepository(MongoDatabase db) {
        this.collection = db.getCollection("users");
    }

    public void save(User user) {
        Document doc = new Document("username", user.getUsername())
                .append("parola", user.getParola())
                .append("nume", user.getNume())
                .append("prenume", user.getPrenume())
                .append("email", user.getEmail());

        if (user instanceof Client client) {
            doc.append("role", "client")
                    .append("sold", client.getSold())
                    .append("carduri", client.getCarduri()); // va fi o listă de subdocumente
        } else if (user instanceof Manager manager) {
            doc.append("role", "manager")
                    .append("hotelIds", manager.getHotelIds()); // listă de ObjectId-uri
        } else if (user instanceof Admin) {
            doc.append("role", "admin");
        } else {
            doc.append("role", "unknown");
        }

        System.out.println("✅ User salvat în Mongo: " + user.getUsername());

        collection.insertOne(doc);
    }

    private User parseUser(Document doc) {
        String role = doc.getString("role");

        switch (role) {
            case "client" -> {
                Client client = new Client();
                client.setId(doc.getObjectId("_id"));
                client.setUsername(doc.getString("username"));
                client.setParola(doc.getString("parola"));
                client.setNume(doc.getString("nume"));
                client.setPrenume(doc.getString("prenume"));
                client.setEmail(doc.getString("email"));
                client.setSold(doc.getDouble("sold").floatValue());

                List<Document> cardDocs = (List<Document>) doc.get("carduri");
                List<Card> carduri = new ArrayList<>();
                if (cardDocs != null) {
                    for (Document cardDoc : cardDocs) {
                        Card card = new Card();
                        card.setNumar(cardDoc.getString("numar"));
                        card.setCvv(cardDoc.getString("cvv"));
                        card.setDetinator(cardDoc.getObjectId("detinator"));
                        carduri.add(card);
                    }
                }
                client.setCarduri(carduri);
                return client;
            }
            case "manager" -> {
                Manager manager = new Manager();
                manager.setId(doc.getObjectId("_id"));
                manager.setUsername(doc.getString("username"));
                manager.setParola(doc.getString("parola"));
                manager.setNume(doc.getString("nume"));
                manager.setPrenume(doc.getString("prenume"));
                manager.setEmail(doc.getString("email"));
                manager.setHotelIds((List<ObjectId>) doc.get("hotelIds"));
                return manager;
            }
            case "admin" -> {
                Admin admin = new Admin();
                admin.setId(doc.getObjectId("_id"));
                admin.setUsername(doc.getString("username"));
                admin.setParola(doc.getString("parola"));
                admin.setNume(doc.getString("nume"));
                admin.setPrenume(doc.getString("prenume"));
                admin.setEmail(doc.getString("email"));
                return admin;
            }
            default -> {
                return null;
            }
        }
    }

    public User findByUsername(String username) {
        Document query = new Document("username", username);
        Document doc = collection.find(query).first();
        return doc == null ? null : parseUser(doc);
    }

    public void deleteById(ObjectId id) {
        collection.deleteOne(new Document("_id", id));
    }

    public User findByUsernameOrEmailAndPassword(String input, String parola) {
        Document query = new Document("$and", List.of(
                new Document("$or", List.of(
                        new Document("username", input),
                        new Document("email", input)
                )),
                new Document("parola", parola)
        ));

        Document doc = collection.find(query).first();
        return doc == null ? null : parseUser(doc);
    }

    public boolean existsByUsernameOrEmail(String username, String email) {
        Document query = new Document("$or", List.of(
                new Document("username", username),
                new Document("email", email)
        ));

        return collection.find(query).first() != null;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public boolean cardExistaGlobal(String numar, String cvv) {
        Document query = new Document("carduri", new Document(
                "$elemMatch", new Document("numar", numar).append("cvv", cvv)
        ));
        return collection.find(query).first() != null;
    }

    public void adaugaCard(Client client, Card card) {
        Document update = new Document("$push", new Document("carduri", new Document()
                .append("numar", card.getNumar())
                .append("cvv", card.getCvv())
                .append("detinator", card.getDetinator())
        ));
        collection.updateOne(new Document("_id", client.getId()), update);
    }

    public boolean platesteCuCard(ObjectId clientId, String numarCard, String cvv, float suma, ObjectId destinatarId) {
        // 1. Verificăm existența cardului valid
        Document queryClient = new Document("_id", clientId)
                .append("carduri", new Document("$elemMatch", new Document("numar", numarCard).append("cvv", cvv)));
        Document docClient = collection.find(queryClient).first();

        if (docClient == null) {
            System.out.println(" Card invalid sau client inexistent.");
            return false;
        }

        float soldCurent = ((Double) docClient.get("sold")).floatValue();
        if (soldCurent < suma) {
            System.out.println(" Fonduri insuficiente.");
            return false;
        }

        // 2. Scădem suma din soldul clientului
        collection.updateOne(new Document("_id", clientId),
                new Document("$set", new Document("sold", soldCurent - suma)));

        // 3. Adăugăm suma în soldul destinatarului (dacă e client sau manager)
        Document docDestinatar = collection.find(new Document("_id", destinatarId)).first();
        if (docDestinatar != null && docDestinatar.containsKey("sold")) {
            float soldDest = ((Double) docDestinatar.get("sold")).floatValue();
            collection.updateOne(new Document("_id", destinatarId),
                    new Document("$set", new Document("sold", soldDest + suma)));
        }

        System.out.println(" Plata efectuată către utilizatorul cu ID: " + destinatarId);
        return true;
    }
}



package com.booking.repository;

import com.booking.models.*;
import com.booking.models.Factura;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;

public class FacturaRepository {
    private MongoCollection<Document> collection;
    public FacturaRepository(MongoDatabase db) {this.collection = db.getCollection("factura");}

    public List<Document> convertBookingItems(List<BookingItem> items) {
        List<Document> docs = new ArrayList<>();
        for (BookingItem item : items) {
            docs.add(new Document("id", item.getId())
                    .append("cameraId", item.getCameraId())
                    .append("cantitate", item.getCantitate())
                    .append("pretTotal", item.getPretTotal()));
        }
        return docs;
    }
    public void save(Factura factura) {
        Document doc = new Document("clientId", factura.getClientId().getId())
                .append("hotelId", factura.getHotelId().getId())
                .append("rezervari", factura.getRezervari().stream().map(item -> new Document()
                        .append("id", item.getId())
                        .append("cameraId", item.getCameraId())
                        .append("cantitate", item.getCantitate())
                        .append("pretTotal", item.getPretTotal())
                ).toList())
                .append("total", factura.getTotal())
                .append("dateEmitere", factura.getDateEmitere())
                .append("bookingId", factura.getBooking().getId());

        if (factura.getId() != null) {
            doc.append("_id", factura.getId());
        }

        collection.insertOne(doc);
    }

    public List<Factura> findByClientId(ObjectId clientId) {
        List<Factura> facturi = new ArrayList<>();

        for (Document doc : collection.find(eq("clientId", clientId))) {
            Factura factura = new Factura();
            factura.setId(doc.getObjectId("_id"));

            Client client = new Client();
            client.setId(doc.getObjectId("clientId"));
            factura.setClientId(client);

            Hotel hotel = new Hotel();
            hotel.setId(doc.getObjectId("hotelId"));
            factura.setHotelId(hotel);

            Booking booking = new Booking();
            booking.setId(doc.getObjectId("bookingId"));
            factura.setBooking(booking);

            List<Document> items = (List<Document>) doc.get("rezervari");
            List<BookingItem> rezervari = new ArrayList<>();
            for (Document itemDoc : items) {
                BookingItem item = new BookingItem(
                        itemDoc.getObjectId("id"),
                        itemDoc.getObjectId("cameraId"),
                        itemDoc.getInteger("cantitate"),
                        itemDoc.getDouble("pretTotal").floatValue()
                );
                rezervari.add(item);
            }
            factura.setRezervari(rezervari);

            factura.setTotal(doc.getDouble("total").floatValue());
            factura.setDateEmitere(doc.getDate("dateEmitere"));

            facturi.add(factura);
        }

        return facturi;
    }


    public void deleteById(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }
}



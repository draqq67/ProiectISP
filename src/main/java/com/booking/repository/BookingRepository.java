package com.booking.repository;

import com.booking.models.Booking;
import com.booking.models.BookingItem;
import com.booking.models.BookingStatus;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class BookingRepository {
   private final MongoCollection<Document> collection;

   public BookingRepository(MongoDatabase db) {
       this.collection = db.getCollection("booking");
   }

    public void save(Booking booking) {
       Document doc = new Document("clientId", booking.getClientId())
               .append("hotelId", booking.getHotelId())
               .append("camereRezervate", convertBookingItems(booking.getCamereRezervate()))
               .append("total", booking.getPretTotal())
               .append("dataCheckIn", booking.getDataCheckIn())
               .append("dataCheckOut", booking.getDataCheckOut())
               .append("status", booking.getStatus());

       if(booking.getId() != null) {
           doc.append("_id", booking.getId());
       }
       collection.insertOne(doc);
    }

    public Booking getBooking(ObjectId id) {
        Document doc = collection.find(eq("_id", id)).first();
        if (doc == null) return null;

        Booking booking = new Booking();
        booking.setId(doc.getObjectId("_id"));
        booking.setClientId(doc.getObjectId("clientId"));
        booking.setHotelId(doc.getObjectId("hotelId"));
        booking.setPretTotal(doc.getDouble("total").floatValue());
        booking.setDataCheckIn(doc.getDate("dataCheckIn"));
        booking.setDataCheckOut(doc.getDate("dataCheckOut"));
        booking.setStatus(BookingStatus.valueOf(doc.getString("status")));

        List<Document> camereDocs = (List<Document>) doc.get("camereRezervate");
        List<BookingItem> camere = new ArrayList<>();
        for (Document d : camereDocs) {
            BookingItem item = new BookingItem(
                    d.getObjectId("id"),
                    d.getObjectId("cameraId"),
                    d.getInteger("cantitate"),
                    d.getDouble("pretTotal").floatValue()
            );
            camere.add(item);
        }
        booking.setCamereRezervate(camere);

        return booking;
    }


    public void delete(ObjectId id) {
       collection.deleteOne(eq("_id", id));
    }

    private List<Document> convertBookingItems(List<BookingItem> items) {
        List<Document> docs = new ArrayList<>();
        for (BookingItem item : items) {
            docs.add(new Document("id", item.getId())
                    .append("cameraId", item.getCameraId())
                    .append("cantitate", item.getCantitate())
                    .append("pretTotal", item.getPretTotal()));
        }
        return docs;
    }

    public void update(Booking booking) {
        Document doc = new Document("clientId", booking.getClientId())
                .append("hotelId", booking.getHotelId())
                .append("camereRezervate", convertBookingItems(booking.getCamereRezervate()))
                .append("total", booking.getPretTotal())
                .append("dataCheckIn", booking.getDataCheckIn())
                .append("dataCheckOut", booking.getDataCheckOut())
                .append("status", booking.getStatus().name());

        collection.updateOne(eq("_id", booking.getId()), new Document("$set", doc));
    }

    public void updateStatus(ObjectId id, BookingStatus status) {
       collection.updateOne(eq("_id", id), new Document("$set", new Document("status", status.name())));
    }

    public void checkOut(ObjectId id) {
       collection.updateOne(eq("_id", id), new Document("status", BookingStatus.CHECKED_OUT.name()));
    }
    public void checkOut(ObjectId id, CameraRepository cameraRepository) {
       Booking booking = getBooking(id);

       if(booking == null) {
           return;
       }
       updateStatus(id, BookingStatus.CHECKED_OUT);

       if(booking.getCamereRezervate() != null) {
           for(BookingItem item : booking.getCamereRezervate()) {
               ObjectId cameraId = item.getCameraId();
               cameraRepository.markAsAvailable(cameraId);
           }
       }
    }
}

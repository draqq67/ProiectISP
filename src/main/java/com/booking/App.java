package com.booking;

import com.booking.config.MongoConfig;
import com.booking.models.*;
import com.booking.repository.*;
import com.booking.service.ClientService;
import com.booking.service.PlataService;
import com.booking.service.UserService;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class App {
    public static void main(String[] args) {
        var db = MongoConfig.getDatabase();

        // Repozitorii
        var userRepo = new UserRepository(db);
        var bookingRepo = new BookingRepository(db);
        var facturaRepo = new FacturaRepository(db);

        // Servicii
        var clientService = new ClientService(userRepo);
        var plataService = new PlataService(bookingRepo, userRepo, facturaRepo);

        // 🔍 1. Găsim clientul
        Client client = (Client) userRepo.findByUsername("testuser");
        if (client == null) {
            System.out.println("❌ Clientul nu a fost găsit.");
            return;
        }

        // 💳 2. Adăugăm card
        String numarCard = "1234123412341234";
        String cvv = "123";
        clientService.adaugaCard(client, numarCard, cvv);

        // 💰 3. Încărcăm soldul
        clientService.incarcaSold(client, numarCard, cvv, 300f);

        // 📦 4. Creăm un booking NEPLĂTIT pentru test
        ObjectId cameraId = new ObjectId("6828ce636ab7eb3c858745ba"); // asigură-te că există
        ObjectId hotelId = new ObjectId("6828ce636ab7eb3c858745bd");

        BookingItem item = new BookingItem(new ObjectId(), cameraId, 1, 200f);

        Booking booking = Booking.builder()
                .id(new ObjectId())
                .clientId(client.getId())
                .hotelId(hotelId)
                .camereRezervate(List.of(item))
                .pretTotal(200f)
                .dataCheckIn(new Date())
                .dataCheckOut(new Date(System.currentTimeMillis() + 86400000))
                .status(BookingStatus.NEPLATIT)
                .build();

        bookingRepo.save(booking);
        System.out.println("✅ Booking creat cu status NEPLATIT. ID: " + booking.getId());

        // 💳 5. Facem plata
        plataService.platesteBooking(client.getId(), booking.getId());
    }
}

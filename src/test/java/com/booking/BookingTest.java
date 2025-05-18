package com.booking;

import com.booking.config.MongoConfig;
import com.booking.models.*;
import com.booking.repository.*;
import com.booking.service.BookingService;
import com.booking.service.UserService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    @Test
    public void testInsertBooking1() {
        var db = MongoConfig.getDatabase();
        var userRepo = new UserRepository(db);
        var userService = new UserService(userRepo);
        IndisponibilitateCameraRepository repo = new IndisponibilitateCameraRepository(db);
        BookingService bookingService = new BookingService(repo);
        BookingItem item = new BookingItem(new ObjectId(), new ObjectId(), 2, 500.0f);
        BookingRepository bookingRepository = new BookingRepository(db);
        Booking booking = Booking.builder()
                .id(new ObjectId())
                .clientId(new ObjectId())
                .hotelId(new ObjectId())
                .camereRezervate(List.of(item))
                .pretTotal(500.0f)
                .dataCheckIn(new Date())
                .dataCheckOut(new Date(System.currentTimeMillis() + 2 * 86400000))
                .status(BookingStatus.PLATIT)
                .build();
        bookingRepository.save(booking);

        List<ObjectId> toateCamerele = Arrays.asList(
                new ObjectId("66301234ab00000123456789"),
                new ObjectId("66301234ab00000123456788")
        );

    }
    @Test
    public void testSaveAndFindFacturaByClientId() {
        var db = MongoConfig.getDatabase();
        var facturaRepo = new FacturaRepository(db);

        ObjectId clientId = new ObjectId();
        ObjectId hotelId = new ObjectId();
        ObjectId bookingId = new ObjectId();

        // Rezervări fictive
        BookingItem item = new BookingItem(new ObjectId(), new ObjectId(), 2, 300.0f);
        List<BookingItem> rezervari = List.of(item);

        // Creeaza factura
        Factura factura = Factura.builder()
                .id(new ObjectId())
                .clientId(new Client() {{
                    setId(clientId);
                }})
                .hotelId(new Hotel() {{
                    setId(hotelId);
                }})
                .booking(new Booking() {{
                    setId(bookingId);
                }})
                .rezervari(rezervari)
                .total(300.0f)
                .dateEmitere(new Date())
                .build();

        facturaRepo.save(factura);

        List<Factura> facturi = facturaRepo.findByClientId(clientId);

        assertFalse(facturi.isEmpty());
        assertEquals(clientId, facturi.get(0).getClientId().getId());
        System.out.println("Factura salvată și regăsită cu succes." + facturi);
    }

    @Test
    public void testAdaugareCamere() {
        var db = MongoConfig.getDatabase();
        var cameraRepo = new CameraRepository(db);

        Camera camera1 = new Camera(
                new ObjectId(),
                "Camera Dubla",
                2,
                150.0f,
                new HashSet<>()
        );

        Camera camera2 = new Camera(
                new ObjectId(),
                "Camera Single",
                1,
                100.0f,
                new HashSet<>(Set.of(LocalDate.of(2025, 5, 22))) // indisponibilă într-o zi
        );

        cameraRepo.save(camera1);
        cameraRepo.save(camera2);

        System.out.println("Camere salvate cu succes." + camera1);
    }
}

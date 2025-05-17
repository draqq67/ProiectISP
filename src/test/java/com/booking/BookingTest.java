package com.booking;

import com.booking.config.MongoConfig;
import com.booking.models.Booking;
import com.booking.models.BookingItem;
import com.booking.models.BookingStatus;
import com.booking.repository.BookingRepository;
import com.booking.repository.UserRepository;
import com.booking.service.UserService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.util.Date;
import java.util.List;

public class BookingTest {

    @Test
    public void testInsertBooking1() {
        var db = MongoConfig.getDatabase();
        var userRepo = new UserRepository(db);
        var userService = new UserService(userRepo);
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
    }
}

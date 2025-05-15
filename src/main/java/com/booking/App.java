package com.booking;

import com.booking.config.MongoConfig;
import com.booking.repository.UserRepository;
import com.booking.service.UserService;

public class App {
    public static void main(String[] args) {
        var db = MongoConfig.getDatabase();
        var userRepo = new UserRepository(db);
        var userService = new UserService(userRepo);

        // Testare manuală: register client
        userService.register(
                "ana.popescu",
                "parola123",
                "Popescu",
                "Ana",
                "ana.popescu@example.com",
                "client"
        );

        // Testare manager
        userService.register(
                "manager1",
                "pass456",
                "Ionescu",
                "Dan",
                "manager.ionescu@example.com",
                "manager"
        );
    }
}

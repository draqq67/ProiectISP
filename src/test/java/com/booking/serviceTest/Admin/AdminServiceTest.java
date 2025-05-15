package com.booking.serviceTest.Admin;

import com.booking.config.MongoTestConfig;
import com.booking.models.Client;
import com.booking.repository.UserRepository;
import com.booking.service.AdminService;
import com.booking.service.ClientService;
import com.booking.service.UserService;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest {

    @Test
    public void testStergereClientCuAdmin() {
        var db = MongoTestConfig.getDatabase();
        UserRepository userRepository = new UserRepository(db);
        UserService userService = new UserService(userRepository);
        AdminService adminService = new AdminService(userRepository);

        // 1. Creează clientul
        String username = "clientDeSters";
        String email = "sterge@exemplu.com";
        userService.register(username, "parola123", "Stergator", "Test", email, "client");

        // 2. Adaugă card
        var user = userService.login(username, "parola123");
        assertNotNull(user);
        assertTrue(user instanceof Client);
        Client client = (Client) user;

        ClientService clientService = new ClientService(userRepository);
        clientService.adaugaCard(client, "4567123412341234", "999");

        // 3. Verificare existență
        long inainte = db.getCollection("users")
                .countDocuments(new Document("username", username));
        assertEquals(1, inainte, "Clientul trebuie să existe înainte de ștergere.");

        // 4. Ștergere
        adminService.stergeClient(username);

        // 5. Verificare că nu mai există
        long dupa = db.getCollection("users")
                .countDocuments(new Document("username", username));
        assertEquals(0, dupa, "Clientul trebuie să fie șters complet.");
    }
}

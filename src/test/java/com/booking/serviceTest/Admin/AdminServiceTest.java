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

/*
 Acest test verifică dacă un admin poate:
 - crea un client nou
 - adăuga un card pentru acel client
 - șterge complet clientul din baza de date
 - confirma că acel client nu mai există după ștergere
*/

public class AdminServiceTest {

    @Test
    public void testStergereClientCuAdmin() {
        var db = MongoTestConfig.getDatabase();
        UserRepository userRepository = new UserRepository(db);
        UserService userService = new UserService(userRepository);
        AdminService adminService = new AdminService(userRepository);

        String username = "clientDeSters";
        String email = "sterge@exemplu.com";

        userService.register(username, "parola123", "Stergator", "Test", email, "client");
        System.out.println("Client înregistrat: " + username);

        var user = userService.login(username, "parola123");
        assertNotNull(user);
        assertTrue(user instanceof Client);
        Client client = (Client) user;
        System.out.println("Client autentificat cu succes: " + client.getUsername());

        ClientService clientService = new ClientService(userRepository);
        clientService.adaugaCard(client, "4567123412341234", "999");
        System.out.println("Card adăugat pentru client: " + client.getUsername());

        long inainte = db.getCollection("users")
                .countDocuments(new Document("username", username));
        assertEquals(1, inainte);
        System.out.println("Verificare: clientul există în baza de date.");

        adminService.stergeClient(username);
        System.out.println("Ștergere efectuată de admin pentru: " + username);

        long dupa = db.getCollection("users")
                .countDocuments(new Document("username", username));
        assertEquals(0, dupa);
        System.out.println("Verificare finală: clientul a fost șters complet.");
    }
}

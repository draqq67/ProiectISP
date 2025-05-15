package com.booking.service;

import com.booking.config.MongoConfig;
import com.booking.models.Client;
import com.booking.repository.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceLoginTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeAll
    public void setUp() {
        var db = MongoConfig.getDatabase();
        userRepository = new UserRepository(db);
        userService = new UserService(userRepository);

        // Ștergem orice user anterior cu username "testuser"
        db.getCollection("users").deleteMany(new org.bson.Document("username", "testuser"));

        // Cream un user de test
        Client client = new Client();
        client.setId(new ObjectId());
        client.setUsername("testuser");
        client.setParola("testpass");
        client.setNume("Popescu");
        client.setPrenume("Ion");
        client.setEmail("testuser@example.com");
        client.setSold(50f);
        client.setCarduri(new ArrayList<>());
        client.setRole("client");

        userRepository.save(client);
    }

    @Test
    public void testLoginWithUsername() {
        var user = userService.login("testuser", "testpass");
        assertNotNull(user, "User ar trebui să existe");
        assertEquals("Popescu", user.getNume());
    }

    @Test
    public void testLoginWithEmail() {
        var user = userService.login("testuser@example.com", "testpass");
        assertNotNull(user, "Login cu email ar trebui să meargă");
        assertEquals("Ion", user.getPrenume());
    }

    @Test
    public void testLoginWrongPassword() {
        var user = userService.login("testuser", "gresit123");
        assertNull(user, "Login cu parolă greșită trebuie să eșueze");
    }

    @Test
    public void testLoginUnknownUser() {
        var user = userService.login("unknown", "whatever");
        assertNull(user, "User necunoscut nu trebuie autentificat");
    }
}

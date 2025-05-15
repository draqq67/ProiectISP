package com.booking.service;

import com.booking.config.MongoConfig;
import com.booking.repository.UserRepository;
import org.bson.Document;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceRegisterTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeAll
    public void setup() {
        var db = MongoConfig.getDatabase();
        this.userRepository = new UserRepository(db);
        this.userService = new UserService(userRepository);

        // curățăm colecția users
        db.getCollection("users").deleteMany(new Document("username", new Document("$in", java.util.List.of(
                "registerTestUser", "registerDuplicateUser", "registerWithExistingEmail"
        ))));
        db.getCollection("users").deleteMany(new Document("email", "register@email.com"));
    }

    @Test
    public void testRegisterSuccess() {
        userService.register("registerTestUser", "parola123", "Test", "User", "register@email.com", "client");

        var user = userService.login("registerTestUser", "parola123");
        assertNotNull(user, "User ar trebui să existe după înregistrare.");
    }

    @Test
    public void testRegisterDuplicateUsername() {
        // înregistrare inițială
        userService.register("registerDuplicateUser", "pass", "Dup", "User", "email1@example.com", "client");

        // încercare duplicat
        userService.register("registerDuplicateUser", "pass", "Dup", "User", "email2@example.com", "client");

        long count = MongoConfig.getDatabase().getCollection("users")
                .countDocuments(new Document("username", "registerDuplicateUser"));

        assertEquals(1, count, "Nu trebuie să se înregistreze de două ori același username.");
    }

    @Test
    public void testRegisterDuplicateEmail() {
        // înregistrare inițială
        userService.register("user1", "pass", "A", "B", "email1@example.com", "manager");

        // duplicat pe email
        userService.register("user2", "pass", "X", "Y", "email1@example.com", "manager");

        long count = MongoConfig.getDatabase().getCollection("users")
                .countDocuments(new Document("email", "email1@example.com"));

        assertEquals(1, count, "Emailul nu trebuie să apară la mai mulți utilizatori.");
    }

    @Test
    public void testRegisterWithInvalidRole() {
        userService.register("badrole", "123", "Radu", "Ionut", "badrole@example.com", "visitor");

        var user = userService.login("badrole", "123");
        assertNull(user, "Nu trebuie să se creeze user cu rol invalid.");
    }
}

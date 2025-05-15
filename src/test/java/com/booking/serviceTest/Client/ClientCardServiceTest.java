package com.booking.serviceTest.Client;

import com.booking.config.MongoTestConfig;
import com.booking.models.Client;
import com.booking.repository.UserRepository;
import com.booking.service.ClientService;
import com.booking.service.UserService;
import org.bson.Document;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientCardServiceTest {

    private UserRepository userRepository;
    private UserService userService;
    private ClientService clientService;

    private Client testClient;

    @BeforeAll
    public void setup() {
        var db = MongoTestConfig.getDatabase();
        userRepository = new UserRepository(db);
        userService = new UserService(userRepository);
        clientService = new ClientService(userRepository);

        var existingUser = userService.login("testCardClient", "test123");

        if (existingUser == null) {
            userService.register("testCardClient", "test123", "Card", "Client", "card@test.com", "client");
            existingUser = userService.login("testCardClient", "test123");
        }

        assertNotNull(existingUser);
        assertTrue(existingUser instanceof Client);

        testClient = (Client) existingUser;
    }


    @Test
    public void testAdaugaMaiMulteCarduri() {
        String card1 = "5555666677778888";
        String card2 = "9999000011112222";

        clientService.adaugaCard(testClient, card1, "123");
        clientService.adaugaCard(testClient, card2, "321");

        long count = MongoTestConfig.getDatabase()
                .getCollection("users")
                .countDocuments(
                        new Document("_id", testClient.getId())
                                .append("carduri.numar", card2)
                                .append("carduri.cvv", "321")
                );

        assertEquals(1, count, "Al doilea card ar fi trebuit să fie adăugat.");
    }

    @Test
    public void testCardUnicGlobalIntreClienti() {
        // 1. Înregistrăm al doilea client dacă nu există
        var second = userService.login("altClient", "parola456");
        if (second == null) {
            userService.register("altClient", "parola456", "Alt", "Client", "alt@test.com", "client");
            second = userService.login("altClient", "parola456");
        }

        assertNotNull(second);
        assertTrue(second instanceof Client);
        Client client2 = (Client) second;

        // 2. Cardul test
        String numar = "7777888899990000";
        String cvv = "123";

        // 3. Clientul 1 adaugă cardul
        clientService.adaugaCard(testClient, numar, cvv);

        // 4. Clientul 2 încearcă să adauge același card
        clientService.adaugaCard(client2, numar, cvv);

        // 5. Verificăm că apare doar la primul client
        long countClient1 = MongoTestConfig.getDatabase()
                .getCollection("users")
                .countDocuments(new Document("_id", testClient.getId())
                        .append("carduri.numar", numar)
                        .append("carduri.cvv", cvv));

        long countClient2 = MongoTestConfig.getDatabase()
                .getCollection("users")
                .countDocuments(new Document("_id", client2.getId())
                        .append("carduri.numar", numar)
                        .append("carduri.cvv", cvv));

        assertEquals(1, countClient1, "Cardul ar trebui să fie salvat la primul client.");
        assertEquals(0, countClient2, "Cardul NU trebuie să se salveze la al doilea client.");
    }

    @Test
    public void testIncarcaSoldCuCardValid() {
        float suma = 200f;
        String numar = "1111222233334444";
        String cvv = "999";

        clientService.adaugaCard(testClient, numar, cvv);

        float soldInitial = testClient.getSold();

        clientService.incarcaSold(testClient, numar, cvv, suma);

        Document doc = MongoTestConfig.getDatabase()
                .getCollection("users")
                .find(new Document("_id", testClient.getId()))
                .first();

        assertNotNull(doc);
        float soldFinal = ((Double) doc.get("sold")).floatValue();

        assertEquals(soldInitial + suma, soldFinal, 0.01f);
    }
    //fac acelasi lucru cele 2 functii de adaugare sold
    @Test
    public void testAdaugaBaniCuCardPreset() {
        String numarPreset = "1111222233334444";
        String cvvPreset = "999";
        float suma = 150f;

        // Adăugăm cardul dacă nu există deja
        boolean cardExista = testClient.getCarduri().stream()
                .anyMatch(card -> card.getNumar().equals(numarPreset) && card.getCvv().equals(cvvPreset));

        if (!cardExista) {
            clientService.adaugaCard(testClient, numarPreset, cvvPreset);
        }

        float soldInitial = testClient.getSold();

        // Încărcăm soldul cu suma dorită
        clientService.incarcaSold(testClient, numarPreset, cvvPreset, suma);

        // Verificăm noul sold din MongoDB
        Document doc = MongoTestConfig.getDatabase()
                .getCollection("users")
                .find(new Document("_id", testClient.getId()))
                .first();

        assertNotNull(doc);
        float soldFinal = ((Double) doc.get("sold")).floatValue();

        assertEquals(soldInitial + suma, soldFinal, 0.01f, "Soldul ar fi trebuit să crească cu suma introdusă.");
    }

    @Test
    public void testStergereCardClient() {
        String numar = "7777888899990000";
        String cvv = "123";

        // 1. Adaugăm cardul la clientul test (dacă nu există deja)
        if (!userRepository.cardExistaGlobal(numar, cvv)) {
            clientService.adaugaCard(testClient, numar, cvv);
        }

        // 2. Confirmăm că există în Mongo
        long countBefore = MongoTestConfig.getDatabase()
                .getCollection("users")
                .countDocuments(new Document("_id", testClient.getId())
                        .append("carduri.numar", numar)
                        .append("carduri.cvv", cvv));
        assertEquals(1, countBefore, "Cardul ar trebui să fie prezent înainte de ștergere.");

        // 3. Ștergem cardul
        clientService.stergeCard(testClient, numar, cvv);

        // 4. Verificăm că a fost șters din Mongo
        long countAfter = MongoTestConfig.getDatabase()
                .getCollection("users")
                .countDocuments(new Document("_id", testClient.getId())
                        .append("carduri.numar", numar)
                        .append("carduri.cvv", cvv));
        assertEquals(0, countAfter, "Cardul ar trebui să fie șters din baza de date.");
    }

}

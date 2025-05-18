package com.booking.serviceTest.Client;

import com.booking.config.MongoTestConfig;
import com.booking.models.Client;
import com.booking.repository.UserRepository;
import com.booking.service.ClientService;
import com.booking.service.UserService;
import org.bson.Document;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/*
 Testele din acest fișier validează următoarele funcționalități:
 - Adăugarea mai multor carduri la același client
 - Unicitatea globală a cardurilor între clienți
 - Încărcarea soldului pe un card valid
 - Încărcarea soldului cu card presetat
 - Ștergerea cardului din contul unui client
*/

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
            System.out.println("Client nou înregistrat: testCardClient");
            existingUser = userService.login("testCardClient", "test123");
        } else {
            System.out.println("Client existent găsit: testCardClient");
        }

        assertNotNull(existingUser);
        assertTrue(existingUser instanceof Client);

        testClient = (Client) existingUser;
        System.out.println("Client autentificat pentru teste: " + testClient.getUsername());
    }

    @Test
    public void testAdaugaMaiMulteCarduri() {
        String card1 = "5555666677778888";
        String card2 = "9999000011112222";

        clientService.adaugaCard(testClient, card1, "123");
        System.out.println("Primul card adăugat: " + card1);

        clientService.adaugaCard(testClient, card2, "321");
        System.out.println("Al doilea card adăugat: " + card2);

        long count = MongoTestConfig.getDatabase()
                .getCollection("users")
                .countDocuments(
                        new Document("_id", testClient.getId())
                                .append("carduri.numar", card2)
                                .append("carduri.cvv", "321")
                );

        assertEquals(1, count, "Al doilea card ar fi trebuit să fie adăugat.");
        System.out.println("Verificare reușită: al doilea card salvat corect.");
    }

    @Test
    public void testCardUnicGlobalIntreClienti() {
        var second = userService.login("altClient", "parola456");
        if (second == null) {
            userService.register("altClient", "parola456", "Alt", "Client", "alt@test.com", "client");
            System.out.println("Client secundar înregistrat: altClient");
            second = userService.login("altClient", "parola456");
        }

        assertNotNull(second);
        assertTrue(second instanceof Client);
        Client client2 = (Client) second;

        String numar = "7777888899990000";
        String cvv = "123";

        clientService.adaugaCard(testClient, numar, cvv);
        System.out.println("Card adăugat la primul client.");

        clientService.adaugaCard(client2, numar, cvv);
        System.out.println("Încercare de adăugare același card la al doilea client.");

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
        System.out.println("Verificare reușită: unicitate card menținută între clienți.");
    }

    @Test
    public void testIncarcaSoldCuCardValid() {
        float suma = 200f;
        String numar = "1111222233334444";
        String cvv = "999";

        clientService.adaugaCard(testClient, numar, cvv);
        System.out.println("Card adăugat pentru încărcare sold.");

        float soldInitial = testClient.getSold();

        clientService.incarcaSold(testClient, numar, cvv, suma);
        System.out.println("Sold încărcat cu suma: " + suma);

        Document doc = MongoTestConfig.getDatabase()
                .getCollection("users")
                .find(new Document("_id", testClient.getId()))
                .first();

        assertNotNull(doc);
        float soldFinal = ((Double) doc.get("sold")).floatValue();

        assertEquals(soldInitial + suma, soldFinal, 0.01f);
        System.out.println("Verificare reușită: sold actualizat corect.");
    }

    @Test
    public void testAdaugaBaniCuCardPreset() {
        String numarPreset = "1111222233334444";
        String cvvPreset = "999";
        float suma = 150f;

        boolean cardExista = testClient.getCarduri().stream()
                .anyMatch(card -> card.getNumar().equals(numarPreset) && card.getCvv().equals(cvvPreset));

        if (!cardExista) {
            clientService.adaugaCard(testClient, numarPreset, cvvPreset);
            System.out.println("Card preset adăugat.");
        }

        float soldInitial = testClient.getSold();

        clientService.incarcaSold(testClient, numarPreset, cvvPreset, suma);
        System.out.println("Sold încărcat cu suma presetată: " + suma);

        Document doc = MongoTestConfig.getDatabase()
                .getCollection("users")
                .find(new Document("_id", testClient.getId()))
                .first();

        assertNotNull(doc);
        float soldFinal = ((Double) doc.get("sold")).floatValue();

        assertEquals(soldInitial + suma, soldFinal, 0.01f);
        System.out.println("Verificare reușită: soldul preset a fost actualizat.");
    }

    @Test
    public void testStergereCardClient() {
        String numar = "7777888899990000";
        String cvv = "123";

        if (!userRepository.cardExistaGlobal(numar, cvv)) {
            clientService.adaugaCard(testClient, numar, cvv);
            System.out.println("Card pentru ștergere adăugat.");
        }

        long countBefore = MongoTestConfig.getDatabase()
                .getCollection("users")
                .countDocuments(new Document("_id", testClient.getId())
                        .append("carduri.numar", numar)
                        .append("carduri.cvv", cvv));
        assertEquals(1, countBefore);

        clientService.stergeCard(testClient, numar, cvv);
        System.out.println("Card șters din contul clientului.");

        long countAfter = MongoTestConfig.getDatabase()
                .getCollection("users")
                .countDocuments(new Document("_id", testClient.getId())
                        .append("carduri.numar", numar)
                        .append("carduri.cvv", cvv));
        assertEquals(0, countAfter);
        System.out.println("Verificare reușită: cardul a fost eliminat din baza de date.");
    }
}

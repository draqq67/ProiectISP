package com.booking.serviceTest;

import com.booking.config.MongoConfig;
import com.booking.models.*;
import com.booking.repository.*;
import com.booking.service.ClientService;
import com.booking.service.PlataService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 Acest test verifică următorul flux complet:
 - Caută un client existent
 - Dacă nu are card, i se adaugă automat
 - Dacă nu are sold suficient, se încarcă automat
 - Se creează un booking NEPLATIT
 - Se plătește bookingul
 - Se verifică că bookingul este PLATIT
 - Se confirmă generarea unei facturi
*/


public class PlataTest {

    @Test
    public void testPlataBookingCuFixareAutomata() {
        var db = MongoConfig.getDatabase();

        var userRepo = new UserRepository(db);
        var bookingRepo = new BookingRepository(db);
        var facturaRepo = new FacturaRepository(db);
        var clientService = new ClientService(userRepo);
        var plataService = new PlataService(bookingRepo, userRepo, facturaRepo);

        System.out.println("Initializare repository-uri si servicii finalizata.");

        ObjectId clientId = new ObjectId("682771c1134d7e7bfeeb884f");
        ObjectId hotelId = new ObjectId("6828ce636ab7eb3c858745bd");
        ObjectId cameraId = new ObjectId("6828ce636ab7eb3c858745ba");

        Client client = (Client) userRepo.findById(clientId);
        assertNotNull(client);
        System.out.println("Client gasit: " + client.getUsername());

        if (client.getCarduri() == null || client.getCarduri().isEmpty()) {
            System.out.println("Nu are carduri. Se adauga unul nou.");
            clientService.adaugaCard(client, "1111222233334444", "999");
            client = (Client) userRepo.findById(clientId);
            System.out.println("Card adaugat cu succes.");
        } else {
            System.out.println("Clientul are deja un card: " + client.getCarduri().get(0).getNumar());
        }

        Card card = client.getCarduri().get(0);
        assertNotNull(card);

        float pretTotal = 350f;
        BookingItem item = new BookingItem(new ObjectId(), cameraId, 1, pretTotal);
        Booking booking = Booking.builder()
                .id(new ObjectId())
                .clientId(clientId)
                .hotelId(hotelId)
                .camereRezervate(List.of(item))
                .pretTotal(pretTotal)
                .dataCheckIn(new Date())
                .dataCheckOut(new Date(System.currentTimeMillis() + 86400000))
                .status(BookingStatus.NEPLATIT)
                .build();

        bookingRepo.save(booking);
        System.out.println("Booking NEPLATIT creat cu ID: " + booking.getId());

        if (client.getSold() < pretTotal) {
            float diferenta = pretTotal - client.getSold();
            System.out.println("Fonduri insuficiente. Se incarca " + diferenta + " RON.");
            clientService.incarcaSold(client, card.getNumar(), card.getCvv(), diferenta + 50);
            System.out.println("Sold incarcat.");
        } else {
            System.out.println("Clientul are suficient sold: " + client.getSold());
        }

        System.out.println("Se incepe plata bookingului...");
        plataService.platesteBooking(clientId, booking.getId());
        System.out.println("Plata efectuata cu succes.");

        Booking rezultat = bookingRepo.getBooking(booking.getId());
        assertEquals(BookingStatus.PLATIT, rezultat.getStatus());
        System.out.println("Bookingul este acum PLATIT.");

        List<Factura> facturi = facturaRepo.findByClientId(clientId);
        boolean facturaGasita = facturi.stream()
                .anyMatch(f -> f.getBooking().getId().equals(booking.getId()));
        assertTrue(facturaGasita);
        System.out.println("Factura a fost generata.");

        System.out.println("Test finalizat cu succes.");
    }
}



package com.booking.service;

import com.booking.models.*;
import com.booking.repository.BookingRepository;
import com.booking.repository.FacturaRepository;
import com.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

/*
 Clasa PlataService gestioneaza plata unei rezervari:
 - verifica daca rezervarea exista si daca este neplatita
 - verifica existenta si validitatea clientului
 - verifica daca soldul este suficient
 - scade suma din sold si actualizeaza statusul rezervarii
 - genereaza si salveaza o factura corespunzatoare
*/

@RequiredArgsConstructor
public class PlataService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FacturaRepository facturaRepository;

    public void platesteBooking(ObjectId clientId, ObjectId bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if (booking == null) {
            System.out.println("Bookingul nu există.");
            return;
        }

        if (booking.getStatus() != BookingStatus.NEPLATIT) {
            System.out.println("Rezervarea este deja plătită sau finalizată.");
            return;
        }

        User user = userRepository.findById(booking.getClientId());
        if (!(user instanceof Client client)) {
            System.out.println("Clientul nu a fost găsit sau nu este valid.");
            return;
        }

        float pretTotal = booking.getPretTotal();
        if (client.getSold() < pretTotal) {
            System.out.println("Fonduri insuficiente. Sold disponibil: " + client.getSold() + " RON. Total necesar: " + pretTotal + " RON.");
            return;
        }

        float nouSold = client.getSold() - pretTotal;
        userRepository.getCollection().updateOne(
                new Document("_id", client.getId()),
                new Document("$set", new Document("sold", nouSold))
        );
        System.out.println("Sold actualizat. Sold nou: " + nouSold + " RON.");

        booking.setStatus(BookingStatus.PLATIT);
        bookingRepository.update(booking);
        System.out.println("Statusul bookingului a fost actualizat la PLATIT.");

        Factura factura = Factura.builder()
                .id(new ObjectId())
                .clientId(client)
                .hotelId(new Hotel() {{
                    setId(booking.getHotelId());
                }})
                .rezervari(booking.getCamereRezervate())
                .total(pretTotal)
                .dateEmitere(new Date())
                .booking(booking)
                .build();

        facturaRepository.save(factura);
        System.out.println("Factură generată și salvată.");
    }
}

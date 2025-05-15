package com.booking.service;

import com.booking.models.Card;
import com.booking.models.Client;
import com.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.ArrayList;

@RequiredArgsConstructor
public class ClientService {

    private final UserRepository userRepository;

    public void adaugaCard(Client client, String numar, String cvv) {
        if (userRepository.cardExistaGlobal(numar, cvv)) {
            System.out.println("Acest card este deja asociat unui utilizator.");
            return;
        }

        Card card = new Card(numar, cvv, client.getId());

        client.getCarduri().add(card);

        userRepository.adaugaCard(client, card);

        System.out.println("✅ Card adăugat pentru " + client.getUsername());
    }


    public void afiseazaCarduri(Client client) {
        if (client.getCarduri().isEmpty()) {
            System.out.println("Nu există carduri.");
            return;
        }

        System.out.println("Carduri pentru " + client.getUsername() + ":");
        for (Card card : client.getCarduri()) {
            System.out.println("- " + card.getNumar() + " (CVV: " + card.getCvv() + ")");
        }
    }

    public void incarcaSold(Client client, String numarCard, String cvv, float suma) {
        // ✅ Verificare sumă
        if (suma <= 0) {
            System.out.println("⚠️ Suma trebuie să fie mai mare decât 0.");
            return;
        }

        // 🔍 Verificare card
        boolean cardValid = false;
        for (Card card : client.getCarduri()) {
            if (card.getNumar().equals(numarCard) && card.getCvv().equals(cvv)) {
                cardValid = true;
                break;
            }
        }

        if (!cardValid) {
            System.out.println("Card invalid. Nu se poate încărca soldul.");
            return;
        }

        // 💰 Update local și Mongo
        float nouSold = client.getSold() + suma;
        client.setSold(nouSold);

        Document query = new Document("_id", client.getId());
        Document update = new Document("$set", new Document("sold", nouSold));
        userRepository.getCollection().updateOne(query, update);

        System.out.println("Sold actualizat: " + nouSold + " RON pentru " + client.getUsername());
    }

    public void stergeCard(Client client, String numarCard, String cvv) {
        boolean cardGasit = client.getCarduri().stream()
                .anyMatch(card -> card.getNumar().equals(numarCard) && card.getCvv().equals(cvv));

        if (!cardGasit) {
            System.out.println("Cardul nu a fost găsit la client.");
            return;
        }

        // Eliminăm din MongoDB cu $pull
        Document query = new Document("_id", client.getId());

        Document conditieCard = new Document("numar", numarCard).append("cvv", cvv);
        Document update = new Document("$pull", new Document("carduri", conditieCard));

        userRepository.getCollection().updateOne(query, update);

        System.out.println("Card șters cu succes pentru " + client.getUsername());
    }


}

package com.booking.service;

import com.booking.models.Card;
import com.booking.models.Client;
import com.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.ArrayList;

/*
 Clasa ClientService permite:
 - adaugarea de carduri pentru un client
 - afisarea cardurilor existente
 - incarcarea soldului pe baza unui card valid
 - stergerea unui card din contul clientului
*/

@RequiredArgsConstructor
public class ClientService {

    private final UserRepository userRepository;

    public void adaugaCard(Client client, String numar, String cvv) {
        if (userRepository.cardExistaGlobal(numar, cvv)) {
            System.out.println("Acest card este deja asociat unui alt utilizator.");
            return;
        }

        Card card = new Card(numar, cvv, client.getId());
        client.getCarduri().add(card);
        userRepository.adaugaCard(client, card);

        System.out.println("Card adaugat pentru utilizatorul: " + client.getUsername());
    }

    public void afiseazaCarduri(Client client) {
        if (client.getCarduri().isEmpty()) {
            System.out.println("Utilizatorul nu are niciun card.");
            return;
        }

        System.out.println("Carduri existente pentru " + client.getUsername() + ":");
        for (Card card : client.getCarduri()) {
            System.out.println("- Numar: " + card.getNumar() + " | CVV: " + card.getCvv());
        }
    }

    public void incarcaSold(Client client, String numarCard, String cvv, float suma) {
        if (suma <= 0) {
            System.out.println("Suma trebuie sa fie mai mare decat 0.");
            return;
        }

        boolean cardValid = false;
        for (Card card : client.getCarduri()) {
            if (card.getNumar().equals(numarCard) && card.getCvv().equals(cvv)) {
                cardValid = true;
                break;
            }
        }

        if (!cardValid) {
            System.out.println("Card invalid. Nu se poate incarca soldul.");
            return;
        }

        float nouSold = client.getSold() + suma;
        client.setSold(nouSold);

        Document query = new Document("_id", client.getId());
        Document update = new Document("$set", new Document("sold", nouSold));
        userRepository.getCollection().updateOne(query, update);

        System.out.println("Sold incarcat. Valoare noua: " + nouSold + " RON pentru " + client.getUsername());
    }

    public void stergeCard(Client client, String numarCard, String cvv) {
        boolean cardGasit = client.getCarduri().stream()
                .anyMatch(card -> card.getNumar().equals(numarCard) && card.getCvv().equals(cvv));

        if (!cardGasit) {
            System.out.println("Cardul nu a fost gasit la client.");
            return;
        }

        Document query = new Document("_id", client.getId());
        Document conditieCard = new Document("numar", numarCard).append("cvv", cvv);
        Document update = new Document("$pull", new Document("carduri", conditieCard));

        userRepository.getCollection().updateOne(query, update);

        System.out.println("Card sters cu succes pentru " + client.getUsername());
    }
}

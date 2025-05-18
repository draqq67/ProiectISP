package com.booking.service;

import com.booking.models.*;
import com.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

/*
 Clasa UserService oferă funcționalități de:
 - înregistrare a utilizatorilor (client sau manager)
 - autentificare prin username/email și parolă
 Include mesaje informative pentru fiecare pas important.
*/


@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void register(String username, String parola, String nume, String prenume, String email, String role) {
        System.out.println("Apel: înregistrare utilizator");

        if (userRepository.existsByUsernameOrEmail(username, email)) {
            System.out.println("Eroare: utilizatorul sau emailul există deja în sistem.");
            return;
        }

        User user = null;

        switch (role.toLowerCase()) {
            case "client" -> {
                System.out.println("Creare utilizator de tip client...");
                Client client = new Client();
                client.setUsername(username);
                client.setParola(parola);
                client.setNume(nume);
                client.setPrenume(prenume);
                client.setEmail(email);
                client.setSold(0f);
                client.setCarduri(new ArrayList<>());
                client.setRole("client");
                user = client;
            }
            case "manager" -> {
                System.out.println("Creare utilizator de tip manager...");
                Manager manager = new Manager();
                manager.setUsername(username);
                manager.setParola(parola);
                manager.setNume(nume);
                manager.setPrenume(prenume);
                manager.setEmail(email);
                manager.setHotelIds(new ArrayList<>());
                manager.setRole("manager");
                user = manager;
            }
            default -> {
                System.out.println("Rol invalid specificat: " + role);
                return;
            }
        }

        System.out.println("Salvare utilizator în baza de date...");
        userRepository.save(user);
        System.out.println("Utilizator înregistrat cu succes: " + username);
    }

    public User login(String input, String parola) {
        User user = userRepository.findByUsernameOrEmailAndPassword(input, parola);
        if (user == null) {
            System.out.println("Autentificare eșuată: nume de utilizator/email sau parolă incorectă.");
        } else {
            System.out.println("Autentificare reușită: " + user.getNume() + " " + user.getPrenume());
        }
        return user;
    }
}

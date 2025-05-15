package com.booking.service;

import com.booking.models.*;
import com.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void register(String username, String parola, String nume, String prenume, String email, String role) {
        // verificăm dacă username sau email există deja
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            System.out.println("Eroare: Username sau email deja existent în sistem.");
            return;
        }

        User user = null;

        switch (role.toLowerCase()) {
            case "client" -> {
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
                System.out.println("Rol invalid. Folosește doar 'client' sau 'manager'.");
                return;
            }
        }

        userRepository.save(user);
        System.out.println("Utilizator înregistrat cu succes ca " + role + "!");
    }


    public User login(String input, String parola) {
        User user = userRepository.findByUsernameOrEmailAndPassword(input, parola);
        if (user == null) {
            System.out.println("Autentificare eșuată: utilizator inexistent sau parolă greșită.");
        } else {
            System.out.println("Autentificare reușită! Bine ai venit, " + user.getNume());
        }
        return user;
    }

}
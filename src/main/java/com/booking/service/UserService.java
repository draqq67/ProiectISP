package com.booking.service;

import com.booking.models.*;
import com.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void register(String username, String parola, String nume, String prenume, String email, String role) {
        System.out.println("➡️ register() apelată");

        if (userRepository.existsByUsernameOrEmail(username, email)) {
            System.out.println("⚠️ EXISTĂ deja user/email");
            return;
        }

        User user = null;
        switch (role.toLowerCase()) {
            case "client" -> {
                System.out.println("🔄 Creăm client...");
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
                System.out.println("🔄 Creăm manager...");
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
                System.out.println("❌ Rol invalid: " + role);
                return;
            }
        }

        System.out.println("📦 Apelăm save()...");
        userRepository.save(user);
        System.out.println("✅ Utilizator salvat: " + username);
    }



    public User login(String input, String parola) {
        User user = userRepository.findByUsernameOrEmailAndPassword(input, parola);
        if (user == null) {
            System.out.println("Autentificare eșuată: utilizator inexistent sau parolă greșită.");
        } else {
            System.out.println("Autentificare reușită! Bine ai venit, " + user.getNume() + user.getPrenume());
        }
        return user;
    }

}
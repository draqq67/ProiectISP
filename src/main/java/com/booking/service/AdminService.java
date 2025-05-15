package com.booking.service;

import com.booking.models.Client;
import com.booking.repository.UserRepository;
import com.booking.models.User;

public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void stergeClient(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            System.out.println("Utilizatorul " + username + " nu există.");
            return;
        }

        if (!(user instanceof Client client)) {
            System.out.println("Utilizatorul nu este de tip client.");
            return;
        }

        // 🧹 Ștergem documentul complet din colecția users
        userRepository.deleteById(client.getId());

        System.out.println("Client șters împreună cu toate cardurile sale: " + username);
    }

}

package com.booking.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client extends User {
    private Float sold;
    private String role = "client";
    private List<Card> carduri = new ArrayList<>();

    public Client(String username, String parola, String nume, String prenume, String email) {
        super(null, username, parola, nume, prenume, email);
        this.sold = 0f;
        this.role = "client";
        this.carduri = new ArrayList<>();
    }

}

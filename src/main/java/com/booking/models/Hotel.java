package com.booking.models;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    private ObjectId id;
    private String nume;
    private String locatie;
    private ObjectId managerId; // referință către Manager
    private List<InventarCamera> camere = new ArrayList<>();
}

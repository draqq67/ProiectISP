package com.booking.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camera {
    private ObjectId id;
    private String denumire;
    private int capacitate;
    private float pretPeNoapte;
    private Set<LocalDate> dateIndisponibile = new HashSet<>();
}

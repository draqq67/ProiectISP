package com.booking.models;

import lombok.*;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private String numar;
    private String cvv;
    private ObjectId detinator; // referință către Client
}

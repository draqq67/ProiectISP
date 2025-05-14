package com.booking.models;

import lombok.*;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
    protected ObjectId id;
    protected String username;
    protected String parola;
    protected String nume;
    protected String prenume;
    protected String email;


}

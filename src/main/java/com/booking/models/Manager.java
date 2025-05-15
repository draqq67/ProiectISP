package com.booking.models;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manager extends User {
    private String role = "manager";
    private List<ObjectId> hotelIds;
}

package com.booking.models;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User {
    private String role = "admin";
}

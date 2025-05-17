package com.booking.models;

import lombok.AllArgsConstructor;
import lombok.*;
import org.bson.types.ObjectId;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Factura {
    private ObjectId id;
    private Client clientId;
    private Hotel hotelId;
    private List<BookingItem> rezervari;
    private float total;
    private Date dateEmitere;
    private Booking booking;
}

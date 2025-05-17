package com.booking.models;
import lombok.*;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking   {
    private ObjectId id;
    private ObjectId clientId;
    private ObjectId hotelId;
    private List<BookingItem> camereRezervate;
    private float pretTotal;
    private Date dataCheckIn;
    private Date dataCheckOut;
    private BookingStatus status;
}

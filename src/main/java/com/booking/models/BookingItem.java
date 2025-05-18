package com.booking.models;

import org.bson.types.ObjectId;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingItem {
    private ObjectId id;
    private ObjectId cameraId;
    private int cantitate;
    private float pretTotal;
}

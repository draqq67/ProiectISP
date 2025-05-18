package com.booking.models;

import lombok.*;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarCamera {
    private ObjectId hotelId;
    private ObjectId cameraId;
    private int numarCamere;
}

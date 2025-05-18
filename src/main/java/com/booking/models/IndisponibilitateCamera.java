package com.booking.models;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndisponibilitateCamera {
    private ObjectId id;
    private ObjectId cameraId;
    private LocalDate dataStart;
    private LocalDate dataEnd;
}

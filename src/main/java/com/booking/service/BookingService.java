package com.booking.service;

import com.booking.models.IndisponibilitateCamera;
import com.booking.repository.IndisponibilitateCameraRepository;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;

public class BookingService {

    private final IndisponibilitateCameraRepository indisponibilitateCameraRepository;

    public BookingService(IndisponibilitateCameraRepository indisponibilitateCameraRepository) {
        this.indisponibilitateCameraRepository = indisponibilitateCameraRepository;
    }

    /**
     * Cauta camerele disponibile si le marcheaza ca ocupate
     *
     * @param startDate      Data de inceput
     * @param endDate        Data de sfarsit
     * @param allCameraIds   Lista completa a camerelor
     * @return               Lista camerelor ocupate
     */
    public List<ObjectId> rezervaCamereDisponibile(LocalDate startDate, LocalDate endDate, List<ObjectId> allCameraIds) {

        List<ObjectId> disponibile = indisponibilitateCameraRepository
                .findAvailableCameraIds(startDate, endDate, allCameraIds);

        for (ObjectId cameraId : disponibile) {
            IndisponibilitateCamera rezervare = new IndisponibilitateCamera();
            rezervare.setId(new ObjectId());
            rezervare.setCameraId(cameraId);
            rezervare.setDataStart(startDate);
            rezervare.setDataEnd(endDate);

            indisponibilitateCameraRepository.save(rezervare);
        }
        if (disponibile.isEmpty()) {
            System.out.println(" Nu exista camere disponibile in acea perioada.");
        }
        return disponibile;
    }
}

package com.booking.service;

import com.booking.config.MongoConfig;
import com.booking.models.Camera;
import com.booking.repository.CameraRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CameraTest {

    private CameraRepository cameraRepository;

    @BeforeAll
    public void setup() {
        var db = MongoConfig.getDatabase();
        db.getCollection("cameras").deleteMany(new org.bson.Document());
        cameraRepository = new CameraRepository(db);
    }

    @Test
    public void testSaveAndFindCamera() {
        Camera camera = new Camera(null, "Camera Test", 2, 200f, Set.of(LocalDate.of(2025, 5, 20)));
        cameraRepository.save(camera);

        List<Camera> camere = cameraRepository.findAll();
        assertFalse(camere.isEmpty(), "Camera nu a fost salvată");

        Camera gasita = camere.get(0);
        assertEquals("Camera Test", gasita.getDenumire());
        assertTrue(gasita.getDateIndisponibile().contains(LocalDate.of(2025, 5, 20)));

        cameraRepository.deleteById(gasita.getId());
    }

    @Test
    public void testUpdateCamera() {
        Camera camera = new Camera(null, "Standard", 2, 150f, Set.of());
        cameraRepository.save(camera);

        Camera salvata = cameraRepository.findAll().get(0);
        salvata.setPretPeNoapte(180f);
        salvata.setDenumire("Standard Plus");
        cameraRepository.update(salvata);

        Camera actualizata = cameraRepository.findById(salvata.getId());
        assertEquals("Standard Plus", actualizata.getDenumire());
        assertEquals(180f, actualizata.getPretPeNoapte());

        cameraRepository.deleteById(actualizata.getId());
    }

    @Test
    public void testDeleteCamera() {
        Camera camera = new Camera(null, "De Șters", 1, 100f, Set.of());
        cameraRepository.save(camera);

        Camera salvata = cameraRepository.findAll().get(0);
        cameraRepository.deleteById(salvata.getId());

        Camera cautata = cameraRepository.findById(salvata.getId());
        assertNull(cautata, "Camera ar fi trebuit ștearsă");
    }

    @Test
    public void testFindByIdInexistent() {
        Camera camera = cameraRepository.findById(new ObjectId());
        assertNull(camera, "Camera nu ar trebui să fie găsit pentru un ID inexistent");
    }
    
}

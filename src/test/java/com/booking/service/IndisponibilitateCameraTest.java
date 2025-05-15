import com.booking.config.MongoConfig;
import com.booking.models.IndisponibilitateCamera;
import com.booking.repository.IndisponibilitateCameraRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IndisponibilitateCameraTest {

    private IndisponibilitateCameraRepository repository;

    @BeforeAll
    public void setup() {
        var db = MongoConfig.getDatabase();
        db.getCollection("indisponibilitateCamera").deleteMany(new org.bson.Document());
        repository = new IndisponibilitateCameraRepository(db);
    }

    @Test
    public void testSaveAndFindByCameraId() {
        ObjectId cameraId = new ObjectId();
        IndisponibilitateCamera indisponibilitate = new IndisponibilitateCamera(null, cameraId, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5));
        repository.save(indisponibilitate);

        List<IndisponibilitateCamera> rezultate = repository.findByCameraId(cameraId);
        assertFalse(rezultate.isEmpty());
        assertEquals(LocalDate.of(2025, 6, 1), rezultate.get(0).getDataStart());

        repository.deleteById(rezultate.get(0).getId());
    }

    @Test
    public void testUpdateIndisponibilitate() {
        ObjectId cameraId = new ObjectId();
        IndisponibilitateCamera indisponibilitate = new IndisponibilitateCamera(null, cameraId, LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 12));
        repository.save(indisponibilitate);

        IndisponibilitateCamera salvata = repository.findByCameraId(cameraId).get(0);
        salvata.setDataEnd(LocalDate.of(2025, 6, 15));
        repository.update(salvata);

        IndisponibilitateCamera actualizata = repository.findByCameraId(cameraId).get(0);
        assertEquals(LocalDate.of(2025, 6, 15), actualizata.getDataEnd());

        repository.deleteById(salvata.getId());
    }

    @Test
    public void testDeleteIndisponibilitate() {
        ObjectId cameraId = new ObjectId();
        IndisponibilitateCamera indisponibilitate = new IndisponibilitateCamera(null, cameraId, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 3));
        repository.save(indisponibilitate);

        IndisponibilitateCamera salvata = repository.findByCameraId(cameraId).get(0);
        repository.deleteById(salvata.getId());

        List<IndisponibilitateCamera> rezultate = repository.findByCameraId(cameraId);
        assertTrue(rezultate.isEmpty());
    }

    @Test
    public void testFindAll() {
        ObjectId cameraId = new ObjectId();
        IndisponibilitateCamera indisponibilitate1 = new IndisponibilitateCamera(null, cameraId, LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 3));
        IndisponibilitateCamera indisponibilitate2 = new IndisponibilitateCamera(null, cameraId, LocalDate.of(2025, 8, 4), LocalDate.of(2025, 8, 6));
        repository.save(indisponibilitate1);
        repository.save(indisponibilitate2);

        List<IndisponibilitateCamera> toate = repository.findAll();
        assertFalse(toate.isEmpty());
        assertEquals(2, toate.size());

        repository.deleteById(indisponibilitate1.getId());
        repository.deleteById(indisponibilitate2.getId());
    }
}
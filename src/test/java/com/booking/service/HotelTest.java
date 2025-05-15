package com.booking.service;

import com.booking.config.MongoConfig;
import com.booking.models.Hotel;
import com.booking.repository.HotelRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HotelTest {

    private HotelRepository hotelRepository;

    @BeforeAll
    public void setup() {
        var db = MongoConfig.getDatabase();
        db.getCollection("hotels").deleteMany(new org.bson.Document()); // curățare colecție
        hotelRepository = new HotelRepository(db);
    }

    @Test
    public void testSaveAndFindHotel() {
        ObjectId managerId = new ObjectId();
        Hotel hotel = new Hotel();
        hotel.setNume("Hotel Test");
        hotel.setLocatie("Bucuresti");
        hotel.setManagerId(managerId);
        hotel.setCamere(List.of());

        hotelRepository.save(hotel);

        List<Hotel> hoteluri = hotelRepository.findByManagerId(managerId);
        assertFalse(hoteluri.isEmpty(), "Hotelul nu a fost salvat corect");

        Hotel gasit = hoteluri.get(0);
        assertEquals("Hotel Test", gasit.getNume());
        assertEquals("Bucuresti", gasit.getLocatie());
        assertEquals(managerId, gasit.getManagerId());

        // Cleanup
        hotelRepository.deleteById(gasit.getId());
    }

    @Test
    public void testUpdateHotel() {
        ObjectId managerId = new ObjectId();
        Hotel hotel = new Hotel();
        hotel.setNume("Hotel Test");
        hotel.setLocatie("Bucuresti");
        hotel.setManagerId(managerId);
        hotel.setCamere(List.of());

        hotelRepository.save(hotel);

        List<Hotel> hoteluri = hotelRepository.findByManagerId(managerId);
        assertFalse(hoteluri.isEmpty(), "Hotelul nu a fost salvat corect");

        Hotel gasit = hoteluri.get(0);
        gasit.setNume("Hotel Test Actualizat");
        hotelRepository.update(gasit);

        List<Hotel> hoteluriActualizate = hotelRepository.findByManagerId(managerId);
        Hotel gasitActualizat = hoteluriActualizate.get(0);
        assertEquals("Hotel Test Actualizat", gasitActualizat.getNume());

        // Cleanup
        hotelRepository.deleteById(gasit.getId());
    }

    @Test
    public void testDeleteHotel() {
        ObjectId managerId = new ObjectId();
        Hotel hotel = new Hotel();
        hotel.setNume("Hotel Test");
        hotel.setLocatie("Bucuresti");
        hotel.setManagerId(managerId);
        hotel.setCamere(List.of());

        hotelRepository.save(hotel);

        List<Hotel> hoteluri = hotelRepository.findByManagerId(managerId);
        assertFalse(hoteluri.isEmpty(), "Hotelul nu a fost salvat corect");

        Hotel gasit = hoteluri.get(0);
        hotelRepository.deleteById(gasit.getId());

        List<Hotel> hoteluriDupaStergere = hotelRepository.findByManagerId(managerId);
        assertTrue(hoteluriDupaStergere.isEmpty(), "Hotelul nu a fost șters corect");
    }


        @Test
    public void testFindByIdInexistent() {
        Hotel hotel = hotelRepository.findById(new ObjectId());
        assertNull(hotel, "Hotelul nu ar trebui să fie găsit pentru un ID inexistent");
    }

    @Test
    public void testFindAllHotels() {
        ObjectId managerId = new ObjectId();

        Hotel h1 = new Hotel(null, "Hotel A", "Cluj", managerId, List.of());
        Hotel h2 = new Hotel(null, "Hotel B", "Iasi", managerId, List.of());

        hotelRepository.save(h1);
        hotelRepository.save(h2);

        List<Hotel> toateHotelurile = hotelRepository.findAll();
        assertTrue(toateHotelurile.size() >= 2, "Trebuie să existe cel puțin două hoteluri");

        // Cleanup
        for (Hotel h : hotelRepository.findByManagerId(managerId)) {
            hotelRepository.deleteById(h.getId());
        }
    }

    @Test
    public void testUpdateHotelInexistent() {
        Hotel inexistent = new Hotel(new ObjectId(), "Ghost Hotel", "Nicaieri", new ObjectId(), List.of());
        hotelRepository.update(inexistent);

        Hotel rezultat = hotelRepository.findById(inexistent.getId());
        assertNull(rezultat, "Hotelul nu ar trebui să fie creat de update dacă nu există");
    }

    @Test
    public void testDeleteByIdInexistent() {
        // Nu ar trebui să arunce excepție
        assertDoesNotThrow(() -> hotelRepository.deleteById(new ObjectId()));
    }
}
    
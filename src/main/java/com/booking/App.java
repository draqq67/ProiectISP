package com.booking;

import com.booking.model.*;
import com.booking.repository.HotelRepository;
import com.booking.repository.RezervareRepository;
import com.booking.repository.UtilizatorRepository;

import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Create Utilizator
        UtilizatorRepository utilizatorRepo = new UtilizatorRepository();
        Utilizator utilizator = new Utilizator("Ion", "Popescu", "ion.popescu@email.com", Rol.CLIENT);
        utilizatorRepo.adaugaUtilizator(utilizator);

        // Create Hotel
        HotelRepository hotelRepo = new HotelRepository();
        List<Servicii> hotelServicii = Arrays.asList(Servicii.WI_FI, Servicii.GYM, Servicii.GRATAR);
        Hotel hotel = new Hotel("Double Room", hotelServicii, 50, "partener123");
        hotelRepo.adaugaHotel(hotel);

        // Create Rezervare
        RezervareRepository rezervareRepo = new RezervareRepository();
        List<Servicii> serviciiRezervare = Arrays.asList(Servicii.WI_FI, Servicii.GYM);
        Rezervare rezervare = new Rezervare();
        rezervare.setClientId(utilizator.getMail());
        rezervare.setHotelId(hotel.getPartenerId());
        rezervare.setCamera(hotel.getTipCamera());
        rezervare.setServicii(serviciiRezervare);
        rezervare.setData("2025-05-10");
        rezervareRepo.adaugaRezervare(rezervare);

        // Retrieve Utilizator
        List<Utilizator> utilizatori = utilizatorRepo.obtineUtilizatoriCuRol(Rol.CLIENT);
        System.out.println("Utilizatori: ");
        utilizatori.forEach(u -> System.out.println(u.getNume() + " " + u.getPrenume()));

        // Retrieve Hotel
        List<Hotel> hoteluri = hotelRepo.obtineHoteluriCuPartenerId("partener123");
        System.out.println("\nHoteluri: ");
        hoteluri.forEach(h -> System.out.println(h.getTipCamera() + " - Servicii: " + h.getListaServicii()));

        // Retrieve Rezervare
        List<Rezervare> rezervari = rezervareRepo.obtineRezervariPentruClient(utilizator.getMail());
        System.out.println("\nRezervari pentru " + utilizator.getMail() + ": ");
        rezervari.forEach(r -> System.out.println("Hotel: " + r.getHotelId() + ", Camera: " + r.getCamera() + ", Servicii: " + r.getServicii()));
    }
}

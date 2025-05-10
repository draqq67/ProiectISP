package com.booking.model;

import java.util.List;

public class Rezervare {
    private String clientId;
    private String hotelId;
    private String camera;
    private List<Servicii> servicii;
    private String data;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public List<Servicii> getServicii() {
        return servicii;
    }

    public void setServicii(List<Servicii> servicii) {
        this.servicii = servicii;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

package com.booking.model;

import java.util.List;

public class Hotel {
    private String tipCamera;
    private List<Servicii> listaServicii; // Using Servicii enum
    private Integer nrCamere;
    private String partenerId;

    public Hotel() {
    }

    public Hotel(String tipCamera, List<Servicii> listaServicii, Integer nrCamere, String partenerId) {
        this.tipCamera = tipCamera;
        this.listaServicii = listaServicii;
        this.nrCamere = nrCamere;
        this.partenerId = partenerId;
    }

    public String getTipCamera() {
        return tipCamera;
    }

    public void setTipCamera(String tipCamera) {
        this.tipCamera = tipCamera;
    }

    public List<Servicii> getListaServicii() {
        return listaServicii;
    }

    public void setListaServicii(List<Servicii> listaServicii) {
        this.listaServicii = listaServicii;
    }

    public Integer getNrCamere() {
        return nrCamere;
    }

    public void setNrCamere(Integer nrCamere) {
        this.nrCamere = nrCamere;
    }

    public String getPartenerId() {
        return partenerId;
    }

    public void setPartenerId(String partenerId) {
        this.partenerId = partenerId;
    }
}

package com.booking.model;

public class Utilizator {
    private String nume;
    private String prenume;
    private String mail;
    private Rol rol;

    public Utilizator() {}

    public Utilizator(String nume, String prenume, String mail, Rol rol) {
        this.nume = nume;
        this.prenume = prenume;
        this.mail = mail;
        this.rol = rol;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}

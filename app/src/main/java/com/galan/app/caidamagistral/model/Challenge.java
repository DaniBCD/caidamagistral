package com.galan.app.caidamagistral.model;

public class Challenge {

    private String nombre, dificultad;
    private int total, estrellas;

    public Challenge() {
    }

    public Challenge(String nombre, String dificultad, int total, int estrellas) {
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.total = total;
        this.estrellas = estrellas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "nombre='" + nombre + '\'' +
                ", dificultad='" + dificultad + '\'' +
                ", total=" + total +
                ", estrellas=" + estrellas +
                '}';
    }
}

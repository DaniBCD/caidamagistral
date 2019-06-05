package com.galan.app.caidamagistral.model;

public class ShopItem {

    private String nombre, precio, imagen;
    private int numeroVeces;

    public ShopItem() {
    }

    public ShopItem(String nombre, String precio, String imagen, int numeroVeces) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.numeroVeces = numeroVeces;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getNumeroVeces() {
        return numeroVeces;
    }

    public void setNumeroVeces(int numeroVeces) {
        this.numeroVeces = numeroVeces;
    }
}

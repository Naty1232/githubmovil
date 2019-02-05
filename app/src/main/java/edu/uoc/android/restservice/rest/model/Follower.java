package edu.uoc.android.restservice.rest.model;

public class Follower {

    // Esta clase se creo para un mejor entendimiento y seleccion de los seguidores de los usuarios

    String nombre;
    String url;

    public Follower(String nombre, String url) {
        this.nombre = nombre;
        this.url = url;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrl() {
        return url;
    }
}

package com.rpg.model;

public class Jugador {
    private final int id;
    private final String usuario;
    private final String rol;

    public Jugador(int id, String usuario, String rol) {
        this.id = id;
        this.usuario = usuario;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getRol() {
        return rol;
    }

    public boolean esAdmin() {
        return "admin".equals(rol);
    }
}
package com.rpg.controller;

public class Resultado<T> {
    private final boolean ok;
    private final String mensaje;
    private final T dato;

    private Resultado(boolean ok, String mensaje, T dato) {
        this.ok = ok;
        this.mensaje = mensaje;
        this.dato = dato;
    }

    public static <T> Resultado<T> exito(String mensaje, T dato) {
        return new Resultado<>(true, mensaje, dato);
    }

    public static <T> Resultado<T> error(String mensaje) {
        return new Resultado<>(false, mensaje, null);
    }

    public boolean isOk() { return ok; }
    public String getMensaje() { return mensaje; }
    public T getDato() { return dato; }
}
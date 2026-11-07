package com.rpg.dao;

public class DaoException extends RuntimeException {
    public DaoException(String mensaje) {
        super(mensaje);
    }

    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
package com.rpg.controller;

import com.rpg.dao.DaoException;
import com.rpg.dao.JugadorDAO;
import com.rpg.model.Jugador;

public class LoginController {

    private static final int MAX_INTENTOS = 3;

    private final JugadorDAO dao = new JugadorDAO();
    private int intentos = 0;

    public boolean estaBloqueado() {
        return intentos >= MAX_INTENTOS;
    }

    public Resultado<Jugador> iniciarSesion(String usuario, String clave) {
        if (estaBloqueado()) {
            return Resultado.error("Sesion bloqueada por demasiados intentos.");
        }
        if (usuario == null || usuario.isBlank() || clave == null || clave.isBlank()) {
            return Resultado.error("Usuario y clave son obligatorios.");
        }
        try {
            Jugador jugador = dao.autenticar(usuario.trim(), clave);
            if (jugador == null) {
                intentos++;
                if (estaBloqueado()) {
                    return Resultado.error("Credenciales incorrectas. Sesion bloqueada.");
                }
                return Resultado.error("Credenciales incorrectas. Intentos restantes: " + (MAX_INTENTOS - intentos));
            }
            intentos = 0;
            return Resultado.exito("Bienvenido, " + jugador.getUsuario() + " (" + jugador.getRol() + ").", jugador);
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }

    public Resultado<Void> registrar(String usuario, String clave) {
        if (usuario == null || usuario.trim().length() < 3) {
            return Resultado.error("El usuario debe tener al menos 3 caracteres.");
        }
        if (clave == null || clave.length() < 4) {
            return Resultado.error("La clave debe tener al menos 4 caracteres.");
        }
        try {
            dao.registrar(usuario.trim(), clave);
            return Resultado.exito("Jugador registrado. Ya puedes iniciar sesion.", null);
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }
}
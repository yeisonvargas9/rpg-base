package com.rpg.controller;

import com.rpg.dao.DaoException;
import com.rpg.dao.MonstruoDAO;
import com.rpg.model.Jugador;
import com.rpg.model.Monstruo;

import java.util.List;

public class MonstruoController {

    private final MonstruoDAO dao = new MonstruoDAO();
    private final Jugador sesion;

    public MonstruoController(Jugador sesion) {
        this.sesion = sesion;
    }

    public Resultado<List<Monstruo>> listar() {
        try {
            return Resultado.exito(null, dao.listar());
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }

    public Resultado<List<Monstruo>> buscar(String nombre) {
        try {
            return Resultado.exito(null, dao.buscarPorNombre(nombre.trim()));
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }

    public Resultado<List<Monstruo>> porVidaMinima(int vidaMinima) {
        try {
            return Resultado.exito(null, dao.listarPorVidaMinima(vidaMinima));
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }

    public Resultado<Void> crear(String nombre, int vida, int ataque) {
        Resultado<Void> rechazo = exigirAdmin();
        if (rechazo != null) {
            return rechazo;
        }
        String problema = validar(nombre, vida, ataque);
        if (problema != null) {
            return Resultado.error(problema);
        }
        try {
            dao.crear(nombre.trim(), vida, ataque);
            return Resultado.exito("Monstruo creado.", null);
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }

    public Resultado<Void> editar(int id, String nombre, int vida, int ataque) {
        Resultado<Void> rechazo = exigirAdmin();
        if (rechazo != null) {
            return rechazo;
        }
        String problema = validar(nombre, vida, ataque);
        if (problema != null) {
            return Resultado.error(problema);
        }
        try {
            if (dao.actualizar(id, nombre.trim(), vida, ataque)) {
                return Resultado.exito("Monstruo actualizado.", null);
            }
            return Resultado.error("No existe un monstruo con ese id.");
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }

    public Resultado<Void> eliminar(int id) {
        Resultado<Void> rechazo = exigirAdmin();
        if (rechazo != null) {
            return rechazo;
        }
        try {
            if (dao.obtenerPorId(id) == null) {
                return Resultado.error("No existe un monstruo con ese id.");
            }
            if (dao.tieneVictorias(id)) {
                return Resultado.error("No se puede eliminar: el monstruo tiene victorias registradas.");
            }
            dao.eliminar(id);
            return Resultado.exito("Monstruo eliminado.", null);
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }

    private Resultado<Void> exigirAdmin() {
        return sesion.esAdmin() ? null : Resultado.error("Solo un administrador puede hacer esto.");
    }

    private String validar(String nombre, int vida, int ataque) {
        if (nombre == null || nombre.isBlank()) {
            return "El nombre no puede estar vacio.";
        }
        if (vida <= 0 || ataque <= 0) {
            return "La vida y el ataque deben ser mayores que 0.";
        }
        return null;
    }
}
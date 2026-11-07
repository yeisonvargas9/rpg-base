package com.rpg.controller;

import com.rpg.dao.DaoException;
import com.rpg.dao.MonstruoDAO;
import com.rpg.dao.VictoriaDAO;
import com.rpg.model.Jugador;
import com.rpg.model.Monstruo;

public class JuegoController {

    private final Jugador sesion;
    private final MonstruoDAO monstruos = new MonstruoDAO();
    private final VictoriaDAO victorias = new VictoriaDAO();

    public JuegoController(Jugador sesion) {
        this.sesion = sesion;
    }

    public Resultado<Void> registrarVictoria(String nombreMonstruo) {
        try {
            Monstruo monstruo = monstruos.obtenerPorNombre(nombreMonstruo);
            if (monstruo == null) {
                return Resultado.error("'" + nombreMonstruo + "' no esta en la tabla monstruos: victoria no registrada.");
            }
            victorias.registrar(sesion.getId(), monstruo.getId());
            return Resultado.exito("Victoria registrada en la base de datos.", null);
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }
}
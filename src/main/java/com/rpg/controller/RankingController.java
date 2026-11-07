package com.rpg.controller;

import com.rpg.dao.DaoException;
import com.rpg.dao.VictoriaDAO;
import com.rpg.model.RankingFila;
import com.rpg.model.VictoriaDetalle;

import java.util.List;

public class RankingController {

    private final VictoriaDAO dao = new VictoriaDAO();

    public Resultado<List<RankingFila>> ranking() {
        try {
            return Resultado.exito(null, dao.rankingPorJugador());
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }

    public Resultado<List<VictoriaDetalle>> victoriasDe(String usuario) {
        try {
            return Resultado.exito(null, dao.victoriasDeJugador(usuario));
        } catch (DaoException e) {
            return Resultado.error(e.getMessage());
        }
    }
}
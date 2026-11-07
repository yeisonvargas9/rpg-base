package com.rpg.dao;

import com.rpg.model.RankingFila;
import com.rpg.model.VictoriaDetalle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VictoriaDAO {

    public void registrar(int jugadorId, int monstruoId) {
        String sql = "INSERT INTO victorias (jugador_id, monstruo_id, fecha) VALUES (?, ?, NOW())";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jugadorId);
            ps.setInt(2, monstruoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al registrar la victoria: " + e.getMessage(), e);
        }
    }

    /** Usuario y total de victorias, de mayor a menor. */
    public List<RankingFila> rankingPorJugador() {
        String sql = "SELECT j.usuario, COUNT(v.id) AS total "
                + "FROM victorias v "
                + "JOIN jugadores j ON j.id = v.jugador_id "
                + "GROUP BY j.id, j.usuario "
                + "ORDER BY total DESC, j.usuario";
        List<RankingFila> ranking = new ArrayList<>();
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ranking.add(new RankingFila(rs.getString("usuario"), rs.getInt("total")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar el ranking: " + e.getMessage(), e);
        }
        return ranking;
    }

    /** Monstruo y fecha de cada victoria del jugador, la mas reciente primero. */
    public List<VictoriaDetalle> victoriasDeJugador(String usuario) {
        String sql = "SELECT m.nombre, v.fecha "
                + "FROM victorias v "
                + "JOIN jugadores j ON j.id = v.jugador_id "
                + "JOIN monstruos m ON m.id = v.monstruo_id "
                + "WHERE j.usuario = ? "
                + "ORDER BY v.fecha DESC";
        List<VictoriaDetalle> lista = new ArrayList<>();
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new VictoriaDetalle(rs.getString("nombre"),
                            rs.getTimestamp("fecha").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar las victorias: " + e.getMessage(), e);
        }
        return lista;
    }
}
package com.rpg.dao;

import com.rpg.model.Jugador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class JugadorDAO {

    /** Registra un jugador nuevo con rol "jugador". */
    public void registrar(String usuario, String clave) {
        String sql = "INSERT INTO jugadores (usuario, clave, rol) VALUES (?, ?, 'jugador')";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, clave);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DaoException("Ya existe un jugador con el usuario '" + usuario + "'", e);
        } catch (SQLException e) {
            throw new DaoException("Error al registrar el jugador: " + e.getMessage(), e);
        }
    }

    /** @return el Jugador si usuario y clave son correctos, o null si no */
    public Jugador autenticar(String usuario, String clave) {
        String sql = "SELECT id, usuario, rol FROM jugadores WHERE usuario = ? AND clave = ?";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Jugador(rs.getInt("id"), rs.getString("usuario"), rs.getString("rol"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al autenticar: " + e.getMessage(), e);
        }
    }

    /** @return true si se cambio el rol, false si no existe ese id */
    public boolean cambiarRol(int id, String rol) {
        String sql = "UPDATE jugadores SET rol = ? WHERE id = ?";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rol);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al cambiar el rol: " + e.getMessage(), e);
        }
    }

    /** @return true si se elimino, false si no existe ese id */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM jugadores WHERE id = ?";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el jugador: " + e.getMessage(), e);
        }
    }
}
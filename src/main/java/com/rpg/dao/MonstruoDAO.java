package com.rpg.dao;

import com.rpg.model.Monstruo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class MonstruoDAO {

    public void crear(String nombre, int vida, int ataque) {
        String sql = "INSERT INTO monstruos (nombre, vida, ataque) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, vida);
            ps.setInt(3, ataque);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DaoException("Ya existe un monstruo llamado '" + nombre + "'", e);
        } catch (SQLException e) {
            throw new DaoException("Error al crear el monstruo: " + e.getMessage(), e);
        }
    }

    public List<Monstruo> listar() {
        return consultarLista("SELECT id, nombre, vida, ataque FROM monstruos ORDER BY id", null, null);
    }

    /** @return el monstruo, o null si no existe */
    public Monstruo obtenerPorId(int id) {
        String sql = "SELECT id, nombre, vida, ataque FROM monstruos WHERE id = ?";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el monstruo: " + e.getMessage(), e);
        }
    }
    /** @return el monstruo con ese nombre exacto, o null si no existe */
    public Monstruo obtenerPorNombre(String nombre) {
        String sql = "SELECT id, nombre, vida, ataque FROM monstruos WHERE nombre = ?";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el monstruo: " + e.getMessage(), e);
        }
    }
    /** @return true si se actualizo, false si no existe ese id */
    public boolean actualizar(int id, String nombre, int vida, int ataque) {
        String sql = "UPDATE monstruos SET nombre = ?, vida = ?, ataque = ? WHERE id = ?";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, vida);
            ps.setInt(3, ataque);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DaoException("Ya existe otro monstruo llamado '" + nombre + "'", e);
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el monstruo: " + e.getMessage(), e);
        }
    }

    /** @return true si se elimino, false si no existe ese id */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM monstruos WHERE id = ?";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el monstruo: " + e.getMessage(), e);
        }
    }

    /** Busca por coincidencia parcial del nombre (ignora mayusculas). */
    public List<Monstruo> buscarPorNombre(String nombre) {
        return consultarLista(
                "SELECT id, nombre, vida, ataque FROM monstruos WHERE nombre LIKE ? ORDER BY nombre",
                "%" + nombre + "%", null);
    }

    public List<Monstruo> listarPorVidaMinima(int vidaMinima) {
        return consultarLista(
                "SELECT id, nombre, vida, ataque FROM monstruos WHERE vida >= ? ORDER BY vida",
                null, vidaMinima);
    }

    /** Lo usa el Controller para no eliminar un monstruo que ya tiene victorias. */
    public boolean tieneVictorias(int id) {
        String sql = "SELECT COUNT(*) FROM victorias WHERE monstruo_id = ?";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al revisar las victorias del monstruo: " + e.getMessage(), e);
        }
    }

    // ---- auxiliares

    /** Ejecuta un SELECT con a lo sumo un parametro de texto o uno entero. */
    private List<Monstruo> consultarLista(String sql, String texto, Integer numero) {
        List<Monstruo> lista = new ArrayList<>();
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (texto != null) {
                ps.setString(1, texto);
            }
            if (numero != null) {
                ps.setInt(1, numero);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar monstruos: " + e.getMessage(), e);
        }
        return lista;
    }

    private Monstruo mapear(ResultSet rs) throws SQLException {
        return new Monstruo(rs.getInt("id"), rs.getString("nombre"), rs.getInt("vida"), rs.getInt("ataque"));
    }
}
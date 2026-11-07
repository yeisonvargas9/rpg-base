package com.rpg.demo;

import com.rpg.dao.ConexionDB;
import com.rpg.dao.JugadorDAO;
import com.rpg.model.Jugador;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.PreparedStatement;

/** SOLO con fines didacticos: login vulnerable a inyeccion SQL. No usar en codigo real. */
public class LoginInseguroDemo {

    /** Concatena el texto del usuario dentro del SQL (esto es lo peligroso). */
    public static boolean validarInseguro(String usuario, String clave) {
        String sql = "SELECT * FROM jugadores WHERE usuario = '" + usuario
                + "' AND clave = '" + clave + "'";
        System.out.println("SQL generado: " + sql);
        try (Connection con = ConexionDB.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
            return false;
        }
    }
    /** Mide 1000 inserciones con Statement y 1000 con PreparedStatement. */
    public static void medirInserciones() {
        final int total = 1000;
        try (Connection con = ConexionDB.obtener()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS prueba_tiempos (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(50) NOT NULL)")) {
                ps.executeUpdate();
            }

            long inicio = System.nanoTime();
            try (Statement st = con.createStatement()) {
                for (int i = 0; i < total; i++) {
                    st.executeUpdate("INSERT INTO prueba_tiempos (nombre) VALUES ('s" + i + "')");
                }
            }
            long msStatement = (System.nanoTime() - inicio) / 1_000_000;

            inicio = System.nanoTime();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO prueba_tiempos (nombre) VALUES (?)")) {
                for (int i = 0; i < total; i++) {
                    ps.setString(1, "p" + i);
                    ps.executeUpdate();
                }
            }
            long msPrepared = (System.nanoTime() - inicio) / 1_000_000;

            try (PreparedStatement ps = con.prepareStatement("DROP TABLE prueba_tiempos")) {
                ps.executeUpdate();
            }
            System.out.println("Statement:         " + msStatement + " ms");
            System.out.println("PreparedStatement: " + msPrepared + " ms");
        } catch (SQLException e) {
            System.out.println("Error en la medicion: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String usuario = "admin' OR 1=1 -- ";   // ojo: hay un espacio despues de --
        String clave = "cualquiera";

        System.out.println("=== LOGIN INSEGURO (Statement) ===");
        boolean entro = validarInseguro(usuario, clave);
        System.out.println("Resultado: " + (entro ? "ENTRO SIN CONOCER LA CLAVE" : "rechazado"));

        System.out.println("\n=== LOGIN SEGURO (PreparedStatement) ===");
        Jugador jugador = new JugadorDAO().autenticar(usuario, clave);
        System.out.println("Resultado: " + (jugador != null ? "ENTRO" : "rechazado"));
    }
}
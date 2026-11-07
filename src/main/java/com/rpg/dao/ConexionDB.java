package com.rpg.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/*
 * URL: jdbc:mysql://localhost:3306/rpg_arena_db
 *   jdbc:mysql  -> protocolo: JDBC con el driver de MySQL
 *   localhost   -> host: el equipo donde corre el servidor MySQL
 *   3306        -> puerto: donde escucha MySQL por defecto
 *   rpg_arena_db -> esquema: la base de datos a la que se conecta

 * JDBC (Java Database Connectivity) es la API estandar de Java para hablar con bases de datos.
 * DriverManager es la clase que, dada una URL (jdbc:mysql://...), busca entre los drivers cargados
 * el que sabe hablar con esa base de datos y abre la conexion.
 * Si falta el driver (mysql-connector-j), la conexion falla. Error al quitar la dependencia:
 * "No se pudo conectar a la base de datos: No suitable driver found for jdbc:mysql://localhost:3306/rpg_arena_db".
 */
public class ConexionDB {

    private static final String ARCHIVO_CONFIG = "config.txt";

    private ConexionDB() {
    }

    public static Connection obtener() {
        Map<String, String> config = leerConfig();
        try {
            return DriverManager.getConnection(config.get("url"), config.get("usuario"), config.get("clave"));
        } catch (SQLException e) {
            throw new DaoException("No se pudo conectar a la base de datos: " + e.getMessage(), e);
        }
    }

    private static Map<String, String> leerConfig() {
        File archivo = new File(ARCHIVO_CONFIG);
        if (!archivo.exists()) {
            throw new DaoException("No se encontro config.txt en la raiz del proyecto. "
                    + "Copia config.example.txt como config.txt y completalo.");
        }
        Map<String, String> datos = new HashMap<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                String[] partes = linea.split("=", 2);
                if (partes.length == 2) {
                    datos.put(partes[0].trim(), partes[1].trim());
                }
            }
        } catch (FileNotFoundException e) {
            throw new DaoException("No se pudo abrir config.txt: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DaoException("Error al leer config.txt: " + e.getMessage(), e);
        }
        for (String clave : new String[]{"url", "usuario", "clave"}) {
            if (!datos.containsKey(clave)) {
                throw new DaoException("Falta la clave '" + clave + "' en config.txt");
            }
        }
        return datos;
    }
}
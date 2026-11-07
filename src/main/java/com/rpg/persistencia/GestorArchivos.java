package com.rpg.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/*
 * RUTAS: una ruta relativa ("partidas/log_combate.txt") se resuelve desde la carpeta donde se ejecuta el
 * programa; una absoluta (getAbsolutePath()) empieza desde la raiz del disco y no depende de donde se ejecute.
 *
 * INVESTIGACION 1 - FileWriter vs Files.writeString:
 * FileWriter (con BufferedWriter) es un flujo con buffer que yo abro, voy escribiendo linea a linea y cierro
 * (try-with-resources). Files.writeString (NIO) escribe todo el texto en una sola llamada y se encarga de
 * abrir y cerrar. Uso el flujo cuando escribo muchas lineas poco a poco o en modo append; uso writeString
 * cuando ya tengo todo el texto en un String y quiero algo corto.
 *
 * File.delete() vs Files.deleteIfExists(): delete() solo devuelve false si falla (sin decir por que);
 * deleteIfExists() devuelve false si no existia y lanza IOException con la causa real si no pudo borrarlo.
 *
 * INVESTIGACION 2 - Seguridad de Serializable:
 * No se debe llamar a readObject con datos que no son de confianza. Un archivo manipulado puede construir
 * objetos inesperados al reconstruirlos (cadenas de "gadgets" que ejecutan codigo) o consumir memoria y CPU
 * con estructuras enormes. Alternativas: guardar en JSON o en un formato de texto propio y validar cada campo
 * al leerlo, en vez de reconstruir objetos arbitrarios.
 *
 * SYNCHRONIZED: el hilo daemon de auto-guardado y el hilo principal comparten estos archivos, asi que toda
 * lectura o escritura entra por el mismo objeto candado.
 */
public final class GestorArchivos {

    private static final String CARPETA = "partidas";
    public static final File LOG = new File(CARPETA, "log_combate.txt");
    public static final File PARTIDA = new File(CARPETA, "partida.dat");
    private static final File TEMPORAL = new File(CARPETA, "partida.dat.tmp");
    private static final Object CANDADO = new Object();

    private GestorArchivos() {
    }

    // ---- File

    public static void prepararCarpeta() {
        File carpeta = new File(CARPETA);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            System.out.println("No se pudo crear la carpeta " + CARPETA);
        }
    }

    public static void mostrarInfo(File archivo) {
        System.out.println("Archivo: " + archivo.getName());
        System.out.println("Ruta relativa: " + archivo.getPath());
        System.out.println("Ruta absoluta: " + archivo.getAbsolutePath());
        System.out.println("Existe: " + archivo.exists() + " | Tamano: " + archivo.length() + " bytes");
    }

    // ---- log de texto (BufferedWriter / BufferedReader)

    /** Agrega el texto al final del log (append). Con new FileWriter(LOG) sin el true se sobrescribiria. */
    public static void agregarLog(String texto) {
        synchronized (CANDADO) {
            prepararCarpeta();
            try {
                if (!LOG.exists()) {
                    LOG.createNewFile();
                }
            } catch (IOException e) {
                System.out.println("No se pudo crear el archivo de log: " + e.getMessage());
                return;
            }
            try (BufferedWriter escritor = new BufferedWriter(new FileWriter(LOG, true))) {
                for (String linea : texto.split("\\R")) {
                    escritor.write(linea);
                    escritor.newLine();
                }
                escritor.write("----------");
                escritor.newLine();
                escritor.flush();
            } catch (IOException e) {
                System.out.println("No se pudo escribir el log de combate: " + e.getMessage());
            }
        }
    }

    public static List<String> leerLog() {
        List<String> lineas = new ArrayList<>();
        synchronized (CANDADO) {
            try (BufferedReader lector = new BufferedReader(new FileReader(LOG))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    lineas.add(linea);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Aun no hay historial de combates guardado.");
            } catch (IOException e) {
                System.out.println("No se pudo leer el historial: " + e.getMessage());
            }
        }
        return lineas;
    }

    // ---- borrado

    public static boolean borrarLog() {
        synchronized (CANDADO) {
            return LOG.delete();
        }
    }

    public static boolean borrarPartida() {
        synchronized (CANDADO) {
            try {
                return Files.deleteIfExists(PARTIDA.toPath());
            } catch (IOException e) {
                System.out.println("No se pudo borrar la partida: " + e.getMessage());
                return false;
            }
        }
    }

    // ---- serializacion

    /** Escribe primero en un temporal y luego lo renombra: si se cierra el juego a medias, el .dat no queda roto. */
    public static void guardarPartida(PartidaGuardada partida) {
        synchronized (CANDADO) {
            prepararCarpeta();
            try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(TEMPORAL))) {
                salida.writeObject(partida);
            } catch (NotSerializableException e) {
                System.out.println("No se pudo guardar: la clase " + e.getMessage() + " no es Serializable.");
                return;
            } catch (IOException e) {
                System.out.println("No se pudo guardar la partida: " + e.getMessage());
                return;
            }
            try {
                Files.move(TEMPORAL.toPath(), PARTIDA.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("No se pudo terminar de guardar la partida: " + e.getMessage());
            }
        }
    }

    /** @return la partida, o null si no existe, esta corrupta o no se puede leer */
    public static PartidaGuardada cargarPartida() {
        synchronized (CANDADO) {
            try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(PARTIDA))) {
                return (PartidaGuardada) entrada.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("No hay ninguna partida guardada.");
            } catch (ClassNotFoundException e) {
                System.out.println("La partida guardada es de una version distinta del juego: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("La partida guardada esta danada o no se puede leer: " + e.getMessage());
            }
            return null;
        }
    }
}
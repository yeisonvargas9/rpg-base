package com.rpg.concurrency;

import com.rpg.entities.Guerrero;
import com.rpg.entities.Personaje;

/** Pruebas de la seccion 6 y de la investigacion obligatoria. Se ejecuta aparte del juego. */
public class PruebasConcurrencia {

    private static final int REPETICIONES = 1_000_000;

    /** Vida SIN proteccion: lee, calcula y escribe en pasos separados (como el Personaje original). */
    private static class VidaSinProteger {
        int vida = 2 * REPETICIONES;

        void recibirDanio(int cantidad) {
            vida = vida - cantidad;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        demostrarCarreraSinProteger();
        demostrarPersonajeSincronizado();
        demostrarDaemon();
    }

    // ---- 1. Condicion de carrera

    private static void demostrarCarreraSinProteger() throws InterruptedException {
        VidaSinProteger objeto = new VidaSinProteger();
        Runnable tarea = () -> {
            for (int i = 0; i < REPETICIONES; i++) {
                objeto.recibirDanio(1);
            }
        };
        Thread a = new Thread(tarea, "veneno");
        Thread b = new Thread(tarea, "ataque");
        a.start();
        b.start();
        a.join();
        b.join();
        System.out.println("SIN proteger -> vida final: " + objeto.vida + " (esperado: 0)");
    }

    // ---- 2. Misma prueba con el Personaje real (synchronized)

    private static void demostrarPersonajeSincronizado() throws InterruptedException {
        Personaje heroe = new Guerrero("Prueba");
        heroe.setVidaMaxima(2 * REPETICIONES);
        heroe.setVida(2 * REPETICIONES);
        Runnable tarea = () -> {
            for (int i = 0; i < REPETICIONES; i++) {
                heroe.recibirDanio(1);
            }
        };
        Thread a = new Thread(tarea, "veneno");
        Thread b = new Thread(tarea, "ataque");
        a.start();
        b.start();
        a.join();
        b.join();
        System.out.println("CON synchronized -> vida final: " + heroe.getVida() + " (esperado: 0)");
    }

    /*
     * Por que synchronized y no AtomicInteger.addAndGet(-danio):
     * setVida() no solo resta. Acota la vida entre 0 y vidaMaxima y ademas cambia el estado a MUERTO.
     * Son varias variables que deben cambiar juntas, y un AtomicInteger solo protege UNA variable.
     * Con addAndGet la vida podria quedar negativa o el estado no coincidir con la vida.
     * synchronized hace que toda la operacion (leer, calcular, acotar, cambiar estado) sea una sola unidad.
     */

    // ---- 3. Hilo daemon

    /*
     * Hilo de usuario: la JVM no termina mientras alguno siga vivo.
     * Hilo daemon: es de servicio (como el auto-guardado); la JVM termina cuando solo quedan daemons.
     */
    private static void demostrarDaemon() {
        Thread daemon = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "daemon-prueba");
        daemon.setDaemon(true);
        daemon.start();

        try {
            daemon.setDaemon(false); // ya hizo start(): debe fallar
        } catch (IllegalThreadStateException e) {
            System.out.println("setDaemon despues de start() lanza: " + e.getClass().getSimpleName());
        }
        System.out.println("Fin de main con el daemon aun vivo: la JVM debe cerrar de inmediato.");
    }
}
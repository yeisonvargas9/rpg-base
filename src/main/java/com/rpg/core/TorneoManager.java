package com.rpg.core;

import com.rpg.entities.Escuadron;
import com.rpg.entities.Guerrero;
import com.rpg.entities.Mago;
import com.rpg.entities.Personaje;

import java.util.List;

/** Torneo de 4 gladiadores: dos semifinales en paralelo y una final. */
public class TorneoManager {

    private final Arena arena;
    private final RegistroCombate registro;

    public TorneoManager(Arena arena, RegistroCombate registro) {
        this.arena = arena;
        this.registro = registro;
    }

    /** @return el campeon del torneo */
    public Personaje jugarTorneo() {
        List<Personaje> gladiadores = crearGladiadores().getMiembros();

        // Aqui guardamos los ganadores de cada semifinal (cada hilo escribe en su propia posicion)
        Personaje[] finalistas = new Personaje[2];

        // Un Thread por semifinal, cada uno con su Runnable (lambda)
        Thread semifinalA = new Thread(
                () -> finalistas[0] = simularSemifinal(gladiadores.get(0), gladiadores.get(1)),
                "semifinal-A");
        Thread semifinalB = new Thread(
                () -> finalistas[1] = simularSemifinal(gladiadores.get(2), gladiadores.get(3)),
                "semifinal-B");

        // Estado NEW: el hilo existe pero aun no ha arrancado
        System.out.println("[Antes de start] A=" + semifinalA.getState() + " | B=" + semifinalB.getState());

        // start() crea el hilo real; las dos semifinales corren al mismo tiempo
        semifinalA.start();
        semifinalB.start();

        // Normalmente RUNNABLE (o ya TERMINATED si termino muy rapido)
        System.out.println("[Durante] A=" + semifinalA.getState() + " | B=" + semifinalB.getState());

        // join(): el hilo principal espera a que cada semifinal termine
        try {
            semifinalA.join();
            semifinalB.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("El torneo fue interrumpido", e);
        }

        // Estado TERMINATED: ya acabaron
        System.out.println("[Despues del join] A=" + semifinalA.getState() + " | B=" + semifinalB.getState());

        // Solo despues de ambos join() es seguro leer los ganadores
        return simularFinal(finalistas[0], finalistas[1]);
    }

    public Personaje simularSemifinal(Personaje a, Personaje b) {
        registro.agregar("=== SEMIFINAL: %s vs %s ===", a.getNombre(), b.getNombre());
        Personaje ganador = arena.duelo(a, b);
        registro.agregar("Clasifica a la final: %s", ganador.getNombre());
        return ganador;
    }

    private Personaje simularFinal(Personaje a, Personaje b) {
        a.restaurarVida();
        b.restaurarVida();
        registro.agregar("=== FINAL: %s vs %s ===", a.getNombre(), b.getNombre());
        return arena.duelo(a, b);
    }

    private Escuadron crearGladiadores() {
        Escuadron escuadron = new Escuadron("Gladiadores");
        escuadron.agregar(new Guerrero("Maximo"));
        escuadron.agregar(new Mago("Circe"));
        escuadron.agregar(new Guerrero("Aquiles"));
        escuadron.agregar(new Mago("Merlina"));
        return escuadron;
    }
}
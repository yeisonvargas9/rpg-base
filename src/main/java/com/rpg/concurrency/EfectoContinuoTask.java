package com.rpg.concurrency;

import com.rpg.entities.EstadoEfecto;
import com.rpg.entities.Personaje;

public class EfectoContinuoTask implements Runnable {
    private final Personaje objetivo;
    private final EstadoEfecto efecto;
    private final long intervaloMs;

    public EfectoContinuoTask(Personaje objetivo, EstadoEfecto efecto, long intervaloMs) {
        this.objetivo = objetivo;
        this.efecto = efecto;
        this.intervaloMs = intervaloMs;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && !efecto.haTerminado() && objetivo.estaVivo()) {
                try {
                    Thread.sleep(intervaloMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                aplicarTick();
                efecto.consumirTurno();
            }
        } finally {
            objetivo.limpiarEfecto(efecto.getNombre());
        }
    }

    private void aplicarTick() {
        if (efecto.esDanio()) {
            objetivo.recibirDanio(efecto.getValorPorTick());
            System.out.printf("%s sufre %d de dano por %s%n",
                    objetivo.getNombre(), efecto.getValorPorTick(), efecto.getNombre());
        } else {
            int curado = objetivo.curar(efecto.getValorPorTick());
            System.out.printf("%s recupera %d de vida por %s%n",
                    objetivo.getNombre(), curado, efecto.getNombre());
        }
    }
}
package com.rpg.concurrency;

public class AutoGuardadoDaemon implements Runnable {
    private final Runnable guardado;
    private final long intervaloMs;

    public AutoGuardadoDaemon(Runnable guardado, long intervaloMs) {
        this.guardado = guardado;
        this.intervaloMs = intervaloMs;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(intervaloMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            guardado.run();
        }
    }
}
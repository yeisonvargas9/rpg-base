package com.rpg.view;

import com.rpg.controller.RankingController;
import com.rpg.controller.Resultado;
import com.rpg.model.Jugador;
import com.rpg.model.RankingFila;
import com.rpg.model.VictoriaDetalle;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RankingUI {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final RankingController controller;
    private final Jugador jugador;

    public RankingUI(RankingController controller, Jugador jugador) {
        this.controller = controller;
        this.jugador = jugador;
    }

    public void mostrar() {
        Resultado<List<RankingFila>> ranking = controller.ranking();
        System.out.println("\n=== RANKING DE VICTORIAS ===");
        if (!ranking.isOk()) {
            System.out.println(ranking.getMensaje());
            return;
        }
        if (ranking.getDato().isEmpty()) {
            System.out.println("Aun no hay victorias registradas.");
        }
        int puesto = 1;
        for (RankingFila fila : ranking.getDato()) {
            System.out.printf("%d. %s - %d victorias%n", puesto++, fila.getUsuario(), fila.getTotalVictorias());
        }

        System.out.println("\n--- Tus victorias (" + jugador.getUsuario() + ") ---");
        Resultado<List<VictoriaDetalle>> propias = controller.victoriasDe(jugador.getUsuario());
        if (!propias.isOk()) {
            System.out.println(propias.getMensaje());
            return;
        }
        for (VictoriaDetalle v : propias.getDato()) {
            System.out.printf("%s - %s%n", v.getMonstruo(), v.getFecha().format(FORMATO));
        }
    }
}
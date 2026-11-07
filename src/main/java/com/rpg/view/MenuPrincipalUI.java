package com.rpg.view;

import com.rpg.controller.JuegoController;
import com.rpg.controller.MonstruoController;
import com.rpg.controller.RankingController;
import com.rpg.core.Consola;
import com.rpg.core.MotorJuego;
import com.rpg.model.Jugador;

public class MenuPrincipalUI {

    private final Consola consola;
    private final MotorJuego motor;
    private final MonstruoUI monstruoUI;
    private final RankingUI rankingUI;

    public MenuPrincipalUI(Consola consola, Jugador jugador) {
        this.consola = consola;
        this.motor = new MotorJuego(consola);
        JuegoController juego = new JuegoController(jugador);
        this.motor.setAlVencer(nombre -> System.out.println(juego.registrarVictoria(nombre).getMensaje()));
        this.monstruoUI = new MonstruoUI(consola, new MonstruoController(jugador));
        this.rankingUI = new RankingUI(new RankingController(), jugador);
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Combate rapido  2. Torneo  3. Bestiario del juego");
            System.out.println("4. Monstruos (BD)  5. Ranking  0. Cerrar sesion");
            opcion = consola.leerEntero("Opcion: ", 0, 5);
            switch (opcion) {
                case 1 -> motor.combateRapido();
                case 2 -> motor.jugarTorneo();
                case 3 -> motor.mostrarBestiario();
                case 4 -> monstruoUI.mostrar();
                case 5 -> rankingUI.mostrar();
                default -> { }
            }
        } while (opcion != 0);
    }
}
package com.rpg.view;

import com.rpg.controller.MonstruoController;
import com.rpg.controller.Resultado;
import com.rpg.core.Consola;
import com.rpg.model.Monstruo;

import java.util.List;

public class MonstruoUI {

    private final Consola consola;
    private final MonstruoController controller;

    public MonstruoUI(Consola consola, MonstruoController controller) {
        this.consola = consola;
        this.controller = controller;
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n=== MONSTRUOS (BD) ===");
            System.out.println("1. Listar  2. Buscar por nombre  3. Listar por vida minima");
            System.out.println("4. Crear  5. Editar  6. Eliminar  0. Volver");
            opcion = consola.leerEntero("Opcion: ", 0, 6);
            switch (opcion) {
                case 1 -> imprimir(controller.listar());
                case 2 -> imprimir(controller.buscar(consola.leerLinea("Nombre (o parte): ")));
                case 3 -> imprimir(controller.porVidaMinima(consola.leerEntero("Vida minima: ", 0, 100000)));
                case 4 -> crear();
                case 5 -> editar();
                case 6 -> eliminar();
                default -> { }
            }
        } while (opcion != 0);
    }

    private void crear() {
        String nombre = consola.leerLinea("Nombre: ");
        int vida = consola.leerEntero("Vida: ", 1, 100000);
        int ataque = consola.leerEntero("Ataque: ", 1, 10000);
        System.out.println(controller.crear(nombre, vida, ataque).getMensaje());
    }

    private void editar() {
        int id = consola.leerEntero("Id del monstruo: ", 1, Integer.MAX_VALUE);
        String nombre = consola.leerLinea("Nuevo nombre: ");
        int vida = consola.leerEntero("Nueva vida: ", 1, 100000);
        int ataque = consola.leerEntero("Nuevo ataque: ", 1, 10000);
        System.out.println(controller.editar(id, nombre, vida, ataque).getMensaje());
    }

    private void eliminar() {
        int id = consola.leerEntero("Id del monstruo a eliminar: ", 1, Integer.MAX_VALUE);
        System.out.println(controller.eliminar(id).getMensaje());
    }

    private void imprimir(Resultado<List<Monstruo>> resultado) {
        if (!resultado.isOk()) {
            System.out.println(resultado.getMensaje());
        } else if (resultado.getDato().isEmpty()) {
            System.out.println("Sin resultados.");
        } else {
            resultado.getDato().forEach(System.out::println);
        }
    }
}
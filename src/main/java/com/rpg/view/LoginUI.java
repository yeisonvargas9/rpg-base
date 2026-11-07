package com.rpg.view;

import com.rpg.controller.LoginController;
import com.rpg.controller.Resultado;
import com.rpg.core.Consola;
import com.rpg.model.Jugador;

import java.util.NoSuchElementException;   // <- import nuevo (6.2)
import java.util.Scanner;                  // <- import nuevo (6.2)

public class LoginUI {

    private final Consola consola;
    private final LoginController controller = new LoginController();

    public LoginUI(Consola consola) {
        this.consola = consola;
    }

    /** Punto de entrada: crea el Scanner y la Consola, y arranca el login. */
    public static void arrancar() {          // <- metodo nuevo (6.3)
        try (Scanner scanner = new Scanner(System.in)) {
            new LoginUI(new Consola(scanner)).iniciar();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada finalizada. Hasta luego.");
        }
    }

    public void iniciar() {
        System.out.println("=== ARENA SEGURA ===");
        while (!controller.estaBloqueado()) {
            System.out.println("\n1. Iniciar sesion  2. Registrarse  0. Salir");
            int opcion = consola.leerEntero("Opcion: ", 0, 2);
            if (opcion == 0) {
                return;
            }
            String usuario = consola.leerLinea("Usuario: ");
            String clave = consola.leerLinea("Clave: ");
            if (opcion == 2) {
                System.out.println(controller.registrar(usuario, clave).getMensaje());
                continue;
            }
            Resultado<Jugador> resultado = controller.iniciarSesion(usuario, clave);
            System.out.println(resultado.getMensaje());
            if (resultado.isOk()) {
                new MenuPrincipalUI(consola, resultado.getDato()).mostrar();
            }
        }
        System.out.println("Sesion bloqueada. Cierra el programa y vuelve a intentarlo.");
    }
}
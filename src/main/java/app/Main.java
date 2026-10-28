package app;

import exception.PersistenciaException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicação.
 */
public class Main {
    public static void main(String[] args) {
        Entrada entrada = new Entrada(new Scanner(System.in));

        MenuPrincipal menu;
        try {
            menu = ConfiguracaoAplicacao.criarMenu(entrada);
        } catch (PersistenciaException | IllegalStateException e) {
            System.out.println("[Erro de dados] " + e.getMessage());
            System.out.println("A aplicação será encerrada sem alterar os dados existentes.");
            return;
        }

        try {
            menu.abrir();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada. Até logo!");
        }
    }
}

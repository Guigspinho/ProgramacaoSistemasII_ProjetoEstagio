package app;

import exception.PersistenciaException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import service.jdbc.ServicosJDBC;

/**
 * Ponto de entrada. Usa os serviços JDBC conectados ao Supabase.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Entrada entrada = new Entrada(scanner);

        final MenuPrincipal menu;
        try {
            ServicosJDBC servicos = new ServicosJDBC();
            menu = new MenuPrincipal(entrada, servicos, servicos, servicos, servicos, servicos);
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

package app;

import exception.PersistenciaException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import service.jdbc.ServicosJDBC;
import service.stub.ServicosEmMemoria;

/**
 * Ponto de entrada. Usa Supabase quando as variáveis de conexão estão configuradas;
 * sem configuração, mantém os serviços em memória para execução local.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Entrada entrada = new Entrada(scanner);

        boolean configuracaoBancoInformada = variavelConfigurada("SUPABASE_JDBC_URL")
                || variavelConfigurada("SUPABASE_DB_USER")
                || variavelConfigurada("SUPABASE_DB_PASSWORD");
        final MenuPrincipal menu;
        try {
            if (configuracaoBancoInformada) {
                ServicosJDBC servicos = new ServicosJDBC();
                menu = new MenuPrincipal(entrada, servicos, servicos, servicos, servicos, servicos);
            } else {
                ServicosEmMemoria servicos = new ServicosEmMemoria();
                menu = new MenuPrincipal(entrada, servicos, servicos, servicos, servicos, servicos);
                System.out.println("Banco Supabase não configurado; usando dados temporários em memória.");
            }
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

    private static boolean variavelConfigurada(String nome) {
        String valor = System.getenv(nome);
        return valor != null && !valor.isBlank();
    }
}

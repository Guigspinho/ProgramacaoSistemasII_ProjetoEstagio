package app;

import java.util.NoSuchElementException;
import java.util.Scanner;
import service.stub.ServicosEmMemoria;

/**
 * Ponto de entrada. Monta as dependências (injeção por construtor) e abre o menu.
 * Por enquanto usa serviços em memória; depois: conexão -> DAOs JDBC -> serviços reais.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Entrada entrada = new Entrada(scanner);

        // TROCAR AQUI pelos serviços reais quando existirem (UsuarioServiceImpl, VagaServiceImpl...)
        ServicosEmMemoria servicos = new ServicosEmMemoria();

        MenuPrincipal menu = new MenuPrincipal(entrada, servicos, servicos, servicos, servicos, servicos);
        try {
            menu.abrir();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada. Até logo!");
        }
    }
}

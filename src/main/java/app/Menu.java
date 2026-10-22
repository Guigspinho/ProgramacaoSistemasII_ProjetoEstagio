package app;

import exception.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu numerado genérico. Cada opção é um título + uma ação (Runnable, interface funcional)
 * registrada com expressão lambda ou referência de método. A opção 0 sempre volta/sai.
 */
public class Menu {
    private record Opcao(String titulo, Runnable acao) { }

    private final String titulo;
    private final String rotuloSaida;
    private final Entrada entrada;
    private final List<Opcao> opcoes = new ArrayList<>();

    public Menu(String titulo, String rotuloSaida, Entrada entrada) {
        this.titulo = titulo;
        this.rotuloSaida = rotuloSaida;
        this.entrada = entrada;
    }

    public Menu adicionar(String titulo, Runnable acao) {
        opcoes.add(new Opcao(titulo, acao));
        return this;
    }

    /** Repete o menu até o usuário escolher 0. */
    public void exibir() {
        while (true) {
            System.out.println("\n=== " + titulo + " ===");
            for (int i = 0; i < opcoes.size(); i++) {
                System.out.println((i + 1) + " - " + opcoes.get(i).titulo());
            }
            System.out.println("0 - " + rotuloSaida);

            int escolha = entrada.lerOpcao("Opção", 0, opcoes.size());
            if (escolha == 0) return;
            executarSeguro(opcoes.get(escolha - 1).acao());
        }
    }

    /**
     * Executa uma ação convertendo as exceções esperadas em mensagens amigáveis,
     * para que nenhum erro de regra ou de persistência encerre o programa.
     */
    public static void executarSeguro(Runnable acao) {
        try {
            acao.run();
        } catch (AcessoNegadoException e) {
            System.out.println("\n[Acesso negado] " + e.getMessage());
        } catch (RegraNegocioException e) {
            System.out.println("\n[Não foi possível concluir] " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("\n[Erro de dados] Falha ao acessar o banco. Tente novamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[Dados inválidos] " + e.getMessage());
        }
    }
}

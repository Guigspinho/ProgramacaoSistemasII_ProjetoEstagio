package app;

import exception.*;
import java.util.ArrayList;
import java.util.List;

/** Menu numerado que mostra opções e devolve a escolha do usuário. */
public class Menu {
    private final String titulo;
    private final String rotuloSaida;
    private final Entrada entrada;
    private final List<String> opcoes = new ArrayList<>();

    public Menu(String titulo, String rotuloSaida, Entrada entrada) {
        this.titulo = titulo;
        this.rotuloSaida = rotuloSaida;
        this.entrada = entrada;
    }

    public Menu adicionar(String titulo) {
        opcoes.add(titulo);
        return this;
    }

    public int exibir() {
        System.out.println("\n=== " + titulo + " ===");
        for (int i = 0; i < opcoes.size(); i++) {
            System.out.println((i + 1) + " - " + opcoes.get(i));
        }
        System.out.println("0 - " + rotuloSaida);
        return entrada.lerOpcao("Opção", 0, opcoes.size());
    }

    public static void tratarErro(RuntimeException e) {
        if (e instanceof AcessoNegadoException) {
            System.out.println("\n[Acesso negado] " + e.getMessage());
        } else if (e instanceof RegraNegocioException) {
            System.out.println("\n[Não foi possível concluir] " + e.getMessage());
        } else if (e instanceof PersistenciaException) {
            System.out.println("\n[Erro de dados] Falha ao acessar o banco. Tente novamente.");
        } else if (e instanceof IllegalArgumentException) {
            System.out.println("\n[Dados inválidos] " + e.getMessage());
        } else {
            throw e;
        }
    }
}
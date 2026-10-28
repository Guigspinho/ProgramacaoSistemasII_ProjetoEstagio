package app;

import java.util.Scanner;
import model.Modalidade;

/**
 * Leitura segura do console. Repete a pergunta quando o valor é inválido,
 * então um erro de digitação nunca derruba a aplicação.
 * Se a entrada acabar (Ctrl+D / fim do arquivo), o Scanner lança NoSuchElementException,
 * tratada no Main para encerrar com elegância.
 */
public class Entrada {
    private final Scanner scanner;

    public Entrada(Scanner scanner) { this.scanner = scanner; }

    public String lerLinha(String rotulo) {
        System.out.print(rotulo + ": ");
        return scanner.nextLine();
    }

    /** Texto obrigatório: repete até receber algo não vazio. */
    public String lerTexto(String rotulo) {
        while (true) {
            String s = lerLinha(rotulo);
            if (!s.isBlank()) return s.trim();
            System.out.println("  Valor obrigatório.");
        }
    }

    /** Texto opcional: Enter em branco devolve null. */
    public String lerTextoOpcional(String rotulo) {
        String s = lerLinha(rotulo + " (Enter para pular)");
        return s.isBlank() ? null : s.trim();
    }

    public int lerInt(String rotulo) {
        while (true) {
            try {
                return Integer.parseInt(lerLinha(rotulo).trim());
            } catch (NumberFormatException e) {
                System.out.println("  Digite um número inteiro.");
            }
        }
    }

    public Long lerLong(String rotulo) { return (long) lerInt(rotulo); }

    /** Número opcional: Enter em branco devolve null (usado em filtros). */
    public Long lerLongOpcional(String rotulo) {
        while (true) {
            String s = lerLinha(rotulo + " (Enter para pular)").trim();
            if (s.isEmpty()) return null;
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                System.out.println("  Digite um número inteiro ou Enter.");
            }
        }
    }

    /** Opção de menu entre min e max (inclusive). */
    public int lerOpcao(String rotulo, int min, int max) {
        while (true) {
            int n = lerInt(rotulo);
            if (n >= min && n <= max) return n;
            System.out.println("  Opção inválida. Escolha entre " + min + " e " + max + ".");
        }
    }

    /** Mostra as modalidades numeradas e lê a escolha. */
    public Modalidade lerModalidade(String rotulo) {
        Modalidade[] valores = Modalidade.values();
        mostrarModalidades(rotulo, valores);
        return valores[lerOpcao("Escolha", 1, valores.length) - 1];
    }

    /** Como lerModalidade, mas Enter em branco devolve null (sem filtro). */
    public Modalidade lerModalidadeOpcional(String rotulo) {
        Modalidade[] valores = Modalidade.values();
        mostrarModalidades(rotulo, valores);
        while (true) {
            String s = lerLinha("Escolha (Enter para pular)").trim();
            if (s.isEmpty()) return null;
            try {
                int n = Integer.parseInt(s);
                if (n >= 1 && n <= valores.length) return valores[n - 1];
            } catch (NumberFormatException e) {
                // cai na mensagem abaixo
            }
            System.out.println("  Opção inválida.");
        }
    }

    private void mostrarModalidades(String rotulo, Modalidade[] valores) {
        System.out.println(rotulo + ":");
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + " - " + valores[i]);
        }
    }
}

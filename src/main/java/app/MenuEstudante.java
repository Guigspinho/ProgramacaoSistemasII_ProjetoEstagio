package app;

import java.util.List;
import model.*;
import service.*;

public class MenuEstudante {
    private final Entrada entrada;
    private final CandidaturaService candidaturaService;
    private final TelaVagas telaVagas;

    public MenuEstudante(Entrada entrada, CandidaturaService candidaturaService, TelaVagas telaVagas) {
        this.entrada = entrada;
        this.candidaturaService = candidaturaService;
        this.telaVagas = telaVagas;
    }

    public void abrir(Usuario ator) {
        Menu menu = new Menu("ESTUDANTE: " + ator.getNome(), "Voltar", entrada)
                .adicionar("Consultar vagas abertas")
                .adicionar("Candidatar-se a uma vaga")
                .adicionar("Minhas candidaturas");
        while (true) {
            int opcao = menu.exibir();
            if (opcao == 0) return;
            try {
                switch (opcao) {
                    case 1: telaVagas.consultarAbertas(); break;
                    case 2: candidatar(ator); break;
                    case 3: minhasCandidaturas(ator); break;
                    default: break;
                }
            } catch (RuntimeException e) {
                Menu.tratarErro(e);
            }
        }
    }

    private void candidatar(Usuario ator) {
        Long vagaId = entrada.lerLong("ID da vaga");
        Candidatura c = candidaturaService.candidatar(ator, vagaId);
        System.out.println("Candidatura realizada: " + c);
    }

    private void minhasCandidaturas(Usuario ator) {
        List<Candidatura> lista = candidaturaService.listarMinhas(ator);
        if (lista.isEmpty()) System.out.println("Você ainda não tem candidaturas.");
        for (Candidatura c : lista) {
            System.out.println(c);
        }
    }
}

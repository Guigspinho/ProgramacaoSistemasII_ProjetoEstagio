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
        new Menu("ESTUDANTE: " + ator.getNome(), "Voltar", entrada)
                .adicionar("Consultar vagas abertas", telaVagas::consultarAbertas)
                .adicionar("Candidatar-se a uma vaga", () -> candidatar(ator))
                .adicionar("Minhas candidaturas", () -> minhasCandidaturas(ator))
                .exibir();
    }

    private void candidatar(Usuario ator) {
        Long vagaId = entrada.lerLong("ID da vaga");
        Candidatura c = candidaturaService.candidatar(ator, vagaId);
        System.out.println("Candidatura realizada: " + c);
    }

    private void minhasCandidaturas(Usuario ator) {
        List<Candidatura> lista = candidaturaService.listarMinhas(ator);
        if (lista.isEmpty()) System.out.println("Você ainda não tem candidaturas.");
        lista.forEach(System.out::println);
    }
}

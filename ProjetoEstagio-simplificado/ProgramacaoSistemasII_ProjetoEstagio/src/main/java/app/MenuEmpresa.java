package app;

import java.util.List;
import model.*;
import service.*;

public class MenuEmpresa {
    private final Entrada entrada;
    private final VagaService vagaService;
    private final CandidaturaService candidaturaService;
    private final TelaVagas telaVagas;

    public MenuEmpresa(Entrada entrada, VagaService vagaService,
                       CandidaturaService candidaturaService, TelaVagas telaVagas) {
        this.entrada = entrada;
        this.vagaService = vagaService;
        this.candidaturaService = candidaturaService;
        this.telaVagas = telaVagas;
    }

    public void abrir(Usuario ator) {
        Menu menu = new Menu("EMPRESA: " + ator.getNome(), "Voltar", entrada)
                .adicionar("Publicar vaga")
                .adicionar("Minhas vagas")
                .adicionar("Editar vaga")
                .adicionar("Encerrar vaga")
                .adicionar("Ver candidatos de uma vaga")
                .adicionar("Consultar vagas abertas (consulta pública)");
        while (true) {
            int opcao = menu.exibir();
            if (opcao == 0) return;
            try {
                switch (opcao) {
                    case 1: publicar(ator); break;
                    case 2: telaVagas.listar(vagaService.listarDaEmpresa(ator)); break;
                    case 3: editar(ator); break;
                    case 4: encerrar(ator); break;
                    case 5: verCandidatos(ator); break;
                    case 6: telaVagas.consultarAbertas(); break;
                    default: break;
                }
            } catch (RuntimeException e) {
                Menu.tratarErro(e);
            }
        }
    }

    private void publicar(Usuario ator) {
        String titulo = entrada.lerTexto("Título");
        String descricao = entrada.lerTextoOpcional("Descrição");
        telaVagas.mostrarAreas();
        Long areaId = entrada.lerLong("ID da área");
        String local = entrada.lerTextoOpcional("Localização");
        Modalidade modalidade = entrada.lerModalidade("Modalidade");
        int carga = entrada.lerInt("Carga horária semanal (horas)");
        Vaga v = vagaService.publicar(ator, titulo, descricao, areaId, local, modalidade, carga);
        System.out.println("Vaga publicada: " + v);
    }

    private void editar(Usuario ator) {
        telaVagas.listar(vagaService.listarDaEmpresa(ator));
        Long vagaId = entrada.lerLong("ID da vaga a editar");
        System.out.println("Informe os novos dados da vaga:");
        String titulo = entrada.lerTexto("Título");
        String descricao = entrada.lerTextoOpcional("Descrição");
        telaVagas.mostrarAreas();
        Long areaId = entrada.lerLong("ID da área");
        String local = entrada.lerTextoOpcional("Localização");
        Modalidade modalidade = entrada.lerModalidade("Modalidade");
        int carga = entrada.lerInt("Carga horária semanal (horas)");
        Vaga v = vagaService.editar(ator, vagaId, titulo, descricao, areaId, local, modalidade, carga);
        System.out.println("Vaga atualizada: " + v);
    }

    private void encerrar(Usuario ator) {
        telaVagas.listar(vagaService.listarDaEmpresa(ator));
        Long vagaId = entrada.lerLong("ID da vaga a encerrar");
        vagaService.encerrar(ator, vagaId);
        System.out.println("Vaga encerrada. As candidaturas anteriores foram preservadas.");
    }

    private void verCandidatos(Usuario ator) {
        Long vagaId = entrada.lerLong("ID da vaga");
        List<Candidatura> lista = candidaturaService.listarCandidatos(ator, vagaId);
        if (lista.isEmpty()) System.out.println("Nenhum candidato para esta vaga.");
        for (Candidatura c : lista) {
            Estudante e = c.getEstudante();
            System.out.println("  " + e.getNome() + " <" + e.getEmail() + ">"
                    + " - " + e.getCurso() + " (inscrito em " + c.getDataInscricao() + ")");
        }
    }
}

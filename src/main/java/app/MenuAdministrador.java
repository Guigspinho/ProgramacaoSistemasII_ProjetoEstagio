package app;

import model.*;
import service.*;

public class MenuAdministrador {
    private final Entrada entrada;
    private final AreaService areaService;
    private final RelatorioService relatorioService;
    private final TelaVagas telaVagas;

    public MenuAdministrador(Entrada entrada, AreaService areaService,
                             RelatorioService relatorioService, TelaVagas telaVagas) {
        this.entrada = entrada;
        this.areaService = areaService;
        this.relatorioService = relatorioService;
        this.telaVagas = telaVagas;
    }

    public void abrir(Usuario ator) {
        Menu menu = new Menu("ADMINISTRADOR: " + ator.getNome(), "Voltar", entrada)
                .adicionar("Cadastrar área")
                .adicionar("Listar áreas")
                .adicionar("Editar área")
                .adicionar("Excluir área")
                .adicionar("Ver contagens do portal");
        while (true) {
            int opcao = menu.exibir();
            if (opcao == 0) return;
            try {
                switch (opcao) {
                    case 1: cadastrarArea(ator); break;
                    case 2: telaVagas.mostrarAreas(); break;
                    case 3: editarArea(ator); break;
                    case 4: excluirArea(ator); break;
                    case 5: contagens(ator); break;
                    default: break;
                }
            } catch (RuntimeException e) {
                Menu.tratarErro(e);
            }
        }
    }

    private void cadastrarArea(Usuario ator) {
        Area a = areaService.cadastrar(ator, entrada.lerTexto("Nome da nova área"));
        System.out.println("Área cadastrada: [" + a.getId() + "] " + a.getNome());
    }

    private void editarArea(Usuario ator) {
        telaVagas.mostrarAreas();
        Long id = entrada.lerLong("ID da área");
        Area a = areaService.editar(ator, id, entrada.lerTexto("Novo nome"));
        System.out.println("Área atualizada: [" + a.getId() + "] " + a.getNome());
    }

    private void excluirArea(Usuario ator) {
        telaVagas.mostrarAreas();
        areaService.excluir(ator, entrada.lerLong("ID da área a excluir"));
        System.out.println("Área excluída.");
    }

    private void contagens(Usuario ator) {
        long[] dados = relatorioService.contagens(ator);
        String[] categorias = {"Estudantes", "Empresas", "Vagas abertas", "Vagas encerradas", "Candidaturas"};
        System.out.println("--- Contagens do portal ---");
        for (int i = 0; i < categorias.length; i++) {
            System.out.println("  " + categorias[i] + ": " + dados[i]);
        }
    }
}

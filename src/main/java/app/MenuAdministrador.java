package app;

import java.util.Map;
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
        new Menu("ADMINISTRADOR: " + ator.getNome(), "Voltar", entrada)
                .adicionar("Cadastrar área", () -> cadastrarArea(ator))
                .adicionar("Listar áreas", telaVagas::mostrarAreas)
                .adicionar("Editar área", () -> editarArea(ator))
                .adicionar("Excluir área", () -> excluirArea(ator))
                .adicionar("Ver contagens do portal", () -> contagens(ator))
                .exibir();
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
        Map<String, Long> dados = relatorioService.contagens(ator);
        System.out.println("--- Contagens do portal ---");
        dados.forEach((rotulo, qtd) -> System.out.println("  " + rotulo + ": " + qtd));
    }
}

package app;

import java.util.List;
import model.*;
import service.*;

/** Telas de vagas reutilizadas por mais de um menu (consulta pública, listagem, áreas). */
public class TelaVagas {
    private final Entrada entrada;
    private final VagaService vagaService;
    private final AreaService areaService;

    public TelaVagas(Entrada entrada, VagaService vagaService, AreaService areaService) {
        this.entrada = entrada;
        this.vagaService = vagaService;
        this.areaService = areaService;
    }

    /** Consulta pública: vagas abertas com filtros opcionais de área e modalidade. */
    public void consultarAbertas() {
        mostrarAreas();
        Long areaId = entrada.lerLongOpcional("Filtrar por ID da área");
        Modalidade modalidade = entrada.lerEnumOpcional("Filtrar por modalidade", Modalidade.class);
        listar(vagaService.listarAbertas(areaId, modalidade));
    }

    public void listar(List<Vaga> vagas) {
        if (vagas.isEmpty()) {
            System.out.println("Nenhuma vaga encontrada.");
            return;
        }
        vagas.forEach(System.out::println);
    }

    public void mostrarAreas() {
        List<Area> areas = areaService.listar();
        System.out.println("Áreas:");
        if (areas.isEmpty()) System.out.println("  (nenhuma cadastrada)");
        areas.forEach(a -> System.out.println("  [" + a.getId() + "] " + a.getNome()));
    }
}

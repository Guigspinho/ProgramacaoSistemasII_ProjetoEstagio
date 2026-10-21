package service;

import java.util.List;
import model.*;

public interface VagaService {
    Vaga publicar(Usuario ator, String titulo, String descricao, Long areaId,
                  String localizacao, Modalidade modalidade, int cargaHorariaSemanal);
    Vaga editar(Usuario ator, Long vagaId, String titulo, String descricao, Long areaId,
                String localizacao, Modalidade modalidade, int cargaHorariaSemanal);
    void encerrar(Usuario ator, Long vagaId);
    /** Consulta pública. areaId e modalidade nulos significam "sem filtro". */
    List<Vaga> listarAbertas(Long areaId, Modalidade modalidade);
    List<Vaga> listarDaEmpresa(Usuario ator);
}

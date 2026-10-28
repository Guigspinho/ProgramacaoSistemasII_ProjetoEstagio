package dao;

import java.util.List;
import model.Candidatura;

/** Operações de persistência para candidaturas. */
public interface CandidaturaDAO {

    /** Grava a candidatura e devolve-a com o ID gerado pelo banco. */
    Candidatura cadastrar(Candidatura candidatura);

    List<Candidatura> listarPorEstudante(Long estudanteId);

    List<Candidatura> listarPorVaga(Long vagaId);

    /** Indica se o estudante já se candidatou à vaga. */
    boolean existePorEstudanteEVaga(Long estudanteId, Long vagaId);
}

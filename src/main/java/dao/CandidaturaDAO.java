package dao;

import model.Candidatura;

import java.sql.SQLException;
import java.util.List;

/** Define as operações de persistência para candidaturas. */
public interface CandidaturaDAO {

    /** Registra uma candidatura e retorna o objeto com o ID gerado pelo banco. */
    Candidatura cadastrar(Candidatura candidatura) throws SQLException;

    /** Lista as candidaturas feitas por um estudante. */
    List<Candidatura> listarPorEstudante(Long estudanteId) throws SQLException;

    /** Lista os candidatos inscritos em uma vaga. */
    List<Candidatura> listarPorVaga(Long vagaId) throws SQLException;

    /** Verifica se o estudante já se candidatou à vaga. */
    boolean existePorEstudanteEVaga(Long estudanteId, Long vagaId) throws SQLException;
}

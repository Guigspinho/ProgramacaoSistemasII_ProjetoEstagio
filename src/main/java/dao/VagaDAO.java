package dao;

import model.Modalidade;
import model.SituacaoVaga;
import model.Vaga;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Define as operações de persistência para vagas de estágio. */
public interface VagaDAO {

    /** Publica uma vaga e retorna o objeto com o ID gerado pelo banco. */
    Vaga publicar(Vaga vaga) throws SQLException;

    /** Busca uma vaga pelo ID. */
    Optional<Vaga> buscarPorId(Long id) throws SQLException;

    /** Lista todas as vagas, abertas ou encerradas. */
    List<Vaga> listarTodas() throws SQLException;

    /** Lista as vagas pertencentes a uma empresa. */
    List<Vaga> listarPorEmpresa(Long empresaId) throws SQLException;

    /**
     * Lista vagas usando filtros opcionais.
     * Um parâmetro nulo significa que aquele filtro não será aplicado;
     * termo nulo ou em branco significa que não haverá busca textual.
     */
    List<Vaga> listarComFiltros(
            Long areaId,
            Modalidade modalidade,
            SituacaoVaga situacao,
            String termo
    ) throws SQLException;

    /** Atualiza os dados da vaga; retorna false se ela não existir. */
    boolean atualizar(Vaga vaga) throws SQLException;

    /** Encerra a vaga; retorna false se ela não existir. */
    boolean encerrar(Long vagaId) throws SQLException;
}

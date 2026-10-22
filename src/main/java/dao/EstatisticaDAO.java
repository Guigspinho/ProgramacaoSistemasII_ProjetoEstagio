package dao;

import model.SituacaoVaga;

import java.sql.SQLException;

/** Define consultas de contagem para os indicadores administrativos. */
public interface EstatisticaDAO {

    long contarUsuarios() throws SQLException;

    long contarEstudantes() throws SQLException;

    long contarEmpresas() throws SQLException;

    long contarAreas() throws SQLException;

    long contarVagas() throws SQLException;

    long contarVagasPorSituacao(SituacaoVaga situacao) throws SQLException;

    long contarCandidaturas() throws SQLException;
}

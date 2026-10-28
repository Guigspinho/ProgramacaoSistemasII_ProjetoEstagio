package dao.jdbc;

import dao.EstatisticaDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.SituacaoVaga;
import service.jdbc.ConexaoSupabase;

public final class JdbcEstatisticaDAO extends JdbcDaoSupport implements EstatisticaDAO {
    public JdbcEstatisticaDAO(ConexaoSupabase conexoes) {
        super(conexoes);
    }

    private long contar(String sql) throws SQLException {
        return consultar(new SqlOperation<Long>() {
            @Override
            public Long execute(Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    return rs.getLong(1);
                }
            }
        });
    }

    @Override public long contarUsuarios() throws SQLException { return contar("SELECT count(*) FROM usuarios"); }
    @Override public long contarEstudantes() throws SQLException { return contar("SELECT count(*) FROM estudantes"); }
    @Override public long contarEmpresas() throws SQLException { return contar("SELECT count(*) FROM empresas"); }
    @Override public long contarAreas() throws SQLException { return contar("SELECT count(*) FROM areas"); }
    @Override public long contarVagas() throws SQLException { return contar("SELECT count(*) FROM vagas"); }

    @Override
    public long contarVagasPorSituacao(SituacaoVaga situacao) throws SQLException {
        return consultar(new SqlOperation<Long>() {
            @Override
            public Long execute(Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT count(*) FROM vagas WHERE situacao = ?")) {
                    statement.setString(1, situacao.name());
                    try (ResultSet rs = statement.executeQuery()) {
                        rs.next();
                        return rs.getLong(1);
                    }
                }
            }
        });
    }

    @Override
    public long contarCandidaturas() throws SQLException {
        return contar("SELECT count(*) FROM candidaturas");
    }
}

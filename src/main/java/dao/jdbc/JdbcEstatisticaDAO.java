package dao.jdbc;

import dao.EstatisticaDAO;
import exception.PersistenciaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.SituacaoVaga;

public class JdbcEstatisticaDAO implements EstatisticaDAO {
    private final ConexaoSupabase conexao;

    public JdbcEstatisticaDAO(ConexaoSupabase conexao) {
        this.conexao = conexao;
    }

    @Override
    public long contarEstudantes() {
        return contar("SELECT count(*) FROM estudantes");
    }

    @Override
    public long contarEmpresas() {
        return contar("SELECT count(*) FROM empresas");
    }

    @Override
    public long contarCandidaturas() {
        return contar("SELECT count(*) FROM candidaturas");
    }

    @Override
    public long contarVagasPorSituacao(SituacaoVaga situacao) {
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM vagas WHERE situacao = ?")) {
            ps.setString(1, situacao.name());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao contar as vagas.", e);
        }
    }

    /** Executa um SELECT count(*) sem parâmetros e devolve o resultado. */
    private long contar(String sql) {
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao contar registros.", e);
        }
    }
}

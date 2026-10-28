package dao.jdbc;

import dao.CandidaturaDAO;
import exception.PersistenciaException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Candidatura;

public class JdbcCandidaturaDAO implements CandidaturaDAO {
    /** Candidatura + estudante + vaga (com área e empresa). O WHERE é acrescentado em cada consulta. */
    private static final String SELECT_CANDIDATURAS =
            "SELECT c.id AS candidatura_id, c.data_inscricao, "
            + "su.id AS estudante_id, su.nome AS estudante_nome, su.email AS estudante_email, "
            + "e.curso AS estudante_curso, "
            + "v.id AS vaga_id, v.titulo AS vaga_titulo, v.descricao AS vaga_descricao, "
            + "v.localizacao AS vaga_localizacao, v.modalidade AS vaga_modalidade, "
            + "v.carga_horaria_semanal AS vaga_carga, v.situacao AS vaga_situacao, "
            + "a.id AS area_id, a.nome AS area_nome, "
            + "eu.id AS empresa_id, eu.nome AS empresa_responsavel, eu.email AS empresa_email, "
            + "em.nome_empresa AS empresa_nome "
            + "FROM candidaturas c "
            + "JOIN estudantes e ON e.usuario_id = c.estudante_id "
            + "JOIN usuarios su ON su.id = e.usuario_id "
            + "JOIN vagas v ON v.id = c.vaga_id "
            + "JOIN areas a ON a.id = v.area_id "
            + "JOIN empresas em ON em.usuario_id = v.empresa_id "
            + "JOIN usuarios eu ON eu.id = em.usuario_id ";

    private final ConexaoSupabase conexao;

    public JdbcCandidaturaDAO(ConexaoSupabase conexao) {
        this.conexao = conexao;
    }

    @Override
    public Candidatura cadastrar(Candidatura candidatura) {
        String sql = "INSERT INTO candidaturas (estudante_id, vaga_id, data_inscricao) VALUES (?, ?, ?)";
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, candidatura.getEstudante().getId());
            ps.setLong(2, candidatura.getVaga().getId());
            ps.setDate(3, Date.valueOf(candidatura.getDataInscricao()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                candidatura.setId(keys.getLong("id"));
            }
            return candidatura;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao registrar a candidatura.", e);
        }
    }

    @Override
    public List<Candidatura> listarPorEstudante(Long estudanteId) {
        return listar("WHERE c.estudante_id = ? ORDER BY c.id", estudanteId);
    }

    @Override
    public List<Candidatura> listarPorVaga(Long vagaId) {
        return listar("WHERE c.vaga_id = ? ORDER BY c.id", vagaId);
    }

    /** Executa o SELECT_CANDIDATURAS com o filtro recebido (um único parâmetro numérico). */
    private List<Candidatura> listar(String filtro, Long id) {
        List<Candidatura> candidaturas = new ArrayList<>();
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(SELECT_CANDIDATURAS + filtro)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    candidaturas.add(JdbcMapeadores.candidatura(rs));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar as candidaturas.", e);
        }
        return candidaturas;
    }

    @Override
    public boolean existePorEstudanteEVaga(Long estudanteId, Long vagaId) {
        String sql = "SELECT 1 FROM candidaturas WHERE estudante_id = ? AND vaga_id = ?";
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, estudanteId);
            ps.setLong(2, vagaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao verificar a candidatura.", e);
        }
    }
}

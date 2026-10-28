package dao.jdbc;

import dao.CandidaturaDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Candidatura;
import service.jdbc.ConexaoSupabase;

public final class JdbcCandidaturaDAO extends JdbcDaoSupport implements CandidaturaDAO {
    private static final String SELECT_CANDIDATURAS = """
            SELECT c.id AS candidatura_id, c.data_inscricao,
                   su.id AS estudante_id, su.nome AS estudante_nome, su.email AS estudante_email,
                   e.curso AS estudante_curso,
                   v.id AS vaga_id, v.titulo AS vaga_titulo, v.descricao AS vaga_descricao,
                   v.localizacao AS vaga_localizacao, v.modalidade AS vaga_modalidade,
                   v.carga_horaria_semanal AS vaga_carga, v.situacao AS vaga_situacao,
                   a.id AS area_id, a.nome AS area_nome,
                   eu.id AS empresa_id, eu.nome AS empresa_responsavel, eu.email AS empresa_email,
                   em.nome_empresa AS empresa_nome
            FROM candidaturas c
            JOIN estudantes e ON e.usuario_id = c.estudante_id
            JOIN usuarios su ON su.id = e.usuario_id
            JOIN vagas v ON v.id = c.vaga_id
            JOIN areas a ON a.id = v.area_id
            JOIN empresas em ON em.usuario_id = v.empresa_id
            JOIN usuarios eu ON eu.id = em.usuario_id
            """;

    public JdbcCandidaturaDAO(ConexaoSupabase conexoes) {
        super(conexoes);
    }

    @Override
    public Candidatura cadastrar(Candidatura candidatura) throws SQLException {
        return consultar(new SqlOperation<Candidatura>() {
            @Override
            public Candidatura execute(Connection connection) throws SQLException {
                String sql = "INSERT INTO candidaturas (estudante_id, vaga_id, data_inscricao) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setLong(1, candidatura.getEstudante().getId());
                    statement.setLong(2, candidatura.getVaga().getId());
                    statement.setDate(3, java.sql.Date.valueOf(candidatura.getDataInscricao()));
                    long id = executarInsertGerandoId(statement);
                    return new Candidatura(id, candidatura.getEstudante(), candidatura.getVaga(),
                            candidatura.getDataInscricao());
                }
            }
        });
    }

    @Override
    public List<Candidatura> listarPorEstudante(Long estudanteId) throws SQLException {
        return listar("c.estudante_id = ?", estudanteId);
    }

    @Override
    public List<Candidatura> listarPorVaga(Long vagaId) throws SQLException {
        return listar("c.vaga_id = ?", vagaId);
    }

    private List<Candidatura> listar(String condicao, Long id) throws SQLException {
        return consultar(new SqlOperation<List<Candidatura>>() {
            @Override
            public List<Candidatura> execute(Connection connection) throws SQLException {
                List<Candidatura> candidaturas = new ArrayList<>();
                try (PreparedStatement statement = connection.prepareStatement(
                        SELECT_CANDIDATURAS + " WHERE " + condicao + " ORDER BY c.id")) {
                    statement.setLong(1, id);
                    try (ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) candidaturas.add(JdbcMapeadores.candidatura(rs));
                    }
                }
                return List.copyOf(candidaturas);
            }
        });
    }

    @Override
    public boolean existePorEstudanteEVaga(Long estudanteId, Long vagaId) throws SQLException {
        return consultar(new SqlOperation<Boolean>() {
            @Override
            public Boolean execute(Connection connection) throws SQLException {
                String sql = "SELECT 1 FROM candidaturas WHERE estudante_id = ? AND vaga_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setLong(1, estudanteId);
                    statement.setLong(2, vagaId);
                    try (ResultSet rs = statement.executeQuery()) {
                        return rs.next();
                    }
                }
            }
        });
    }
}

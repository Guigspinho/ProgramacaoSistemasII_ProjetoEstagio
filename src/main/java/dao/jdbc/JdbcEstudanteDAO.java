package dao.jdbc;

import dao.EstudanteDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Estudante;
import service.jdbc.ConexaoSupabase;

public final class JdbcEstudanteDAO extends JdbcDaoSupport implements EstudanteDAO {
    private static final String SELECT_ESTUDANTES = """
            SELECT u.id AS estudante_id, u.nome AS estudante_nome, u.email AS estudante_email,
                   e.curso AS estudante_curso
            FROM usuarios u JOIN estudantes e ON e.usuario_id = u.id
            """;

    public JdbcEstudanteDAO(ConexaoSupabase conexoes) {
        super(conexoes);
    }

    @Override
    public Estudante cadastrar(Estudante estudante) throws SQLException {
        return transacionar(connection -> {
            long id;
            String sqlUsuario = "INSERT INTO usuarios (nome, email, perfil) VALUES (?, ?, 'ESTUDANTE')";
            try (PreparedStatement statement = connection.prepareStatement(
                    sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, estudante.getNome().trim());
                statement.setString(2, estudante.getEmail().trim().toLowerCase());
                id = executarInsertGerandoId(statement);
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO estudantes (usuario_id, curso) VALUES (?, ?)")) {
                statement.setLong(1, id);
                statement.setString(2, estudante.getCurso().trim());
                statement.executeUpdate();
            }
            return new Estudante(id, estudante.getNome().trim(),
                    estudante.getEmail().trim().toLowerCase(), estudante.getCurso().trim());
        });
    }

    @Override
    public Optional<Estudante> buscarPorId(Long id) throws SQLException {
        return consultar(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    SELECT_ESTUDANTES + " WHERE u.id = ?")) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    return rs.next() ? Optional.of(JdbcMapeadores.estudante(rs)) : Optional.empty();
                }
            }
        });
    }

    @Override
    public List<Estudante> listarTodos() throws SQLException {
        return consultar(connection -> {
            List<Estudante> estudantes = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(
                    SELECT_ESTUDANTES + " ORDER BY u.id");
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) estudantes.add(JdbcMapeadores.estudante(rs));
            }
            return List.copyOf(estudantes);
        });
    }
}

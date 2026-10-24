package dao.jdbc;

import dao.UsuarioDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import service.jdbc.ConexaoSupabase;
import model.Usuario;

public final class JdbcUsuarioDAO extends JdbcDaoSupport implements UsuarioDAO {
    private static final String SELECT_USUARIOS = """
            SELECT u.id AS usuario_id, u.nome AS usuario_nome, u.email AS usuario_email,
                   u.perfil AS usuario_perfil, e.curso AS estudante_curso,
                   em.nome_empresa AS empresa_nome
            FROM usuarios u
            LEFT JOIN estudantes e ON e.usuario_id = u.id
            LEFT JOIN empresas em ON em.usuario_id = u.id
            """;

    public JdbcUsuarioDAO(ConexaoSupabase conexoes) {
        super(conexoes);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) throws SQLException {
        return buscar("u.id = ?", id);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        return buscar("u.email = ?", email);
    }

    private Optional<Usuario> buscar(String condicao, Object parametro) throws SQLException {
        return consultar(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    SELECT_USUARIOS + " WHERE " + condicao)) {
                statement.setObject(1, parametro);
                try (ResultSet rs = statement.executeQuery()) {
                    return rs.next() ? Optional.of(JdbcMapeadores.usuario(rs)) : Optional.empty();
                }
            }
        });
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        return consultar(connection -> {
            List<Usuario> usuarios = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(
                    SELECT_USUARIOS + " ORDER BY u.id");
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) usuarios.add(JdbcMapeadores.usuario(rs));
            }
            return List.copyOf(usuarios);
        });
    }
}

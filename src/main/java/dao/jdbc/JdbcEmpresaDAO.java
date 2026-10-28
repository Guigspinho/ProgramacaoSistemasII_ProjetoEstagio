package dao.jdbc;

import dao.EmpresaDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Empresa;
import service.jdbc.ConexaoSupabase;

public final class JdbcEmpresaDAO extends JdbcDaoSupport implements EmpresaDAO {
    private static final String SELECT_EMPRESAS = """
            SELECT u.id AS empresa_id, u.nome AS empresa_responsavel, u.email AS empresa_email,
                   e.nome_empresa AS empresa_nome
            FROM usuarios u JOIN empresas e ON e.usuario_id = u.id
            """;

    public JdbcEmpresaDAO(ConexaoSupabase conexoes) {
        super(conexoes);
    }

    @Override
    public Empresa cadastrar(Empresa empresa) throws SQLException {
        return transacionar(new SqlOperation<Empresa>() {
            @Override
            public Empresa execute(Connection connection) throws SQLException {
                long id;
                String sqlUsuario = "INSERT INTO usuarios (nome, email, perfil) VALUES (?, ?, 'EMPRESA')";
                try (PreparedStatement statement = connection.prepareStatement(
                        sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, empresa.getNome().trim());
                    statement.setString(2, empresa.getEmail().trim().toLowerCase());
                    id = executarInsertGerandoId(statement);
                }
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO empresas (usuario_id, nome_empresa) VALUES (?, ?)")) {
                    statement.setLong(1, id);
                    statement.setString(2, empresa.getNomeEmpresa().trim());
                    statement.executeUpdate();
                }
                return new Empresa(id, empresa.getNome().trim(),
                        empresa.getEmail().trim().toLowerCase(), empresa.getNomeEmpresa().trim());
            }
        });
    }

    @Override
    public Optional<Empresa> buscarPorId(Long id) throws SQLException {
        return consultar(new SqlOperation<Optional<Empresa>>() {
            @Override
            public Optional<Empresa> execute(Connection connection) throws SQLException {
                try (PreparedStatement statement = connection.prepareStatement(
                        SELECT_EMPRESAS + " WHERE u.id = ?")) {
                    statement.setLong(1, id);
                    try (ResultSet rs = statement.executeQuery()) {
                        return rs.next() ? Optional.of(JdbcMapeadores.empresa(rs)) : Optional.empty();
                    }
                }
            }
        });
    }

    @Override
    public List<Empresa> listarTodas() throws SQLException {
        return consultar(new SqlOperation<List<Empresa>>() {
            @Override
            public List<Empresa> execute(Connection connection) throws SQLException {
                List<Empresa> empresas = new ArrayList<>();
                try (PreparedStatement statement = connection.prepareStatement(
                        SELECT_EMPRESAS + " ORDER BY u.id");
                     ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) empresas.add(JdbcMapeadores.empresa(rs));
                }
                return List.copyOf(empresas);
            }
        });
    }
}

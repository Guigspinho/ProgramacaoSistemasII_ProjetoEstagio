package dao.jdbc;

import dao.AreaDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Area;
import service.jdbc.ConexaoSupabase;

public final class JdbcAreaDAO extends JdbcDaoSupport implements AreaDAO {
    public JdbcAreaDAO(ConexaoSupabase conexoes) {
        super(conexoes);
    }

    @Override
    public Area cadastrar(Area area) throws SQLException {
        return consultar(connection -> {
            String sql = "INSERT INTO areas (nome) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, area.getNome().trim());
                long id = executarInsertGerandoId(statement);
                return new Area(id, area.getNome().trim());
            }
        });
    }

    @Override
    public List<Area> listarTodas() throws SQLException {
        return consultar(connection -> {
            List<Area> areas = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id AS area_id, nome AS area_nome FROM areas ORDER BY nome");
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) areas.add(JdbcMapeadores.area(rs));
            }
            return List.copyOf(areas);
        });
    }

    @Override
    public Optional<Area> buscarPorId(Long id) throws SQLException {
        return consultar(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id AS area_id, nome AS area_nome FROM areas WHERE id = ?")) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    return rs.next() ? Optional.of(JdbcMapeadores.area(rs)) : Optional.empty();
                }
            }
        });
    }

    @Override
    public boolean atualizar(Area area) throws SQLException {
        return consultar(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE areas SET nome = ? WHERE id = ?")) {
                statement.setString(1, area.getNome().trim());
                statement.setLong(2, area.getId());
                return statement.executeUpdate() > 0;
            }
        });
    }

    @Override
    public boolean excluir(Long id) throws SQLException {
        return consultar(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM areas WHERE id = ?")) {
                statement.setLong(1, id);
                return statement.executeUpdate() > 0;
            }
        });
    }
}

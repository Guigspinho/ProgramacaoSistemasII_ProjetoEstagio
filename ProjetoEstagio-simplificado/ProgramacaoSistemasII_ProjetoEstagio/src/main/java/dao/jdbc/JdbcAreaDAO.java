package dao.jdbc;

import dao.AreaDAO;
import exception.PersistenciaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Area;

public class JdbcAreaDAO implements AreaDAO {
    private static final String SELECT_AREAS = "SELECT id AS area_id, nome AS area_nome FROM areas ";

    private final ConexaoSupabase conexao;

    public JdbcAreaDAO(ConexaoSupabase conexao) {
        this.conexao = conexao;
    }

    @Override
    public Area cadastrar(Area area) {
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO areas (nome) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, area.getNome());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                area.setId(keys.getLong("id"));
            }
            return area;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao cadastrar a área.", e);
        }
    }

    @Override
    public List<Area> listarTodas() {
        List<Area> areas = new ArrayList<>();
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(SELECT_AREAS + "ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                areas.add(JdbcMapeadores.area(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar as áreas.", e);
        }
        return areas;
    }

    @Override
    public Area buscarPorId(Long id) {
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(SELECT_AREAS + "WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return JdbcMapeadores.area(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar a área.", e);
        }
    }

    @Override
    public Area buscarPorNome(String nome) {
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(SELECT_AREAS + "WHERE lower(nome) = lower(?)")) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return JdbcMapeadores.area(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar a área pelo nome.", e);
        }
    }

    @Override
    public boolean atualizar(Area area) {
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement("UPDATE areas SET nome = ? WHERE id = ?")) {
            ps.setString(1, area.getNome());
            ps.setLong(2, area.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar a área.", e);
        }
    }

    @Override
    public boolean excluir(Long id) {
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement("DELETE FROM areas WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao excluir a área.", e);
        }
    }

    @Override
    public boolean estaEmUso(Long areaId) {
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM vagas WHERE area_id = ?")) {
            ps.setLong(1, areaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao verificar o uso da área.", e);
        }
    }
}

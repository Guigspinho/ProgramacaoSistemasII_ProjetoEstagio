package dao.jdbc;

import dao.VagaDAO;
import exception.PersistenciaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Vaga;

public class JdbcVagaDAO implements VagaDAO {
    private final ConexaoSupabase conexao;

    public JdbcVagaDAO(ConexaoSupabase conexao) {
        this.conexao = conexao;
    }

    @Override
    public Vaga publicar(Vaga vaga) {
        String sql = "INSERT INTO vagas (empresa_id, area_id, titulo, descricao, localizacao, "
                + "modalidade, carga_horaria_semanal) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, vaga.getEmpresa().getId());
            ps.setLong(2, vaga.getArea().getId());
            ps.setString(3, vaga.getTitulo());
            ps.setString(4, vaga.getDescricao());
            ps.setString(5, vaga.getLocalizacao());
            ps.setString(6, vaga.getModalidade().name());
            ps.setInt(7, vaga.getCargaHorariaSemanal());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                vaga.setId(keys.getLong("id"));
            }
            return vaga;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao publicar a vaga.", e);
        }
    }

    @Override
    public Vaga buscarPorId(Long id) {
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(JdbcMapeadores.SELECT_VAGAS + "WHERE v.id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return JdbcMapeadores.vaga(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar a vaga.", e);
        }
    }

    @Override
    public List<Vaga> listarAbertas() {
        List<Vaga> vagas = new ArrayList<>();
        String sql = JdbcMapeadores.SELECT_VAGAS + "WHERE v.situacao = 'ABERTA' ORDER BY v.id";
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vagas.add(JdbcMapeadores.vaga(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar as vagas abertas.", e);
        }
        return vagas;
    }

    @Override
    public List<Vaga> listarPorEmpresa(Long empresaId) {
        List<Vaga> vagas = new ArrayList<>();
        String sql = JdbcMapeadores.SELECT_VAGAS + "WHERE v.empresa_id = ? ORDER BY v.id";
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, empresaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vagas.add(JdbcMapeadores.vaga(rs));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar as vagas da empresa.", e);
        }
        return vagas;
    }

    @Override
    public boolean atualizar(Vaga vaga) {
        String sql = "UPDATE vagas SET area_id = ?, titulo = ?, descricao = ?, localizacao = ?, "
                + "modalidade = ?, carga_horaria_semanal = ? "
                + "WHERE id = ? AND empresa_id = ? AND situacao = 'ABERTA'";
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, vaga.getArea().getId());
            ps.setString(2, vaga.getTitulo());
            ps.setString(3, vaga.getDescricao());
            ps.setString(4, vaga.getLocalizacao());
            ps.setString(5, vaga.getModalidade().name());
            ps.setInt(6, vaga.getCargaHorariaSemanal());
            ps.setLong(7, vaga.getId());
            ps.setLong(8, vaga.getEmpresa().getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar a vaga.", e);
        }
    }

    @Override
    public boolean encerrar(Long vagaId) {
        String sql = "UPDATE vagas SET situacao = 'ENCERRADA' WHERE id = ? AND situacao = 'ABERTA'";
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, vagaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao encerrar a vaga.", e);
        }
    }
}

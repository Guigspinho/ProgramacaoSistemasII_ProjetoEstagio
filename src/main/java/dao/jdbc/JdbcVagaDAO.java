package dao.jdbc;

import dao.VagaDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Modalidade;
import model.SituacaoVaga;
import model.Vaga;
import service.jdbc.ConexaoSupabase;

public final class JdbcVagaDAO extends JdbcDaoSupport implements VagaDAO {
    public JdbcVagaDAO(ConexaoSupabase conexoes) {
        super(conexoes);
    }

    @Override
    public Vaga publicar(Vaga vaga) throws SQLException {
        return consultar(connection -> {
            String sql = """
                    INSERT INTO vagas
                        (empresa_id, area_id, titulo, descricao, localizacao, modalidade, carga_horaria_semanal)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, vaga.getEmpresa().getId());
                statement.setLong(2, vaga.getArea().getId());
                statement.setString(3, vaga.getTitulo().trim());
                statement.setString(4, vaga.getDescricao());
                statement.setString(5, vaga.getLocalizacao());
                statement.setString(6, vaga.getModalidade().name());
                statement.setInt(7, vaga.getCargaHorariaSemanal());
                long id = executarInsertGerandoId(statement);
                return new Vaga(id, vaga.getEmpresa(), vaga.getTitulo(), vaga.getDescricao(),
                        vaga.getArea(), vaga.getLocalizacao(), vaga.getModalidade(),
                        vaga.getCargaHorariaSemanal());
            }
        });
    }

    @Override
    public Optional<Vaga> buscarPorId(Long id) throws SQLException {
        return consultar(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    JdbcMapeadores.SELECT_VAGAS + " WHERE v.id = ?")) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    return rs.next() ? Optional.of(JdbcMapeadores.vaga(rs)) : Optional.empty();
                }
            }
        });
    }

    @Override
    public List<Vaga> listarTodas() throws SQLException {
        return listar(null, null, null, null);
    }

    @Override
    public List<Vaga> listarPorEmpresa(Long empresaId) throws SQLException {
        return consultar(connection -> {
            List<Vaga> vagas = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(
                    JdbcMapeadores.SELECT_VAGAS + " WHERE v.empresa_id = ? ORDER BY v.id")) {
                statement.setLong(1, empresaId);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) vagas.add(JdbcMapeadores.vaga(rs));
                }
            }
            return List.copyOf(vagas);
        });
    }

    @Override
    public List<Vaga> listarComFiltros(Long areaId, Modalidade modalidade, SituacaoVaga situacao,
                                       String termo) throws SQLException {
        return listar(areaId, modalidade, situacao, termo);
    }

    private List<Vaga> listar(Long areaId, Modalidade modalidade, SituacaoVaga situacao, String termo)
            throws SQLException {
        return consultar(connection -> {
            StringBuilder sql = new StringBuilder(JdbcMapeadores.SELECT_VAGAS);
            List<Object> parametros = new ArrayList<>();
            List<String> filtros = new ArrayList<>();
            if (areaId != null) {
                filtros.add("v.area_id = ?");
                parametros.add(areaId);
            }
            if (modalidade != null) {
                filtros.add("v.modalidade = ?");
                parametros.add(modalidade.name());
            }
            if (situacao != null) {
                filtros.add("v.situacao = ?");
                parametros.add(situacao.name());
            }
            if (termo != null && !termo.isBlank()) {
                filtros.add("(v.titulo ILIKE ? OR coalesce(v.descricao, '') ILIKE ?)");
                String busca = "%" + termo.trim() + "%";
                parametros.add(busca);
                parametros.add(busca);
            }
            if (!filtros.isEmpty()) sql.append(" WHERE ").append(String.join(" AND ", filtros));
            sql.append(" ORDER BY v.id");

            List<Vaga> vagas = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < parametros.size(); i++) statement.setObject(i + 1, parametros.get(i));
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) vagas.add(JdbcMapeadores.vaga(rs));
                }
            }
            return List.copyOf(vagas);
        });
    }

    @Override
    public boolean atualizar(Vaga vaga) throws SQLException {
        return consultar(connection -> {
            String sql = """
                    UPDATE vagas
                    SET area_id = ?, titulo = ?, descricao = ?, localizacao = ?,
                        modalidade = ?, carga_horaria_semanal = ?
                    WHERE id = ? AND empresa_id = ? AND situacao = 'ABERTA'
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, vaga.getArea().getId());
                statement.setString(2, vaga.getTitulo().trim());
                statement.setString(3, vaga.getDescricao());
                statement.setString(4, vaga.getLocalizacao());
                statement.setString(5, vaga.getModalidade().name());
                statement.setInt(6, vaga.getCargaHorariaSemanal());
                statement.setLong(7, vaga.getId());
                statement.setLong(8, vaga.getEmpresa().getId());
                return statement.executeUpdate() > 0;
            }
        });
    }

    @Override
    public boolean encerrar(Long vagaId) throws SQLException {
        return consultar(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE vagas SET situacao = 'ENCERRADA' WHERE id = ? AND situacao = 'ABERTA'")) {
                statement.setLong(1, vagaId);
                return statement.executeUpdate() > 0;
            }
        });
    }
}

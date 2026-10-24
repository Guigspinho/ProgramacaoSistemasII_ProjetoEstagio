package dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.Administrador;
import model.Area;
import model.Candidatura;
import model.Empresa;
import model.Estudante;
import model.Modalidade;
import model.Perfil;
import model.SituacaoVaga;
import model.Usuario;
import model.Vaga;

final class JdbcMapeadores {
    static final String SELECT_VAGAS = """
            SELECT v.id AS vaga_id, v.titulo AS vaga_titulo, v.descricao AS vaga_descricao,
                   v.localizacao AS vaga_localizacao, v.modalidade AS vaga_modalidade,
                   v.carga_horaria_semanal AS vaga_carga, v.situacao AS vaga_situacao,
                   a.id AS area_id, a.nome AS area_nome,
                   u.id AS empresa_id, u.nome AS empresa_responsavel, u.email AS empresa_email,
                   e.nome_empresa AS empresa_nome
            FROM vagas v
            JOIN areas a ON a.id = v.area_id
            JOIN empresas e ON e.usuario_id = v.empresa_id
            JOIN usuarios u ON u.id = e.usuario_id
            """;

    private JdbcMapeadores() { }

    static Usuario usuario(ResultSet rs) throws SQLException {
        long id = rs.getLong("usuario_id");
        String nome = rs.getString("usuario_nome");
        String email = rs.getString("usuario_email");
        return switch (Perfil.valueOf(rs.getString("usuario_perfil"))) {
            case ADMINISTRADOR -> new Administrador(id, nome, email);
            case EMPRESA -> new Empresa(id, nome, email, rs.getString("empresa_nome"));
            case ESTUDANTE -> new Estudante(id, nome, email, rs.getString("estudante_curso"));
        };
    }

    static Estudante estudante(ResultSet rs) throws SQLException {
        return new Estudante(rs.getLong("estudante_id"), rs.getString("estudante_nome"),
                rs.getString("estudante_email"), rs.getString("estudante_curso"));
    }

    static Empresa empresa(ResultSet rs) throws SQLException {
        return new Empresa(rs.getLong("empresa_id"), rs.getString("empresa_responsavel"),
                rs.getString("empresa_email"), rs.getString("empresa_nome"));
    }

    static Area area(ResultSet rs) throws SQLException {
        return new Area(rs.getLong("area_id"), rs.getString("area_nome"));
    }

    static Vaga vaga(ResultSet rs) throws SQLException {
        Vaga vaga = new Vaga(rs.getLong("vaga_id"), empresa(rs), rs.getString("vaga_titulo"),
                rs.getString("vaga_descricao"), area(rs), rs.getString("vaga_localizacao"),
                Modalidade.valueOf(rs.getString("vaga_modalidade")), rs.getInt("vaga_carga"));
        if (SituacaoVaga.ENCERRADA.name().equals(rs.getString("vaga_situacao"))) vaga.encerrar();
        return vaga;
    }

    static Candidatura candidatura(ResultSet rs) throws SQLException {
        return new Candidatura(rs.getLong("candidatura_id"), estudante(rs), vaga(rs),
                rs.getDate("data_inscricao").toLocalDate());
    }
}

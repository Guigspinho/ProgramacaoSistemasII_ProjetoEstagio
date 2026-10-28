package dao.jdbc;

import dao.UsuarioDAO;
import exception.PersistenciaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Empresa;
import model.Estudante;
import model.Usuario;

public class JdbcUsuarioDAO implements UsuarioDAO {
    private static final String SELECT_USUARIOS =
            "SELECT u.id AS usuario_id, u.nome AS usuario_nome, u.email AS usuario_email, "
            + "u.perfil AS usuario_perfil, e.curso AS estudante_curso, em.nome_empresa AS empresa_nome "
            + "FROM usuarios u "
            + "LEFT JOIN estudantes e ON e.usuario_id = u.id "
            + "LEFT JOIN empresas em ON em.usuario_id = u.id ";

    private final ConexaoSupabase conexao;

    public JdbcUsuarioDAO(ConexaoSupabase conexao) {
        this.conexao = conexao;
    }

    /** Grava em duas tabelas (usuarios e estudantes): por isso usa transação. */
    @Override
    public Estudante cadastrarEstudante(Estudante estudante) {
        try (Connection con = conexao.abrir()) {
            con.setAutoCommit(false);
            try {
                long id = inserirUsuario(con, estudante);
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO estudantes (usuario_id, curso) VALUES (?, ?)")) {
                    ps.setLong(1, id);
                    ps.setString(2, estudante.getCurso());
                    ps.executeUpdate();
                }
                con.commit();
                estudante.setId(id);
                return estudante;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao cadastrar o estudante.", e);
        }
    }

    /** Grava em duas tabelas (usuarios e empresas): por isso usa transação. */
    @Override
    public Empresa cadastrarEmpresa(Empresa empresa) {
        try (Connection con = conexao.abrir()) {
            con.setAutoCommit(false);
            try {
                long id = inserirUsuario(con, empresa);
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO empresas (usuario_id, nome_empresa) VALUES (?, ?)")) {
                    ps.setLong(1, id);
                    ps.setString(2, empresa.getNomeEmpresa());
                    ps.executeUpdate();
                }
                con.commit();
                empresa.setId(id);
                return empresa;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao cadastrar a empresa.", e);
        }
    }

    /** Insere na tabela usuarios (dados comuns) e devolve o ID gerado. */
    private long inserirUsuario(Connection con, Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, perfil) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPerfil().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong("id");
            }
        }
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(SELECT_USUARIOS + "WHERE u.email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return JdbcMapeadores.usuario(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar usuário por e-mail.", e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection con = conexao.abrir();
             PreparedStatement ps = con.prepareStatement(SELECT_USUARIOS + "ORDER BY u.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(JdbcMapeadores.usuario(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar usuários.", e);
        }
        return usuarios;
    }
}

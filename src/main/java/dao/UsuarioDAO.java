package dao;

import model.Usuario;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Define as consultas comuns aos usuários do sistema.
 * A implementação JDBC ficará no pacote dao.jdbc.
 */
public interface UsuarioDAO {

    /** Busca um usuário pelo identificador. */
    Optional<Usuario> buscarPorId(Long id) throws SQLException;

    /** Busca um usuário pelo e-mail. */
    Optional<Usuario> buscarPorEmail(String email) throws SQLException;

    /** Lista os usuários cadastrados, incluindo seus perfis concretos. */
    List<Usuario> listarTodos() throws SQLException;
}

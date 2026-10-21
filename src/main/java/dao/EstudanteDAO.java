package dao;

import model.Estudante;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Define as operações de persistência específicas dos estudantes. */
public interface EstudanteDAO {

    /** Cadastra o estudante e retorna o objeto com o ID gerado pelo banco. */
    Estudante cadastrar(Estudante estudante) throws SQLException;

    /** Busca um estudante pelo ID. */
    Optional<Estudante> buscarPorId(Long id) throws SQLException;

    /** Lista os estudantes cadastrados. */
    List<Estudante> listarTodos() throws SQLException;
}

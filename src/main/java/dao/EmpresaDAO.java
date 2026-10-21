package dao;

import model.Empresa;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Define as operações de persistência específicas das empresas. */
public interface EmpresaDAO {

    /** Cadastra a empresa e retorna o objeto com o ID gerado pelo banco. */
    Empresa cadastrar(Empresa empresa) throws SQLException;

    /** Busca uma empresa pelo ID. */
    Optional<Empresa> buscarPorId(Long id) throws SQLException;

    /** Lista as empresas cadastradas. */
    List<Empresa> listarTodas() throws SQLException;
}

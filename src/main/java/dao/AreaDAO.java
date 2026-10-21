package dao;
import model.Area;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Define as operações de persistência para áreas. */
public interface AreaDAO {

    /** Cadastra uma área e retorna o objeto com o ID gerado pelo banco. */
    Area cadastrar(Area area) throws SQLException;

    /** Lista todas as áreas. */
    List<Area> listarTodas() throws SQLException;

    /** Busca uma área pelo ID. */
    Optional<Area> buscarPorId(Long id) throws SQLException;

    /** Atualiza a área; retorna false quando nenhum registro foi alterado. */
    boolean atualizar(Area area) throws SQLException;

    /** Exclui a área; retorna false quando nenhum registro foi excluído. */
    boolean excluir(Long id) throws SQLException;
}

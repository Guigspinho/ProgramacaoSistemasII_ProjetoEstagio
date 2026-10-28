package dao;

import java.util.List;
import model.Area;

/** Operações de persistência para áreas. */
public interface AreaDAO {

    /** Grava a área e devolve o objeto com o ID gerado pelo banco. */
    Area cadastrar(Area area);

    List<Area> listarTodas();

    /** Devolve a área ou null se o ID não existir. */
    Area buscarPorId(Long id);

    /** Devolve a área ou null se o nome não existir (ignora maiúsculas/minúsculas). */
    Area buscarPorNome(String nome);

    /** Atualiza o nome; devolve false se nenhum registro foi alterado. */
    boolean atualizar(Area area);

    /** Exclui a área; devolve false se nenhum registro foi excluído. */
    boolean excluir(Long id);

    /** Indica se alguma vaga usa esta área. */
    boolean estaEmUso(Long areaId);
}

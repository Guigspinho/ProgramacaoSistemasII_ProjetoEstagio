package dao;

import java.util.List;
import model.Vaga;

/** Operações de persistência para vagas. */
public interface VagaDAO {

    /** Grava a vaga e devolve o objeto com o ID gerado pelo banco. */
    Vaga publicar(Vaga vaga);

    /** Devolve a vaga ou null se o ID não existir. */
    Vaga buscarPorId(Long id);

    /** Somente vagas com situação ABERTA. */
    List<Vaga> listarAbertas();

    /** Vagas (abertas e encerradas) de uma empresa. */
    List<Vaga> listarPorEmpresa(Long empresaId);

    /** Atualiza uma vaga aberta; devolve false se nenhum registro foi alterado. */
    boolean atualizar(Vaga vaga);

    /** Encerra uma vaga aberta; devolve false se nenhum registro foi alterado. */
    boolean encerrar(Long vagaId);
}

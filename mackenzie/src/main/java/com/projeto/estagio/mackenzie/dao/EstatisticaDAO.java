package com.projeto.estagio.mackenzie.dao;

import com.projeto.estagio.mackenzie.model.SituacaoVaga;

/** Consultas de contagem usadas pelo administrador. */
public interface EstatisticaDAO {

    long contarEstudantes();

    long contarEmpresas();

    long contarVagasPorSituacao(SituacaoVaga situacao);

    long contarCandidaturas();
}

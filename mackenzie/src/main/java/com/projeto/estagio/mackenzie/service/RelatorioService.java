package com.projeto.estagio.mackenzie.service;

import com.projeto.estagio.mackenzie.dao.EstatisticaDAO;
import com.projeto.estagio.mackenzie.model.Perfil;
import com.projeto.estagio.mackenzie.model.SituacaoVaga;
import com.projeto.estagio.mackenzie.model.Usuario;

public class RelatorioService {
    private final EstatisticaDAO estatisticaDAO;

    public RelatorioService(EstatisticaDAO estatisticaDAO) {
        this.estatisticaDAO = estatisticaDAO;
    }

    /** Rótulo -> quantidade. Somente o administrador pode consultar. */
    public long[] contagens(Usuario ator) {
        Validacao.exigirPerfil(ator, Perfil.ADMINISTRADOR, "ver as contagens");

        return new long[] {
                estatisticaDAO.contarEstudantes(),
                estatisticaDAO.contarEmpresas(),
                estatisticaDAO.contarVagasPorSituacao(SituacaoVaga.ABERTA),
                estatisticaDAO.contarVagasPorSituacao(SituacaoVaga.ENCERRADA),
                estatisticaDAO.contarCandidaturas()
        };
    }
}

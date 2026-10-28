package service;

import dao.EstatisticaDAO;
import model.Perfil;
import model.SituacaoVaga;
import model.Usuario;

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

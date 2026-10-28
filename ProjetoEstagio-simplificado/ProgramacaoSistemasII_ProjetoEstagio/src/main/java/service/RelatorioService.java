package service;

import dao.EstatisticaDAO;
import java.util.LinkedHashMap;
import java.util.Map;
import model.Perfil;
import model.SituacaoVaga;
import model.Usuario;

public class RelatorioService {
    private final EstatisticaDAO estatisticaDAO;

    public RelatorioService(EstatisticaDAO estatisticaDAO) {
        this.estatisticaDAO = estatisticaDAO;
    }

    /** Rótulo -> quantidade. Somente o administrador pode consultar. */
    public Map<String, Long> contagens(Usuario ator) {
        Validacao.exigirPerfil(ator, Perfil.ADMINISTRADOR, "ver as contagens");

        Map<String, Long> contagens = new LinkedHashMap<>();
        contagens.put("Estudantes", estatisticaDAO.contarEstudantes());
        contagens.put("Empresas", estatisticaDAO.contarEmpresas());
        contagens.put("Vagas abertas", estatisticaDAO.contarVagasPorSituacao(SituacaoVaga.ABERTA));
        contagens.put("Vagas encerradas", estatisticaDAO.contarVagasPorSituacao(SituacaoVaga.ENCERRADA));
        contagens.put("Candidaturas", estatisticaDAO.contarCandidaturas());
        return contagens;
    }
}

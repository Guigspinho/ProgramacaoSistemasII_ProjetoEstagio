package service;

import dao.CandidaturaDAO;
import dao.VagaDAO;
import exception.AcessoNegadoException;
import exception.RegraNegocioException;
import java.time.LocalDate;
import java.util.List;
import model.Candidatura;
import model.Estudante;
import model.Perfil;
import model.Usuario;
import model.Vaga;

public class CandidaturaService {
    private final CandidaturaDAO candidaturaDAO;
    private final VagaDAO vagaDAO;

    public CandidaturaService(CandidaturaDAO candidaturaDAO, VagaDAO vagaDAO) {
        this.candidaturaDAO = candidaturaDAO;
        this.vagaDAO = vagaDAO;
    }

    public Candidatura candidatar(Usuario ator, Long vagaId) {
        Validacao.exigirPerfil(ator, Perfil.ESTUDANTE, "se candidatar");
        Vaga vaga = buscarVaga(vagaId);
        if (!vaga.estaAberta()) {
            throw new RegraNegocioException("A vaga está encerrada.");
        }
        if (candidaturaDAO.existePorEstudanteEVaga(ator.getId(), vagaId)) {
            throw new RegraNegocioException("Você já se candidatou a esta vaga.");
        }

        Candidatura candidatura = new Candidatura(null, (Estudante) ator, vaga, LocalDate.now());
        return candidaturaDAO.cadastrar(candidatura);
    }

    /** O estudante só enxerga as próprias candidaturas. */
    public List<Candidatura> listarMinhas(Usuario ator) {
        Validacao.exigirPerfil(ator, Perfil.ESTUDANTE, "consultar candidaturas");
        return candidaturaDAO.listarPorEstudante(ator.getId());
    }

    /** A empresa só enxerga os candidatos das suas próprias vagas. */
    public List<Candidatura> listarCandidatos(Usuario ator, Long vagaId) {
        Validacao.exigirPerfil(ator, Perfil.EMPRESA, "ver candidatos");
        Vaga vaga = buscarVaga(vagaId);
        if (!vaga.getEmpresa().getId().equals(ator.getId())) {
            throw new AcessoNegadoException("Esta vaga pertence a outra empresa.");
        }
        return candidaturaDAO.listarPorVaga(vagaId);
    }

    private Vaga buscarVaga(Long vagaId) {
        Vaga vaga = vagaDAO.buscarPorId(vagaId);
        if (vaga == null) {
            throw new RegraNegocioException("Vaga " + vagaId + " não encontrada.");
        }
        return vaga;
    }
}

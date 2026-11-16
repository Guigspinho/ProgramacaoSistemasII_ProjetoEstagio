package com.projeto.estagio.mackenzie.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.projeto.estagio.mackenzie.dao.CandidaturaDAO;
import com.projeto.estagio.mackenzie.dao.VagaDAO;
import com.projeto.estagio.mackenzie.exception.AcessoNegadoException;
import com.projeto.estagio.mackenzie.exception.RegraNegocioException;
import com.projeto.estagio.mackenzie.model.Area;
import com.projeto.estagio.mackenzie.model.Candidatura;
import com.projeto.estagio.mackenzie.model.Empresa;
import com.projeto.estagio.mackenzie.model.Estudante;
import com.projeto.estagio.mackenzie.model.Modalidade;
import com.projeto.estagio.mackenzie.model.SituacaoVaga;
import com.projeto.estagio.mackenzie.model.Vaga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CandidaturaServiceTest {
    private static final Long VAGA_ID = 30L;
    private static final Long EMPRESA_PROPRIETARIA_ID = 10L;
    private static final Long OUTRA_EMPRESA_ID = 20L;
    private static final Long ESTUDANTE_ID = 40L;

    private CandidaturaDAO candidaturaDAO;
    private VagaDAO vagaDAO;
    private CandidaturaService service;

    @BeforeEach
    void configurar() {
        candidaturaDAO = mock(CandidaturaDAO.class);
        vagaDAO = mock(VagaDAO.class);
        service = new CandidaturaService(candidaturaDAO, vagaDAO);
    }

    @Test
    void rejeitaSegundaCandidaturaDoMesmoEstudanteNaMesmaVaga() {
        Estudante estudante = estudante();
        when(vagaDAO.buscarPorId(VAGA_ID)).thenReturn(vaga(empresaProprietaria()));
        when(candidaturaDAO.existePorEstudanteEVaga(ESTUDANTE_ID, VAGA_ID))
                .thenReturn(false, true);
        when(candidaturaDAO.cadastrar(any(Candidatura.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        service.candidatar(estudante, VAGA_ID);

        assertThrows(RegraNegocioException.class, () -> service.candidatar(estudante, VAGA_ID));
        verify(candidaturaDAO).cadastrar(any(Candidatura.class));
    }

    @Test
    void rejeitaCandidaturaEmVagaEncerrada() {
        Vaga vaga = vaga(empresaProprietaria());
        vaga.setSituacao(SituacaoVaga.ENCERRADA);
        when(vagaDAO.buscarPorId(VAGA_ID)).thenReturn(vaga);

        assertThrows(RegraNegocioException.class, () -> service.candidatar(estudante(), VAGA_ID));
        verify(candidaturaDAO, never()).existePorEstudanteEVaga(ESTUDANTE_ID, VAGA_ID);
        verify(candidaturaDAO, never()).cadastrar(any(Candidatura.class));
    }

    @Test
    void impedeEmpresaDeConsultarCandidatosDeVagaDeOutraEmpresa() {
        when(vagaDAO.buscarPorId(VAGA_ID)).thenReturn(vaga(empresaProprietaria()));

        assertThrows(AcessoNegadoException.class,
                () -> service.listarCandidatos(outraEmpresa(), VAGA_ID));
        verify(candidaturaDAO, never()).listarPorVaga(VAGA_ID);
    }

    private static Estudante estudante() {
        return new Estudante(ESTUDANTE_ID, "Estudante Teste", "estudante@teste.com", "Sistemas");
    }

    private static Empresa empresaProprietaria() {
        return new Empresa(EMPRESA_PROPRIETARIA_ID, "Empresa Proprietária",
                "proprietaria@teste.com", "Empresa A");
    }

    private static Empresa outraEmpresa() {
        return new Empresa(OUTRA_EMPRESA_ID, "Outra Empresa", "outra@teste.com", "Empresa B");
    }

    private static Vaga vaga(Empresa proprietaria) {
        return new Vaga(VAGA_ID, proprietaria, "Estágio de teste", "Descrição",
                new Area(1L, "Tecnologia"), "São Paulo", Modalidade.HIBRIDA, 20);
    }
}

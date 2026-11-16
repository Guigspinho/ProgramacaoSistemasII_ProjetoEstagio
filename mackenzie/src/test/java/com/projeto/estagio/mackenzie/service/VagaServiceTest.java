package com.projeto.estagio.mackenzie.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.projeto.estagio.mackenzie.dao.AreaDAO;
import com.projeto.estagio.mackenzie.dao.VagaDAO;
import com.projeto.estagio.mackenzie.model.Area;
import com.projeto.estagio.mackenzie.model.Empresa;
import com.projeto.estagio.mackenzie.model.Modalidade;
import com.projeto.estagio.mackenzie.model.Vaga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VagaServiceTest {
    private static final Long AREA_ID = 1L;
    private static final Long EMPRESA_ID = 10L;

    private AreaDAO areaDAO;
    private VagaDAO vagaDAO;
    private VagaService service;

    @BeforeEach
    void configurar() {
        areaDAO = mock(AreaDAO.class);
        vagaDAO = mock(VagaDAO.class);
        service = new VagaService(vagaDAO, areaDAO);
    }

    @Test
    void publicaVagaComTodosOsCamposObrigatorios() {
        when(areaDAO.buscarPorId(AREA_ID)).thenReturn(new Area(AREA_ID, "Tecnologia"));
        when(vagaDAO.publicar(any(Vaga.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Vaga vaga = service.publicar(empresa(), "Estágio Java", "Desenvolvimento back-end",
                AREA_ID, "São Paulo", Modalidade.HIBRIDA, 20);

        assertEquals("Estágio Java", vaga.getTitulo());
        assertEquals("Desenvolvimento back-end", vaga.getDescricao());
        assertEquals("São Paulo", vaga.getLocalizacao());
        verify(vagaDAO).publicar(any(Vaga.class));
    }

    @Test
    void rejeitaTituloOuModalidadeAusente() {
        assertThrows(IllegalArgumentException.class,
                () -> service.publicar(empresa(), null, "Desenvolvimento",
                        AREA_ID, "São Paulo", Modalidade.HIBRIDA, 20));
        assertThrows(IllegalArgumentException.class,
                () -> service.publicar(empresa(), "Estágio Java", "Desenvolvimento",
                        AREA_ID, "São Paulo", null, 20));

        verify(vagaDAO, never()).publicar(any(Vaga.class));
    }

    @Test
    void rejeitaDescricaoAusenteOuEmBranco() {
        assertThrows(IllegalArgumentException.class,
                () -> service.publicar(empresa(), "Estágio Java", null,
                        AREA_ID, "São Paulo", Modalidade.HIBRIDA, 20));
        assertThrows(IllegalArgumentException.class,
                () -> service.publicar(empresa(), "Estágio Java", " ",
                        AREA_ID, "São Paulo", Modalidade.HIBRIDA, 20));

        verify(vagaDAO, never()).publicar(any(Vaga.class));
    }

    @Test
    void rejeitaLocalizacaoAusenteOuEmBranco() {
        assertThrows(IllegalArgumentException.class,
                () -> service.publicar(empresa(), "Estágio Java", "Desenvolvimento",
                        AREA_ID, null, Modalidade.HIBRIDA, 20));
        assertThrows(IllegalArgumentException.class,
                () -> service.publicar(empresa(), "Estágio Java", "Desenvolvimento",
                        AREA_ID, " ", Modalidade.HIBRIDA, 20));

        verify(vagaDAO, never()).publicar(any(Vaga.class));
    }

    @Test
    void rejeitaAreaAusenteECargaHorariaForaDoLimite() {
        assertThrows(IllegalArgumentException.class,
                () -> service.publicar(empresa(), "Estágio Java", "Desenvolvimento",
                        null, "São Paulo", Modalidade.HIBRIDA, 20));
        assertThrows(IllegalArgumentException.class,
                () -> service.publicar(empresa(), "Estágio Java", "Desenvolvimento",
                        AREA_ID, "São Paulo", Modalidade.HIBRIDA, 0));
        assertThrows(IllegalArgumentException.class,
                () -> service.publicar(empresa(), "Estágio Java", "Desenvolvimento",
                        AREA_ID, "São Paulo", Modalidade.HIBRIDA, 31));

        verify(vagaDAO, never()).publicar(any(Vaga.class));
    }

    private static Empresa empresa() {
        return new Empresa(EMPRESA_ID, "Responsável", "rh@exemplo.com", "Empresa Exemplo");
    }
}

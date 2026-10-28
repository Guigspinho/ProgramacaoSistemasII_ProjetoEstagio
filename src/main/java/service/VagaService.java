package service;

import dao.AreaDAO;
import dao.VagaDAO;
import exception.AcessoNegadoException;
import exception.RegraNegocioException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import model.Area;
import model.Empresa;
import model.Modalidade;
import model.Perfil;
import model.Usuario;
import model.Vaga;

public class VagaService {
    private static final int CARGA_MINIMA = 1;
    private static final int CARGA_MAXIMA = 30;

    private final VagaDAO vagaDAO;
    private final AreaDAO areaDAO;

    public VagaService(VagaDAO vagaDAO, AreaDAO areaDAO) {
        this.vagaDAO = vagaDAO;
        this.areaDAO = areaDAO;
    }

    public Vaga publicar(Usuario ator, String titulo, String descricao, Long areaId,
                         String localizacao, Modalidade modalidade, int cargaHorariaSemanal) {
        Validacao.exigirPerfil(ator, Perfil.EMPRESA, "publicar vagas");
        String tituloValido = Validacao.textoObrigatorio(titulo, "Título");
        validarModalidadeECarga(modalidade, cargaHorariaSemanal);
        Area area = buscarArea(areaId);

        Vaga vaga = new Vaga(null, (Empresa) ator, tituloValido, descricao, area,
                localizacao, modalidade, cargaHorariaSemanal);
        return vagaDAO.publicar(vaga);
    }

    public Vaga editar(Usuario ator, Long vagaId, String titulo, String descricao, Long areaId,
                       String localizacao, Modalidade modalidade, int cargaHorariaSemanal) {
        Vaga vaga = buscarVagaEditavel(ator, vagaId);
        String tituloValido = Validacao.textoObrigatorio(titulo, "Título");
        validarModalidadeECarga(modalidade, cargaHorariaSemanal);
        Area area = buscarArea(areaId);

        vaga.setTitulo(tituloValido);
        vaga.setDescricao(descricao);
        vaga.setArea(area);
        vaga.setLocalizacao(localizacao);
        vaga.setModalidade(modalidade);
        vaga.setCargaHorariaSemanal(cargaHorariaSemanal);

        if (!vagaDAO.atualizar(vaga)) {
            throw new RegraNegocioException("A vaga não está mais disponível.");
        }
        return vaga;
    }

    public void validarPodeEditar(Usuario ator, Long vagaId) {
        buscarVagaEditavel(ator, vagaId);
    }

    public void encerrar(Usuario ator, Long vagaId) {
        Vaga vaga = buscarVagaDaEmpresa(ator, vagaId);
        if (!vaga.estaAberta()) {
            throw new RegraNegocioException("A vaga já está encerrada.");
        }
        vagaDAO.encerrar(vagaId);
    }

    /**
     * Consulta pública de vagas abertas. areaId e modalidade nulos significam "sem filtro".
     *
     * Predicate<Vaga> é uma interface funcional: tem um único método, test(Vaga), que devolve
     * true ou false. A lambda abaixo descreve o critério "a vaga atende aos filtros escolhidos?"
     * e o stream().filter(...) a aplica a cada vaga. Assim, uma mesma lambda cobre todas as
     * combinações (sem filtro, só área, só modalidade ou os dois) sem vários if/else.
     */
    public List<Vaga> listarAbertas(Long areaId, Modalidade modalidade) {
        List<Vaga> abertas = vagaDAO.listarAbertas();

        Predicate<Vaga> atendeFiltros = vaga ->
                (areaId == null || vaga.getArea().getId().equals(areaId))
                && (modalidade == null || vaga.getModalidade() == modalidade);

        return abertas.stream().filter(atendeFiltros).collect(Collectors.toList());
    }

    public List<Vaga> listarDaEmpresa(Usuario ator) {
        Validacao.exigirPerfil(ator, Perfil.EMPRESA, "listar suas vagas");
        return vagaDAO.listarPorEmpresa(ator.getId());
    }

    /** Busca a vaga e confirma que ela pertence à empresa selecionada. */
    private Vaga buscarVagaDaEmpresa(Usuario ator, Long vagaId) {
        Validacao.exigirPerfil(ator, Perfil.EMPRESA, "gerenciar vagas");
        Vaga vaga = vagaDAO.buscarPorId(vagaId);
        if (vaga == null) {
            throw new RegraNegocioException("Vaga " + vagaId + " não encontrada.");
        }
        if (!vaga.getEmpresa().getId().equals(ator.getId())) {
            throw new AcessoNegadoException("Esta vaga pertence a outra empresa.");
        }
        return vaga;
    }

    private Vaga buscarVagaEditavel(Usuario ator, Long vagaId) {
        Vaga vaga = buscarVagaDaEmpresa(ator, vagaId);
        if (!vaga.estaAberta()) {
            throw new RegraNegocioException("Vaga encerrada não pode ser editada.");
        }
        return vaga;
    }

    private Area buscarArea(Long areaId) {
        Area area = areaDAO.buscarPorId(areaId);
        if (area == null) {
            throw new RegraNegocioException("Área " + areaId + " não encontrada.");
        }
        return area;
    }

    private void validarModalidadeECarga(Modalidade modalidade, int carga) {
        if (modalidade == null) {
            throw new IllegalArgumentException("Modalidade obrigatória.");
        }
        if (carga < CARGA_MINIMA || carga > CARGA_MAXIMA) {
            throw new IllegalArgumentException("Carga horária deve estar entre 1 e 30 horas semanais.");
        }
    }
}

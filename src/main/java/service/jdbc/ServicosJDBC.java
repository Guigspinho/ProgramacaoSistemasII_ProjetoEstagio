package service.jdbc;

import dao.AreaDAO;
import dao.CandidaturaDAO;
import dao.EmpresaDAO;
import dao.EstatisticaDAO;
import dao.EstudanteDAO;
import dao.UsuarioDAO;
import dao.VagaDAO;
import dao.jdbc.InicializadorBanco;
import dao.jdbc.JdbcAreaDAO;
import dao.jdbc.JdbcCandidaturaDAO;
import dao.jdbc.JdbcEmpresaDAO;
import dao.jdbc.JdbcEstatisticaDAO;
import dao.jdbc.JdbcEstudanteDAO;
import dao.jdbc.JdbcUsuarioDAO;
import dao.jdbc.JdbcVagaDAO;
import exception.AcessoNegadoException;
import exception.PersistenciaException;
import exception.RegraNegocioException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import model.Area;
import model.Candidatura;
import model.Empresa;
import model.Estudante;
import model.Modalidade;
import model.Perfil;
import model.SituacaoVaga;
import model.Usuario;
import model.Vaga;
import service.AreaService;
import service.CandidaturaService;
import service.RelatorioService;
import service.UsuarioService;
import service.VagaService;

/**
 * Serviços de produção. Aplicam regras do portal e delegam o acesso aos dados
 * para contratos DAO, sem executar SQL nesta camada.
 */
public final class ServicosJDBC implements UsuarioService, AreaService, VagaService,
        CandidaturaService, RelatorioService {
    @FunctionalInterface
    private interface OperacaoDAO<T> {
        T executar() throws SQLException;
    }

    private final UsuarioDAO usuarioDAO;
    private final EstudanteDAO estudanteDAO;
    private final EmpresaDAO empresaDAO;
    private final AreaDAO areaDAO;
    private final VagaDAO vagaDAO;
    private final CandidaturaDAO candidaturaDAO;
    private final EstatisticaDAO estatisticaDAO;

    public ServicosJDBC() {
        ConexaoSupabase conexoes = new ConexaoSupabase();
        new InicializadorBanco(conexoes).inicializar();
        usuarioDAO = new JdbcUsuarioDAO(conexoes);
        estudanteDAO = new JdbcEstudanteDAO(conexoes);
        empresaDAO = new JdbcEmpresaDAO(conexoes);
        areaDAO = new JdbcAreaDAO(conexoes);
        vagaDAO = new JdbcVagaDAO(conexoes);
        candidaturaDAO = new JdbcCandidaturaDAO(conexoes);
        estatisticaDAO = new JdbcEstatisticaDAO(conexoes);
    }

    public ServicosJDBC(UsuarioDAO usuarioDAO, EstudanteDAO estudanteDAO, EmpresaDAO empresaDAO,
                        AreaDAO areaDAO, VagaDAO vagaDAO, CandidaturaDAO candidaturaDAO,
                        EstatisticaDAO estatisticaDAO) {
        this.usuarioDAO = usuarioDAO;
        this.estudanteDAO = estudanteDAO;
        this.empresaDAO = empresaDAO;
        this.areaDAO = areaDAO;
        this.vagaDAO = vagaDAO;
        this.candidaturaDAO = candidaturaDAO;
        this.estatisticaDAO = estatisticaDAO;
    }

    private <T> T executarDAO(OperacaoDAO<T> operacao) {
        try {
            return operacao.executar();
        } catch (SQLException e) {
            String sqlState = e.getSQLState() == null ? "" : e.getSQLState();
            switch (sqlState) {
                case "23505" -> throw new RegraNegocioException(
                        "Já existe um registro com os mesmos dados únicos (e-mail, área ou candidatura).");
                case "23503" -> throw new RegraNegocioException(
                        "A operação referencia um registro inexistente ou ainda utilizado.");
                case "23502", "22P02", "22001" -> throw new RegraNegocioException(
                        "Os dados informados são inválidos para o banco.");
                default -> throw new PersistenciaException("Falha ao acessar o banco de dados.", e);
            }
        }
    }

    private void exigirPerfil(Usuario ator, Perfil perfil, String acao) {
        if (ator == null || ator.getPerfil() != perfil) {
            throw new AcessoNegadoException("Somente " + perfil.name().toLowerCase()
                    + " pode " + acao + ".");
        }
    }

    private static String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(campo + " obrigatório.");
        return valor.trim();
    }

    private static String normalizarEmail(String email) {
        String normalizado = textoObrigatorio(email, "E-mail").toLowerCase();
        if (!normalizado.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        return normalizado;
    }

    private Area buscarArea(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Área inválida.");
        return executarDAO(() -> areaDAO.buscarPorId(id))
                .orElseThrow(() -> new RegraNegocioException("Área " + id + " não encontrada."));
    }

    private Vaga buscarVaga(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Vaga inválida.");
        return executarDAO(() -> vagaDAO.buscarPorId(id))
                .orElseThrow(() -> new RegraNegocioException("Vaga " + id + " não encontrada."));
    }

    private Empresa exigirEmpresa(Usuario ator, String acao) {
        exigirPerfil(ator, Perfil.EMPRESA, acao);
        if (!(ator instanceof Empresa empresa)) {
            throw new AcessoNegadoException("O usuário selecionado não representa uma empresa válida.");
        }
        return empresa;
    }

    private Estudante exigirEstudante(Usuario ator, String acao) {
        exigirPerfil(ator, Perfil.ESTUDANTE, acao);
        if (!(ator instanceof Estudante estudante)) {
            throw new AcessoNegadoException("O usuário selecionado não representa um estudante válido.");
        }
        return estudante;
    }

    private Vaga vagaDaEmpresa(Usuario ator, Long vagaId) {
        Empresa empresa = exigirEmpresa(ator, "gerenciar vagas");
        Vaga vaga = buscarVaga(vagaId);
        if (!vaga.getEmpresa().getId().equals(empresa.getId())) {
            throw new AcessoNegadoException("Esta vaga pertence a outra empresa.");
        }
        return vaga;
    }

    @Override
    public Estudante cadastrarEstudante(String nome, String email, String curso) {
        String nomeValido = textoObrigatorio(nome, "Nome");
        String emailValido = normalizarEmail(email);
        String cursoValido = textoObrigatorio(curso, "Curso");
        return executarDAO(() -> estudanteDAO.cadastrar(
                new Estudante(null, nomeValido, emailValido, cursoValido)));
    }

    @Override
    public Empresa cadastrarEmpresa(String nome, String email, String nomeEmpresa) {
        String nomeValido = textoObrigatorio(nome, "Nome");
        String emailValido = normalizarEmail(email);
        String empresaValida = textoObrigatorio(nomeEmpresa, "Nome da empresa");
        return executarDAO(() -> empresaDAO.cadastrar(
                new Empresa(null, nomeValido, emailValido, empresaValida)));
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return executarDAO(usuarioDAO::listarTodos);
    }

    @Override
    public Area cadastrar(Usuario ator, String nome) {
        exigirPerfil(ator, Perfil.ADMINISTRADOR, "cadastrar áreas");
        String nomeValido = textoObrigatorio(nome, "Nome da área");
        return executarDAO(() -> areaDAO.cadastrar(new Area(null, nomeValido)));
    }

    @Override
    public Area editar(Usuario ator, Long areaId, String novoNome) {
        exigirPerfil(ator, Perfil.ADMINISTRADOR, "editar áreas");
        Area area = buscarArea(areaId);
        String nomeValido = textoObrigatorio(novoNome, "Nome da área");
        Area atualizada = new Area(area.getId(), nomeValido);
        boolean alterada = executarDAO(() -> areaDAO.atualizar(atualizada));
        if (!alterada) throw new RegraNegocioException("A área não foi encontrada.");
        return atualizada;
    }

    @Override
    public void excluir(Usuario ator, Long areaId) {
        exigirPerfil(ator, Perfil.ADMINISTRADOR, "excluir áreas");
        buscarArea(areaId);
        boolean excluida = executarDAO(() -> areaDAO.excluir(areaId));
        if (!excluida) throw new RegraNegocioException("A área não foi encontrada.");
    }

    @Override
    public List<Area> listar() {
        return executarDAO(areaDAO::listarTodas);
    }

    @Override
    public Vaga publicar(Usuario ator, String titulo, String descricao, Long areaId,
                         String localizacao, Modalidade modalidade, int carga) {
        Empresa empresa = exigirEmpresa(ator, "publicar vagas");
        String tituloValido = textoObrigatorio(titulo, "Título");
        Area area = buscarArea(areaId);
        if (modalidade == null) throw new IllegalArgumentException("Modalidade obrigatória.");
        if (carga <= 0) throw new IllegalArgumentException("Carga horária deve ser positiva.");
        Vaga vaga = new Vaga(null, empresa, tituloValido, descricao, area, localizacao, modalidade, carga);
        return executarDAO(() -> vagaDAO.publicar(vaga));
    }

    @Override
    public Vaga editar(Usuario ator, Long vagaId, String titulo, String descricao, Long areaId,
                       String localizacao, Modalidade modalidade, int carga) {
        Vaga atual = vagaDaEmpresa(ator, vagaId);
        if (!atual.estaAberta()) throw new RegraNegocioException("Vaga encerrada não pode ser editada.");
        String tituloValido = textoObrigatorio(titulo, "Título");
        Area area = buscarArea(areaId);
        if (modalidade == null) throw new IllegalArgumentException("Modalidade obrigatória.");
        if (carga <= 0) throw new IllegalArgumentException("Carga horária deve ser positiva.");

        Vaga editada = new Vaga(atual.getId(), atual.getEmpresa(), tituloValido, descricao,
                area, localizacao, modalidade, carga);
        boolean atualizada = executarDAO(() -> vagaDAO.atualizar(editada));
        if (!atualizada) throw new RegraNegocioException("A vaga não está mais disponível.");
        return editada;
    }

    @Override
    public void encerrar(Usuario ator, Long vagaId) {
        Vaga vaga = vagaDaEmpresa(ator, vagaId);
        if (!vaga.estaAberta()) throw new RegraNegocioException("A vaga já está encerrada.");
        boolean encerrada = executarDAO(() -> vagaDAO.encerrar(vagaId));
        if (!encerrada) throw new RegraNegocioException("A vaga já está encerrada.");
    }

    @Override
    public List<Vaga> listarAbertas(Long areaId, Modalidade modalidade) {
        if (areaId != null && areaId <= 0) throw new IllegalArgumentException("Área inválida.");
        List<Vaga> abertas = executarDAO(
                () -> vagaDAO.listarComFiltros(null, null, SituacaoVaga.ABERTA, null));

        Predicate<Vaga> filtro = Vaga::estaAberta;
        if (areaId != null) filtro = filtro.and(v -> v.getArea().getId().equals(areaId));
        if (modalidade != null) filtro = filtro.and(v -> v.getModalidade() == modalidade);
        return abertas.stream().filter(filtro).toList();
    }

    @Override
    public List<Vaga> listarDaEmpresa(Usuario ator) {
        Empresa empresa = exigirEmpresa(ator, "listar suas vagas");
        return executarDAO(() -> vagaDAO.listarPorEmpresa(empresa.getId()));
    }

    @Override
    public Candidatura candidatar(Usuario ator, Long vagaId) {
        Estudante estudante = exigirEstudante(ator, "se candidatar");
        Vaga vaga = buscarVaga(vagaId);
        if (!vaga.estaAberta()) throw new RegraNegocioException("A vaga está encerrada.");
        boolean duplicada = executarDAO(
                () -> candidaturaDAO.existePorEstudanteEVaga(estudante.getId(), vaga.getId()));
        if (duplicada) throw new RegraNegocioException("Você já se candidatou a esta vaga.");
        Candidatura candidatura = new Candidatura(null, estudante, vaga, LocalDate.now());
        return executarDAO(() -> candidaturaDAO.cadastrar(candidatura));
    }

    @Override
    public List<Candidatura> listarMinhas(Usuario ator) {
        Estudante estudante = exigirEstudante(ator, "consultar candidaturas");
        return executarDAO(() -> candidaturaDAO.listarPorEstudante(estudante.getId()));
    }

    @Override
    public List<Candidatura> listarCandidatos(Usuario ator, Long vagaId) {
        Vaga vaga = vagaDaEmpresa(ator, vagaId);
        return executarDAO(() -> candidaturaDAO.listarPorVaga(vaga.getId()));
    }

    @Override
    public Map<String, Long> contagens(Usuario ator) {
        exigirPerfil(ator, Perfil.ADMINISTRADOR, "ver as contagens");
        Map<String, Long> contagens = new LinkedHashMap<>();
        contagens.put("Estudantes", executarDAO(estatisticaDAO::contarEstudantes));
        contagens.put("Empresas", executarDAO(estatisticaDAO::contarEmpresas));
        contagens.put("Vagas abertas", executarDAO(
                () -> estatisticaDAO.contarVagasPorSituacao(SituacaoVaga.ABERTA)));
        contagens.put("Vagas encerradas", executarDAO(
                () -> estatisticaDAO.contarVagasPorSituacao(SituacaoVaga.ENCERRADA)));
        contagens.put("Candidaturas", executarDAO(estatisticaDAO::contarCandidaturas));
        return contagens;
    }
}

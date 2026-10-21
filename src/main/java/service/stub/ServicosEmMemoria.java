package service.stub;

import exception.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import model.*;
import service.*;

/**
 * IMPLEMENTAÇÃO PROVISÓRIA, só para o menu rodar antes de existirem DAO e serviços reais.
 * Guarda tudo em listas na memória e já aplica as regras principais.
 * Quando a Pessoa B entregar os serviços reais (com DAO), basta trocar no Main e apagar este pacote.
 */
public class ServicosEmMemoria implements UsuarioService, AreaService, VagaService,
        CandidaturaService, RelatorioService {

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Area> areas = new ArrayList<>();
    private final List<Vaga> vagas = new ArrayList<>();
    private final List<Candidatura> candidaturas = new ArrayList<>();
    private long proximoId = 1;

    public ServicosEmMemoria() { popularDemonstracao(); }

    private long novoId() { return proximoId++; }

    private void popularDemonstracao() {
        usuarios.add(new Administrador(novoId(), "Admin", "admin@portal.com"));
        Empresa acme = new Empresa(novoId(), "Maria RH", "rh@acme.com", "ACME Software");
        Empresa beta = new Empresa(novoId(), "Carlos RH", "rh@beta.com", "Beta Design");
        usuarios.add(acme);
        usuarios.add(beta);
        usuarios.add(new Estudante(novoId(), "João Silva", "joao@aluno.edu", "Sistemas de Informação"));
        usuarios.add(new Estudante(novoId(), "Ana Souza", "ana@aluno.edu", "Design"));
        Area tec = new Area(novoId(), "Tecnologia");
        areas.add(tec);
        areas.add(new Area(novoId(), "Administração"));
        Area design = new Area(novoId(), "Design");
        areas.add(design);
        vagas.add(new Vaga(novoId(), acme, "Estágio em Desenvolvimento Java", "Apoio ao back-end",
                tec, "Itápolis - SP", Modalidade.HIBRIDA, 30));
        vagas.add(new Vaga(novoId(), beta, "Estágio em UX", "Protótipos e pesquisa",
                design, "Remoto", Modalidade.REMOTA, 20));
    }

    // ---------- helpers de regra ----------
    private void exigirPerfil(Usuario ator, Perfil perfil, String acao) {
        if (ator == null || ator.getPerfil() != perfil)
            throw new AcessoNegadoException("Somente " + perfil.name().toLowerCase() + " pode " + acao + ".");
    }

    private Vaga buscarVaga(Long id) {
        return vagas.stream().filter(v -> v.getId().equals(id)).findFirst()
                .orElseThrow(() -> new RegraNegocioException("Vaga " + id + " não encontrada."));
    }

    private Area buscarArea(Long id) {
        return areas.stream().filter(a -> a.getId().equals(id)).findFirst()
                .orElseThrow(() -> new RegraNegocioException("Área " + id + " não encontrada."));
    }

    private Vaga vagaDaEmpresa(Usuario ator, Long vagaId) {
        exigirPerfil(ator, Perfil.EMPRESA, "gerenciar vagas");
        Vaga v = buscarVaga(vagaId);
        if (!v.getEmpresa().getId().equals(ator.getId()))
            throw new AcessoNegadoException("Esta vaga pertence a outra empresa.");
        return v;
    }

    private void garantirEmailUnico(String email) {
        String normalizado = email.trim().toLowerCase();
        if (usuarios.stream().anyMatch(u -> u.getEmail().equals(normalizado)))
            throw new RegraNegocioException("E-mail já cadastrado: " + normalizado);
    }

    // ---------- UsuarioService ----------
    @Override public Estudante cadastrarEstudante(String nome, String email, String curso) {
        Estudante e = new Estudante(novoId(), nome, email, curso);
        garantirEmailUnico(e.getEmail());
        usuarios.add(e);
        return e;
    }

    @Override public Empresa cadastrarEmpresa(String nome, String email, String nomeEmpresa) {
        Empresa e = new Empresa(novoId(), nome, email, nomeEmpresa);
        garantirEmailUnico(e.getEmail());
        usuarios.add(e);
        return e;
    }

    @Override public List<Usuario> listarUsuarios() { return List.copyOf(usuarios); }

    // ---------- AreaService ----------
    @Override public Area cadastrar(Usuario ator, String nome) {
        exigirPerfil(ator, Perfil.ADMINISTRADOR, "cadastrar áreas");
        if (areas.stream().anyMatch(a -> a.getNome().equalsIgnoreCase(nome.trim())))
            throw new RegraNegocioException("Já existe uma área com esse nome.");
        Area a = new Area(novoId(), nome.trim());
        areas.add(a);
        return a;
    }

    @Override public Area editar(Usuario ator, Long areaId, String novoNome) {
        exigirPerfil(ator, Perfil.ADMINISTRADOR, "editar áreas");
        Area a = buscarArea(areaId);
        if (novoNome == null || novoNome.isBlank()) throw new IllegalArgumentException("Nome da área obrigatório");
        if (areas.stream().anyMatch(o -> o != a && o.getNome().equalsIgnoreCase(novoNome.trim())))
            throw new RegraNegocioException("Já existe uma área com esse nome.");
        a.setNome(novoNome.trim());
        return a;
    }

    @Override public void excluir(Usuario ator, Long areaId) {
        exigirPerfil(ator, Perfil.ADMINISTRADOR, "excluir áreas");
        Area a = buscarArea(areaId);
        if (vagas.stream().anyMatch(v -> v.getArea().getId().equals(areaId)))
            throw new RegraNegocioException("A área \"" + a.getNome() + "\" está em uso por uma vaga e não pode ser excluída.");
        areas.remove(a);
    }

    @Override public List<Area> listar() { return List.copyOf(areas); }

    // ---------- VagaService ----------
    @Override public Vaga publicar(Usuario ator, String titulo, String descricao, Long areaId,
                                   String localizacao, Modalidade modalidade, int carga) {
        exigirPerfil(ator, Perfil.EMPRESA, "publicar vagas");
        Vaga v = new Vaga(novoId(), (Empresa) ator, titulo, descricao, buscarArea(areaId),
                localizacao, modalidade, carga);
        vagas.add(v);
        return v;
    }

    @Override public Vaga editar(Usuario ator, Long vagaId, String titulo, String descricao, Long areaId,
                                 String localizacao, Modalidade modalidade, int carga) {
        Vaga v = vagaDaEmpresa(ator, vagaId);
        if (!v.estaAberta()) throw new RegraNegocioException("Vaga encerrada não pode ser editada.");
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("Título obrigatório");
        v.setTitulo(titulo);
        v.setDescricao(descricao);
        v.setArea(buscarArea(areaId));
        v.setLocalizacao(localizacao);
        v.setModalidade(modalidade);
        v.setCargaHorariaSemanal(carga);
        return v;
    }

    @Override public void encerrar(Usuario ator, Long vagaId) {
        Vaga v = vagaDaEmpresa(ator, vagaId);
        if (!v.estaAberta()) throw new RegraNegocioException("A vaga já está encerrada.");
        v.encerrar();
    }

    @Override public List<Vaga> listarAbertas(Long areaId, Modalidade modalidade) {
        // Predicate (interface funcional) combinado com and(): só aplica os filtros informados.
        Predicate<Vaga> filtro = Vaga::estaAberta;
        if (areaId != null) filtro = filtro.and(v -> v.getArea().getId().equals(areaId));
        if (modalidade != null) filtro = filtro.and(v -> v.getModalidade() == modalidade);
        return vagas.stream().filter(filtro).collect(Collectors.toList());
    }

    @Override public List<Vaga> listarDaEmpresa(Usuario ator) {
        exigirPerfil(ator, Perfil.EMPRESA, "listar suas vagas");
        return vagas.stream().filter(v -> v.getEmpresa().getId().equals(ator.getId())).collect(Collectors.toList());
    }

    // ---------- CandidaturaService ----------
    @Override public Candidatura candidatar(Usuario ator, Long vagaId) {
        exigirPerfil(ator, Perfil.ESTUDANTE, "se candidatar");
        Vaga v = buscarVaga(vagaId);
        if (!v.estaAberta()) throw new RegraNegocioException("A vaga está encerrada.");
        boolean duplicada = candidaturas.stream().anyMatch(c ->
                c.getEstudante().getId().equals(ator.getId()) && c.getVaga().getId().equals(vagaId));
        if (duplicada) throw new RegraNegocioException("Você já se candidatou a esta vaga.");
        Candidatura c = new Candidatura(novoId(), (Estudante) ator, v, LocalDate.now());
        candidaturas.add(c);
        return c;
    }

    @Override public List<Candidatura> listarMinhas(Usuario ator) {
        exigirPerfil(ator, Perfil.ESTUDANTE, "consultar candidaturas");
        return candidaturas.stream().filter(c -> c.getEstudante().getId().equals(ator.getId()))
                .collect(Collectors.toList());
    }

    @Override public List<Candidatura> listarCandidatos(Usuario ator, Long vagaId) {
        Vaga v = vagaDaEmpresa(ator, vagaId);
        return candidaturas.stream().filter(c -> c.getVaga().getId().equals(v.getId()))
                .collect(Collectors.toList());
    }

    // ---------- RelatorioService ----------
    @Override public Map<String, Long> contagens(Usuario ator) {
        exigirPerfil(ator, Perfil.ADMINISTRADOR, "ver as contagens");
        Map<String, Long> m = new LinkedHashMap<>();
        m.put("Estudantes", usuarios.stream().filter(u -> u.getPerfil() == Perfil.ESTUDANTE).count());
        m.put("Empresas", usuarios.stream().filter(u -> u.getPerfil() == Perfil.EMPRESA).count());
        m.put("Vagas abertas", vagas.stream().filter(Vaga::estaAberta).count());
        m.put("Vagas encerradas", vagas.stream().filter(v -> !v.estaAberta()).count());
        m.put("Candidaturas", (long) candidaturas.size());
        return m;
    }
}

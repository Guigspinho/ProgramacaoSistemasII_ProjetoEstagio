package app;

import dao.AreaDAO;
import dao.CandidaturaDAO;
import dao.EstatisticaDAO;
import dao.UsuarioDAO;
import dao.VagaDAO;
import dao.jdbc.ConexaoSupabase;
import dao.jdbc.InicializadorBanco;
import dao.jdbc.JdbcAreaDAO;
import dao.jdbc.JdbcCandidaturaDAO;
import dao.jdbc.JdbcEstatisticaDAO;
import dao.jdbc.JdbcUsuarioDAO;
import dao.jdbc.JdbcVagaDAO;
import service.AreaService;
import service.CandidaturaService;
import service.RelatorioService;
import service.UsuarioService;
import service.VagaService;

/** Monta a aplicação com persistência JDBC. */
public final class ConfiguracaoAplicacao {
    private ConfiguracaoAplicacao() {
}

    public static MenuPrincipal criarMenu(Entrada entrada) {
        ConexaoSupabase conexao = new ConexaoSupabase();
        new InicializadorBanco(conexao).inicializar();

        UsuarioDAO usuarioDAO = new JdbcUsuarioDAO(conexao);
        AreaDAO areaDAO = new JdbcAreaDAO(conexao);
        VagaDAO vagaDAO = new JdbcVagaDAO(conexao);
        CandidaturaDAO candidaturaDAO = new JdbcCandidaturaDAO(conexao);
        EstatisticaDAO estatisticaDAO = new JdbcEstatisticaDAO(conexao);

        UsuarioService usuarioService = new UsuarioService(usuarioDAO);
        AreaService areaService = new AreaService(areaDAO);
        VagaService vagaService = new VagaService(vagaDAO, areaDAO);
        CandidaturaService candidaturaService = new CandidaturaService(candidaturaDAO, vagaDAO);
        RelatorioService relatorioService = new RelatorioService(estatisticaDAO);

        return new MenuPrincipal(entrada, usuarioService, areaService, vagaService,
                candidaturaService, relatorioService);
    }
}
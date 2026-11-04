package com.projeto.estagio.mackenzie.app;

import com.projeto.estagio.mackenzie.dao.AreaDAO;
import com.projeto.estagio.mackenzie.dao.CandidaturaDAO;
import com.projeto.estagio.mackenzie.dao.EstatisticaDAO;
import com.projeto.estagio.mackenzie.dao.UsuarioDAO;
import com.projeto.estagio.mackenzie.dao.VagaDAO;
import com.projeto.estagio.mackenzie.dao.jdbc.ConexaoSupabase;
import com.projeto.estagio.mackenzie.dao.jdbc.InicializadorBanco;
import com.projeto.estagio.mackenzie.dao.jdbc.JdbcAreaDAO;
import com.projeto.estagio.mackenzie.dao.jdbc.JdbcCandidaturaDAO;
import com.projeto.estagio.mackenzie.dao.jdbc.JdbcEstatisticaDAO;
import com.projeto.estagio.mackenzie.dao.jdbc.JdbcUsuarioDAO;
import com.projeto.estagio.mackenzie.dao.jdbc.JdbcVagaDAO;
import com.projeto.estagio.mackenzie.service.AreaService;
import com.projeto.estagio.mackenzie.service.CandidaturaService;
import com.projeto.estagio.mackenzie.service.RelatorioService;
import com.projeto.estagio.mackenzie.service.UsuarioService;
import com.projeto.estagio.mackenzie.service.VagaService;
import java.util.Scanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Monta a aplicação com persistência JDBC.
 *
 * É a mesma fiação que antes era feita à mão em {@code criarMenu}, agora registrada como beans
 * do Spring. As classes de domínio, DAOs e serviços continuam sem nenhuma anotação do
 * framework: a injeção acontece pelos construtores que elas já tinham.
 */
@Configuration
public class ConfiguracaoAplicacao {

    @Bean
    public Entrada entrada() {
        return new Entrada(new Scanner(System.in));
    }

    @Bean
    public ConexaoSupabase conexaoSupabase() {
        return new ConexaoSupabase();
    }

    @Bean
    public InicializadorBanco inicializadorBanco(ConexaoSupabase conexao) {
        return new InicializadorBanco(conexao);
    }

    @Bean
    public UsuarioDAO usuarioDAO(ConexaoSupabase conexao) {
        return new JdbcUsuarioDAO(conexao);
    }

    @Bean
    public AreaDAO areaDAO(ConexaoSupabase conexao) {
        return new JdbcAreaDAO(conexao);
    }

    @Bean
    public VagaDAO vagaDAO(ConexaoSupabase conexao) {
        return new JdbcVagaDAO(conexao);
    }

    @Bean
    public CandidaturaDAO candidaturaDAO(ConexaoSupabase conexao) {
        return new JdbcCandidaturaDAO(conexao);
    }

    @Bean
    public EstatisticaDAO estatisticaDAO(ConexaoSupabase conexao) {
        return new JdbcEstatisticaDAO(conexao);
    }

    @Bean
    public UsuarioService usuarioService(UsuarioDAO usuarioDAO) {
        return new UsuarioService(usuarioDAO);
    }

    @Bean
    public AreaService areaService(AreaDAO areaDAO) {
        return new AreaService(areaDAO);
    }

    @Bean
    public VagaService vagaService(VagaDAO vagaDAO, AreaDAO areaDAO) {
        return new VagaService(vagaDAO, areaDAO);
    }

    @Bean
    public CandidaturaService candidaturaService(CandidaturaDAO candidaturaDAO, VagaDAO vagaDAO) {
        return new CandidaturaService(candidaturaDAO, vagaDAO);
    }

    @Bean
    public RelatorioService relatorioService(EstatisticaDAO estatisticaDAO) {
        return new RelatorioService(estatisticaDAO);
    }

    @Bean
    public MenuPrincipal menuPrincipal(Entrada entrada, UsuarioService usuarioService,
                                       AreaService areaService, VagaService vagaService,
                                       CandidaturaService candidaturaService,
                                       RelatorioService relatorioService) {
        return new MenuPrincipal(entrada, usuarioService, areaService, vagaService,
                candidaturaService, relatorioService);
    }
}

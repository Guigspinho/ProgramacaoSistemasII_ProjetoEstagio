package com.projeto.estagio.mackenzie.app;

import com.projeto.estagio.mackenzie.dao.AreaDAO;
import com.projeto.estagio.mackenzie.dao.CandidaturaDAO;
import com.projeto.estagio.mackenzie.dao.EstatisticaDAO;
import com.projeto.estagio.mackenzie.dao.UsuarioDAO;
import com.projeto.estagio.mackenzie.dao.VagaDAO;
import com.projeto.estagio.mackenzie.model.Usuario;
import com.projeto.estagio.mackenzie.service.AreaService;
import com.projeto.estagio.mackenzie.service.CandidaturaService;
import com.projeto.estagio.mackenzie.service.RelatorioService;
import com.projeto.estagio.mackenzie.service.UsuarioService;
import com.projeto.estagio.mackenzie.service.VagaService;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Monta os serviços e componentes da aplicação.
 *
 * É a mesma fiação que antes era feita à mão em {@code criarMenu}, agora registrada como beans
 * do Spring. Os DAOs são implementados com JPA e descobertos por component scan.
 */
@Configuration
public class ConfiguracaoAplicacao {

    @Bean
    public Entrada entrada() {
        return new Entrada(new Scanner(System.in));
    }

    @Bean
    public UsuarioService usuarioService(UsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder) {
        return new UsuarioService(usuarioDAO, passwordEncoder);
    }

    @Bean
    @Order(0)
    public CommandLineRunner inicializarSenhasLegadas(
            UsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder,
            @Value("${APP_BOOTSTRAP_PASSWORD:}") String senhaInicial) {
        return args -> {
            if (senhaInicial.isBlank()) return;
            if (senhaInicial.length() < 8) {
                throw new IllegalStateException("APP_BOOTSTRAP_PASSWORD deve ter pelo menos 8 caracteres.");
            }
            for (Usuario usuario : usuarioDAO.listarTodos()) {
                if (usuario.getSenhaHash() == null || usuario.getSenhaHash().isBlank()) {
                    usuarioDAO.atualizarSenhaHash(usuario.getId(), passwordEncoder.encode(senhaInicial));
                }
            }
        };
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

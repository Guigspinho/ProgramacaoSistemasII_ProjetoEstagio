package com.projeto.estagio.mackenzie.app;

import com.projeto.estagio.mackenzie.dao.AreaDAO;
import com.projeto.estagio.mackenzie.dao.CandidaturaDAO;
import com.projeto.estagio.mackenzie.dao.EstatisticaDAO;
import com.projeto.estagio.mackenzie.dao.UsuarioDAO;
import com.projeto.estagio.mackenzie.dao.VagaDAO;
import com.projeto.estagio.mackenzie.service.AreaService;
import com.projeto.estagio.mackenzie.service.CandidaturaService;
import com.projeto.estagio.mackenzie.service.RelatorioService;
import com.projeto.estagio.mackenzie.service.UsuarioService;
import com.projeto.estagio.mackenzie.service.VagaService;
import java.util.Scanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Monta os serviços e componentes da aplicação.
 *
 * É a mesma fiação que antes era feita à mão em {@code criarMenu}, agora registrada como beans
 * do Spring. Os DAOs são implementados com JPA e descobertos por component scan.
 */
@Configuration
public class ConfiguracaoAplicacao implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("https://www.httprequest.net")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "X-Usuario-Id");
    }

    @Bean
    public Entrada entrada() {
        return new Entrada(new Scanner(System.in));
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

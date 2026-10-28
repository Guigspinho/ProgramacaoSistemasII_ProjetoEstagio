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
import exception.PersistenciaException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import service.AreaService;
import service.CandidaturaService;
import service.RelatorioService;
import service.UsuarioService;
import service.VagaService;

/**
 * Ponto de entrada. Monta as camadas de baixo para cima:
 * conexão -> DAOs (JDBC) -> serviços -> menus.
 */
public class Main {
    public static void main(String[] args) {
        Entrada entrada = new Entrada(new Scanner(System.in));

        MenuPrincipal menu;
        try {
            ConexaoSupabase conexao = new ConexaoSupabase();
            new InicializadorBanco(conexao).inicializar();

            // Persistência: cada DAO é uma interface com implementação JDBC.
            UsuarioDAO usuarioDAO = new JdbcUsuarioDAO(conexao);
            AreaDAO areaDAO = new JdbcAreaDAO(conexao);
            VagaDAO vagaDAO = new JdbcVagaDAO(conexao);
            CandidaturaDAO candidaturaDAO = new JdbcCandidaturaDAO(conexao);
            EstatisticaDAO estatisticaDAO = new JdbcEstatisticaDAO(conexao);

            // Regras de negócio: cada serviço recebe os DAOs de que precisa.
            UsuarioService usuarioService = new UsuarioService(usuarioDAO);
            AreaService areaService = new AreaService(areaDAO);
            VagaService vagaService = new VagaService(vagaDAO, areaDAO);
            CandidaturaService candidaturaService = new CandidaturaService(candidaturaDAO, vagaDAO);
            RelatorioService relatorioService = new RelatorioService(estatisticaDAO);

            // Apresentação: o menu só conhece os serviços.
            menu = new MenuPrincipal(entrada, usuarioService, areaService, vagaService,
                    candidaturaService, relatorioService);
        } catch (PersistenciaException | IllegalStateException e) {
            System.out.println("[Erro de dados] " + e.getMessage());
            System.out.println("A aplicação será encerrada sem alterar os dados existentes.");
            return;
        }

        try {
            menu.abrir();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada. Até logo!");
        }
    }
}

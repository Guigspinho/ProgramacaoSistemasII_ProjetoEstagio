package com.projeto.estagio.mackenzie.app;

import java.util.List;
import com.projeto.estagio.mackenzie.model.*;
import com.projeto.estagio.mackenzie.service.*;

/**
 * Menu inicial. Permite cadastrar estudantes e empresas, consultar vagas (público)
 * e entrar com e-mail e senha.
 */
public class MenuPrincipal {
    private final Entrada entrada;
    private final UsuarioService usuarioService;
    private final TelaVagas telaVagas;
    private final MenuEstudante menuEstudante;
    private final MenuEmpresa menuEmpresa;
    private final MenuAdministrador menuAdministrador;

    public MenuPrincipal(Entrada entrada, UsuarioService usuarioService, AreaService areaService,
                         VagaService vagaService, CandidaturaService candidaturaService,
                         RelatorioService relatorioService) {
        this.entrada = entrada;
        this.usuarioService = usuarioService;
        this.telaVagas = new TelaVagas(entrada, vagaService, areaService);
        this.menuEstudante = new MenuEstudante(entrada, candidaturaService, telaVagas);
        this.menuEmpresa = new MenuEmpresa(entrada, vagaService, candidaturaService, telaVagas);
        this.menuAdministrador = new MenuAdministrador(entrada, areaService, relatorioService, telaVagas);
    }

    public void abrir() {
        Menu menu = new Menu("PORTAL DE ESTÁGIOS", "Sair", entrada)
            .adicionar("Entrar")
                .adicionar("Cadastrar estudante")
                .adicionar("Cadastrar empresa")
                .adicionar("Consultar vagas abertas");
        while (true) {
            int opcao = menu.exibir();
            if (opcao == 0) break;
            try {
                switch (opcao) {
                    case 1: entrar(); break;
                    case 2: cadastrarEstudante(); break;
                    case 3: cadastrarEmpresa(); break;
                    case 4: telaVagas.consultarAbertas(); break;
                    default: break;
                }
            } catch (RuntimeException e) {
                Menu.tratarErro(e);
            }
        }
        System.out.println("\nAté logo!");
    }

    private void entrar() {
        String email = entrada.lerTexto("E-mail");
        String senha = entrada.lerTexto("Senha");
        Usuario ator = usuarioService.autenticar(email, senha);
        switch (ator.getPerfil()) {
            case ESTUDANTE: menuEstudante.abrir(ator); break;
            case EMPRESA: menuEmpresa.abrir(ator); break;
            case ADMINISTRADOR: menuAdministrador.abrir(ator); break;
        }
    }

    private void cadastrarEstudante() {
        String nome = entrada.lerTexto("Nome");
        String email = entrada.lerTexto("E-mail");
        String curso = entrada.lerTexto("Curso");
        String senha = entrada.lerTexto("Senha (mínimo 8 caracteres)");
        Estudante e = usuarioService.cadastrarEstudante(nome, email, curso, senha);
        System.out.println("Estudante cadastrado: " + e);
    }

    private void cadastrarEmpresa() {
        String nome = entrada.lerTexto("Nome do responsável");
        String email = entrada.lerTexto("E-mail");
        String nomeEmpresa = entrada.lerTexto("Nome da empresa");
        String senha = entrada.lerTexto("Senha (mínimo 8 caracteres)");
        Empresa e = usuarioService.cadastrarEmpresa(nome, email, nomeEmpresa, senha);
        System.out.println("Empresa cadastrada: " + e);
    }
}

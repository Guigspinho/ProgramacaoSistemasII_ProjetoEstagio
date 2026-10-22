package app;

import java.util.List;
import model.*;
import service.*;

/**
 * Menu inicial. Permite cadastrar estudantes e empresas, consultar vagas (público)
 * e "entrar" como um usuário de demonstração (identidade simulada da Entrega 1:
 * NÃO é autenticação; será substituída pelo login com Spring Security na Entrega 2).
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
        new Menu("PORTAL DE ESTÁGIOS", "Sair", entrada)
                .adicionar("Entrar como usuário de demonstração", this::entrar)
                .adicionar("Cadastrar estudante", this::cadastrarEstudante)
                .adicionar("Cadastrar empresa", this::cadastrarEmpresa)
                .adicionar("Consultar vagas abertas", telaVagas::consultarAbertas)
                .exibir();
        System.out.println("\nAté logo!");
    }

    private void entrar() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        System.out.println("Usuários de demonstração (identidade simulada):");
        for (int i = 0; i < usuarios.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + usuarios.get(i));
        }
        int n = entrada.lerOpcao("Escolha o usuário (0 = cancelar)", 0, usuarios.size());
        if (n == 0) return;

        Usuario ator = usuarios.get(n - 1);
        switch (ator.getPerfil()) {
            case ESTUDANTE -> menuEstudante.abrir(ator);
            case EMPRESA -> menuEmpresa.abrir(ator);
            case ADMINISTRADOR -> menuAdministrador.abrir(ator);
        }
    }

    private void cadastrarEstudante() {
        String nome = entrada.lerTexto("Nome");
        String email = entrada.lerTexto("E-mail");
        String curso = entrada.lerTexto("Curso");
        Estudante e = usuarioService.cadastrarEstudante(nome, email, curso);
        System.out.println("Estudante cadastrado: " + e);
    }

    private void cadastrarEmpresa() {
        String nome = entrada.lerTexto("Nome do responsável");
        String email = entrada.lerTexto("E-mail");
        String nomeEmpresa = entrada.lerTexto("Nome da empresa");
        Empresa e = usuarioService.cadastrarEmpresa(nome, email, nomeEmpresa);
        System.out.println("Empresa cadastrada: " + e);
    }
}

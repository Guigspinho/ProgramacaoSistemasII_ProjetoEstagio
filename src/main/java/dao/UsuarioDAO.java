package dao;

import java.util.List;
import model.Empresa;
import model.Estudante;
import model.Usuario;

/** Operações de persistência para usuários (estudantes, empresas e administradores). */
public interface UsuarioDAO {

    /** Grava o estudante (tabelas usuarios e estudantes) e devolve-o com o ID gerado. */
    Estudante cadastrarEstudante(Estudante estudante);

    /** Grava a empresa (tabelas usuarios e empresas) e devolve-a com o ID gerado. */
    Empresa cadastrarEmpresa(Empresa empresa);

    /** Devolve o usuário ou null se o e-mail não existir. */
    Usuario buscarPorEmail(String email);

    /** Todos os usuários, já como Estudante, Empresa ou Administrador. */
    List<Usuario> listarTodos();
}

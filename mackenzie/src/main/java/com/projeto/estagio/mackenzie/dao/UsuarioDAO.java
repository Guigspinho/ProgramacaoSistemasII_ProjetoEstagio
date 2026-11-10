package com.projeto.estagio.mackenzie.dao;

import java.util.List;
import com.projeto.estagio.mackenzie.model.Empresa;
import com.projeto.estagio.mackenzie.model.Estudante;
import com.projeto.estagio.mackenzie.model.Usuario;

/** Operações de persistência para usuários (estudantes, empresas e administradores). */
public interface UsuarioDAO {

    /** Grava o estudante (tabelas usuarios e estudantes) e devolve-o com o ID gerado. */
    Estudante cadastrarEstudante(Estudante estudante);

    /** Grava a empresa (tabelas usuarios e empresas) e devolve-a com o ID gerado. */
    Empresa cadastrarEmpresa(Empresa empresa);

    /** Devolve o usuário ou null se o e-mail não existir. */
    Usuario buscarPorEmail(String email);

    /** Devolve o usuário ou null se o ID não existir. */
    Usuario buscarPorId(Long id);

    /** Todos os usuários, já como Estudante, Empresa ou Administrador. */
    List<Usuario> listarTodos();
}

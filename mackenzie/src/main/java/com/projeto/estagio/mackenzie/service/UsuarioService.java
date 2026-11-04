package com.projeto.estagio.mackenzie.service;

import com.projeto.estagio.mackenzie.dao.UsuarioDAO;
import com.projeto.estagio.mackenzie.exception.RegraNegocioException;
import java.util.List;
import com.projeto.estagio.mackenzie.model.Empresa;
import com.projeto.estagio.mackenzie.model.Estudante;
import com.projeto.estagio.mackenzie.model.Usuario;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Estudante cadastrarEstudante(String nome, String email, String curso) {
        String nomeValido = Validacao.textoObrigatorio(nome, "Nome");
        String emailValido = Validacao.normalizarEmail(email);
        String cursoValido = Validacao.textoObrigatorio(curso, "Curso");
        exigirEmailLivre(emailValido);

        return usuarioDAO.cadastrarEstudante(new Estudante(null, nomeValido, emailValido, cursoValido));
    }

    public Empresa cadastrarEmpresa(String nome, String email, String nomeEmpresa) {
        String nomeValido = Validacao.textoObrigatorio(nome, "Nome");
        String emailValido = Validacao.normalizarEmail(email);
        String empresaValida = Validacao.textoObrigatorio(nomeEmpresa, "Nome da empresa");
        exigirEmailLivre(emailValido);

        return usuarioDAO.cadastrarEmpresa(new Empresa(null, nomeValido, emailValido, empresaValida));
    }

    /** Usuários cadastrados, usados na seleção da identidade simulada. */
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }

    private void exigirEmailLivre(String email) {
        if (usuarioDAO.buscarPorEmail(email) != null) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }
    }
}

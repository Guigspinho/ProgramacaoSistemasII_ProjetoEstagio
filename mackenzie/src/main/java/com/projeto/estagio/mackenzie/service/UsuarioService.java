package com.projeto.estagio.mackenzie.service;

import com.projeto.estagio.mackenzie.dao.UsuarioDAO;
import com.projeto.estagio.mackenzie.exception.RecursoNaoEncontradoException;
import com.projeto.estagio.mackenzie.exception.RegraNegocioException;
import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.projeto.estagio.mackenzie.model.Empresa;
import com.projeto.estagio.mackenzie.model.Estudante;
import com.projeto.estagio.mackenzie.model.Usuario;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public Estudante cadastrarEstudante(String nome, String email, String curso, String senha) {
        String nomeValido = Validacao.textoObrigatorio(nome, "Nome");
        String emailValido = Validacao.normalizarEmail(email);
        String cursoValido = Validacao.textoObrigatorio(curso, "Curso");
        String senhaValida = validarSenha(senha);
        exigirEmailLivre(emailValido);

        return usuarioDAO.cadastrarEstudante(new Estudante(null, nomeValido, emailValido,
                cursoValido, passwordEncoder.encode(senhaValida)));
    }

    public Empresa cadastrarEmpresa(String nome, String email, String nomeEmpresa, String senha) {
        String nomeValido = Validacao.textoObrigatorio(nome, "Nome");
        String emailValido = Validacao.normalizarEmail(email);
        String empresaValida = Validacao.textoObrigatorio(nomeEmpresa, "Nome da empresa");
        String senhaValida = validarSenha(senha);
        exigirEmailLivre(emailValido);

        return usuarioDAO.cadastrarEmpresa(new Empresa(null, nomeValido, emailValido,
                empresaValida, passwordEncoder.encode(senhaValida)));
    }

    public Usuario buscarPorId(Long id) {
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Usuário " + id + " não encontrado.");
        }
        return usuario;
    }

    public Usuario buscarPorEmail(String email) {
        String emailValido = Validacao.normalizarEmail(email);
        Usuario usuario = usuarioDAO.buscarPorEmail(emailValido);
        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado.");
        }
        return usuario;
    }

    public Usuario autenticar(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario.getSenhaHash() == null || !passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw new RegraNegocioException("E-mail ou senha inválidos.");
        }
        return usuario;
    }

    private String validarSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("A senha excede o limite aceito pelo BCrypt.");
        }
        return senha;
    }

    private void exigirEmailLivre(String email) {
        if (usuarioDAO.buscarPorEmail(email) != null) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }
    }
}

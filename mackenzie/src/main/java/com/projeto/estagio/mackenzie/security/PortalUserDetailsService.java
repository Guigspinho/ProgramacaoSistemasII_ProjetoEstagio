package com.projeto.estagio.mackenzie.security;

import com.projeto.estagio.mackenzie.exception.RecursoNaoEncontradoException;
import com.projeto.estagio.mackenzie.model.Usuario;
import com.projeto.estagio.mackenzie.service.UsuarioService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PortalUserDetailsService implements UserDetailsService {
    private final UsuarioService usuarioService;

    public PortalUserDetailsService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario;
        try {
            usuario = usuarioService.buscarPorEmail(email);
        } catch (RecursoNaoEncontradoException exception) {
            throw new UsernameNotFoundException("Credenciais inválidas.");
        }
        if (usuario.getSenhaHash() == null || usuario.getSenhaHash().isBlank()) {
            throw new UsernameNotFoundException("Credenciais inválidas.");
        }
        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .roles(usuario.getPerfil().name())
                .build();
    }
}
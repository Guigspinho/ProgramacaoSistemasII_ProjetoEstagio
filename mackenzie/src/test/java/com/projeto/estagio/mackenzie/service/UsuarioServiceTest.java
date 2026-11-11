package com.projeto.estagio.mackenzie.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.projeto.estagio.mackenzie.dao.UsuarioDAO;
import com.projeto.estagio.mackenzie.model.Estudante;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioServiceTest {

    @Test
    void cadastroArmazenaHashBCryptEmVezDaSenhaOriginal() {
        UsuarioDAO usuarioDAO = mock(UsuarioDAO.class);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UsuarioService service = new UsuarioService(usuarioDAO, passwordEncoder);
        String senha = "senha-segura-123";

        when(usuarioDAO.buscarPorEmail("ana@exemplo.com")).thenReturn(null);
        when(usuarioDAO.cadastrarEstudante(any(Estudante.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Estudante estudante = service.cadastrarEstudante(
                "Ana", "ana@exemplo.com", "Sistemas", senha);

        assertNotEquals(senha, estudante.getSenhaHash());
        assertTrue(passwordEncoder.matches(senha, estudante.getSenhaHash()));
    }

    @Test
    void cadastroRejeitaSenhaMaiorQueLimiteDoBCrypt() {
        UsuarioService service = new UsuarioService(mock(UsuarioDAO.class), new BCryptPasswordEncoder());

        assertThrows(IllegalArgumentException.class,
                () -> service.cadastrarEstudante("Ana", "ana@exemplo.com", "Sistemas", "a".repeat(73)));
    }
}

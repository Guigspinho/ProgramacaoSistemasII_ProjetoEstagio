package service;

import exception.AcessoNegadoException;
import model.Perfil;
import model.Usuario;

/** Verificações simples usadas por vários serviços. */
public class Validacao {

    private Validacao() { }

    /** Exige texto não vazio e devolve o texto sem espaços nas pontas. */
    public static String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " obrigatório.");
        }
        return valor.trim();
    }

    /** Exige e-mail no formato texto@texto.texto (sem espaços) e devolve em minúsculas. */
    public static String normalizarEmail(String email) {
        String normalizado = textoObrigatorio(email, "E-mail").toLowerCase();
        if (!normalizado.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        return normalizado;
    }

    /** Garante que o usuário selecionado tem o perfil necessário para a ação. */
    public static void exigirPerfil(Usuario ator, Perfil perfil, String acao) {
        if (ator == null || ator.getPerfil() != perfil) {
            throw new AcessoNegadoException("Somente " + perfil.name().toLowerCase() + " pode " + acao + ".");
        }
    }
}

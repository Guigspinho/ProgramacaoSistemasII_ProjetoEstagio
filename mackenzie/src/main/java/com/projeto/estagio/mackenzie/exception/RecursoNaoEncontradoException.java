package com.projeto.estagio.mackenzie.exception;

/** Recurso solicitado que não existe. */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

package com.projeto.estagio.mackenzie.exception;

/** Falha ao acessar o banco de dados (encapsula SQLException). */
public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String mensagem, Throwable causa) { super(mensagem, causa); }
}
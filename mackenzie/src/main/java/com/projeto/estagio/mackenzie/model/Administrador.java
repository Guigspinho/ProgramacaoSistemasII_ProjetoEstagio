package com.projeto.estagio.mackenzie.model;

public class Administrador extends Usuario {
    protected Administrador() {
        super(null, null, null, Perfil.ADMINISTRADOR, null);
    }

    public Administrador(Long id, String nome, String email) {
        this(id, nome, email, null);
    }

    public Administrador(Long id, String nome, String email, String senhaHash) {
        super(id, nome, email, Perfil.ADMINISTRADOR, senhaHash);
    }
}

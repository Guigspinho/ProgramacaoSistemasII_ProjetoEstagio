package com.projeto.estagio.mackenzie.model;

public class Administrador extends Usuario {
    protected Administrador() {
        super(null, null, null, Perfil.ADMINISTRADOR);
    }

    public Administrador(Long id, String nome, String email) {
        super(id, nome, email, Perfil.ADMINISTRADOR);
    }
}

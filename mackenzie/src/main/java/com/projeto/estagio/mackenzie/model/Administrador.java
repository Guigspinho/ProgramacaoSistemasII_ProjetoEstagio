package com.projeto.estagio.mackenzie.model;

public class Administrador extends Usuario {
    public Administrador(Long id, String nome, String email) {
        super(id, nome, email, Perfil.ADMINISTRADOR);
    }
}

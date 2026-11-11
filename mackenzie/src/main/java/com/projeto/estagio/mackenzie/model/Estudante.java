package com.projeto.estagio.mackenzie.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudantes")
@PrimaryKeyJoinColumn(name = "usuario_id", referencedColumnName = "id")
@DiscriminatorValue("ESTUDANTE")
public class Estudante extends Usuario {
    @Column(name = "curso", nullable = false)
    private String curso;

    protected Estudante() {
        super(null, null, null, Perfil.ESTUDANTE, null);
    }

    public Estudante(Long id, String nome, String email, String curso) {
        this(id, nome, email, curso, null);
    }

    public Estudante(Long id, String nome, String email, String curso, String senhaHash) {
        super(id, nome, email, Perfil.ESTUDANTE, senhaHash);
        this.curso = curso;
    }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
}

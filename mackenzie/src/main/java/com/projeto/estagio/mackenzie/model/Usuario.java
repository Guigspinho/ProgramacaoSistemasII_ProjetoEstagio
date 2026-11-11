package com.projeto.estagio.mackenzie.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/** Base comum de todos os perfis (estudante, empresa e administrador). */
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "perfil", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("ADMINISTRADOR")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash")
    private String senhaHash;

    @Transient
    private Perfil perfil;

    protected Usuario() {
        this(null, null, null, Perfil.ADMINISTRADOR, null);
    }

    public Usuario(Long id, String nome, String email, Perfil perfil) {
        this(id, nome, email, perfil, null);
    }

    public Usuario(Long id, String nome, String email, Perfil perfil, String senhaHash) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.senhaHash = senhaHash;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Perfil getPerfil() { return perfil; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    @Override
    public String toString() { return perfil + " #" + id + " - " + nome + " <" + email + ">"; }
}

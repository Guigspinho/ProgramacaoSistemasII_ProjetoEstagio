package com.projeto.estagio.mackenzie.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresas")
@PrimaryKeyJoinColumn(name = "usuario_id", referencedColumnName = "id")
@DiscriminatorValue("EMPRESA")
public class Empresa extends Usuario {
    @Column(name = "nome_empresa", nullable = false)
    private String nomeEmpresa;

    protected Empresa() {
        super(null, null, null, Perfil.EMPRESA);
    }

    public Empresa(Long id, String nome, String email, String nomeEmpresa) {
        super(id, nome, email, Perfil.EMPRESA);
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }
}

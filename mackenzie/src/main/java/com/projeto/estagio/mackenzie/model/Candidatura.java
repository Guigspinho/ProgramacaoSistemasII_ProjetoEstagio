package com.projeto.estagio.mackenzie.model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

/** Associação entre estudante e vaga. O par (estudante, vaga) não pode se repetir. */
@Entity
@Table(name = "candidaturas")
public class Candidatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estudante_id", referencedColumnName = "id", nullable = false)
    private Estudante estudante;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vaga_id", nullable = false)
    private Vaga vaga;

    @Column(name = "data_inscricao", nullable = false)
    private LocalDate dataInscricao;

    protected Candidatura() { }

    public Candidatura(Long id, Estudante estudante, Vaga vaga, LocalDate dataInscricao) {
        this.id = id;
        this.estudante = estudante;
        this.vaga = vaga;
        this.dataInscricao = dataInscricao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Estudante getEstudante() { return estudante; }
    public Vaga getVaga() { return vaga; }
    public LocalDate getDataInscricao() { return dataInscricao; }

    @Override
    public String toString() {
        return "Candidatura #" + id + ": " + estudante.getNome() + " -> " + vaga.getTitulo() + " em " + dataInscricao;
    }
}

package model;

import java.time.LocalDate;

/** Associação entre estudante e vaga. O par (estudante, vaga) não pode se repetir. */
public class Candidatura {
    private Long id;
    private final Estudante estudante;
    private final Vaga vaga;
    private final LocalDate dataInscricao;

    public Candidatura(Long id, Estudante estudante, Vaga vaga, LocalDate dataInscricao) {
        if (estudante == null || vaga == null) throw new IllegalArgumentException("Estudante e vaga obrigatórios");
        this.id = id;
        this.estudante = estudante;
        this.vaga = vaga;
        this.dataInscricao = dataInscricao != null ? dataInscricao : LocalDate.now();
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

package com.projeto.estagio.mackenzie.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vagas")
public class Vaga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "empresa_id", referencedColumnName = "id", nullable = false)
    private Empresa empresa;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Column(name = "localizacao")
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade", nullable = false)
    private Modalidade modalidade;

    @Column(name = "carga_horaria_semanal", nullable = false)
    private int cargaHorariaSemanal;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false)
    private SituacaoVaga situacao = SituacaoVaga.ABERTA;

    protected Vaga() { }

    public Vaga(Long id, Empresa empresa, String titulo, String descricao, Area area,
                String localizacao, Modalidade modalidade, int cargaHorariaSemanal) {
        this.id = id;
        this.empresa = empresa;
        this.titulo = titulo;
        this.descricao = descricao;
        this.area = area;
        this.localizacao = localizacao;
        this.modalidade = modalidade;
        this.cargaHorariaSemanal = cargaHorariaSemanal;
    }

    public boolean estaAberta() { return situacao == SituacaoVaga.ABERTA; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Empresa getEmpresa() { return empresa; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = area; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public Modalidade getModalidade() { return modalidade; }
    public void setModalidade(Modalidade modalidade) { this.modalidade = modalidade; }
    public int getCargaHorariaSemanal() { return cargaHorariaSemanal; }
    public void setCargaHorariaSemanal(int cargaHorariaSemanal) { this.cargaHorariaSemanal = cargaHorariaSemanal; }
    public SituacaoVaga getSituacao() { return situacao; }
    public void setSituacao(SituacaoVaga situacao) { this.situacao = situacao; }

    @Override
    public String toString() {
        return "Vaga #" + id + " [" + situacao + "] " + titulo + " | " + empresa.getNomeEmpresa()
                + " | " + area + " | " + modalidade + " | " + cargaHorariaSemanal + "h/sem | " + localizacao;
    }
}

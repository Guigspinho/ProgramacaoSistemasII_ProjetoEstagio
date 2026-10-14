package model;

public class Vaga {
    private Long id;
    private final Empresa empresa;
    private String titulo;
    private String descricao;
    private Area area;
    private String localizacao;
    private Modalidade modalidade;
    private int cargaHorariaSemanal;
    private SituacaoVaga situacao = SituacaoVaga.ABERTA;

    public Vaga(Long id, Empresa empresa, String titulo, String descricao, Area area,
                String localizacao, Modalidade modalidade, int cargaHorariaSemanal) {
        if (empresa == null || area == null || modalidade == null)
            throw new IllegalArgumentException("Empresa, área e modalidade são obrigatórias");
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("Título obrigatório");
        if (cargaHorariaSemanal <= 0) throw new IllegalArgumentException("Carga horária deve ser positiva");
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
    public void encerrar() { this.situacao = SituacaoVaga.ENCERRADA; }

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
    public void setCargaHorariaSemanal(int c) {
        if (c <= 0) throw new IllegalArgumentException("Carga horária deve ser positiva");
        this.cargaHorariaSemanal = c;
    }
    public SituacaoVaga getSituacao() { return situacao; }

    @Override
    public String toString() {
        return "Vaga #" + id + " [" + situacao + "] " + titulo + " | " + empresa.getNomeEmpresa()
                + " | " + area + " | " + modalidade + " | " + cargaHorariaSemanal + "h/sem | " + localizacao;
    }
}

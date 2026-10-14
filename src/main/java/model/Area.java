package model;

public class Area {
    private Long id;
    private String nome;

    public Area(Long id, String nome) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome da área obrigatório");
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public String toString() { return nome; }
}

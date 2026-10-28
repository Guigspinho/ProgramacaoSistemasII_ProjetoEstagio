package model;

/** Base comum de todos os perfis (estudante, empresa e administrador). */
public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private final Perfil perfil;

    public Usuario(Long id, String nome, String email, Perfil perfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Perfil getPerfil() { return perfil; }

    @Override
    public String toString() { return perfil + " #" + id + " - " + nome + " <" + email + ">"; }
}

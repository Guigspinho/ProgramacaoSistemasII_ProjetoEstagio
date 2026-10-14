package model;

/** Base comum de todos os perfis. O e-mail deve ser único (garantido na camada de serviço/DAO). */
public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private final Perfil perfil;

    public Usuario(Long id, String nome, String email, Perfil perfil) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome obrigatório");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("E-mail inválido");
        if (perfil == null) throw new IllegalArgumentException("Perfil obrigatório");
        this.id = id;
        this.nome = nome;
        this.email = email.trim().toLowerCase();
        this.perfil = perfil;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email.trim().toLowerCase(); }
    public Perfil getPerfil() { return perfil; }

    @Override
    public String toString() { return perfil + " #" + id + " - " + nome + " <" + email + ">"; }
}

package model;

public class Empresa extends Usuario {
    private String nomeEmpresa;

    public Empresa(Long id, String nome, String email, String nomeEmpresa) {
        super(id, nome, email, Perfil.EMPRESA);
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }
}

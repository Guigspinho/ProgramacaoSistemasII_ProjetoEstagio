package model;

public class Estudante extends Usuario {
    private String curso;

    public Estudante(Long id, String nome, String email, String curso) {
        super(id, nome, email, Perfil.ESTUDANTE);
        this.curso = curso;
    }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
}

package service;

import java.util.List;
import model.*;

public interface UsuarioService {
    Estudante cadastrarEstudante(String nome, String email, String curso);
    Empresa cadastrarEmpresa(String nome, String email, String nomeEmpresa);
    /** Usuários cadastrados, usados na seleção da identidade simulada. */
    List<Usuario> listarUsuarios();
}

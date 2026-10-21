package service;

import java.util.List;
import model.*;

public interface AreaService {
    Area cadastrar(Usuario ator, String nome);
    Area editar(Usuario ator, Long areaId, String novoNome);
    void excluir(Usuario ator, Long areaId);
    List<Area> listar();
}

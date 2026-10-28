package service;

import dao.AreaDAO;
import exception.RegraNegocioException;
import java.util.List;
import model.Area;
import model.Perfil;
import model.Usuario;

public class AreaService {
    private final AreaDAO areaDAO;

    public AreaService(AreaDAO areaDAO) {
        this.areaDAO = areaDAO;
    }

    public Area cadastrar(Usuario ator, String nome) {
        Validacao.exigirPerfil(ator, Perfil.ADMINISTRADOR, "cadastrar áreas");
        String nomeValido = Validacao.textoObrigatorio(nome, "Nome da área");
        if (areaDAO.buscarPorNome(nomeValido) != null) {
            throw new RegraNegocioException("Já existe uma área com este nome.");
        }
        return areaDAO.cadastrar(new Area(null, nomeValido));
    }

    public Area editar(Usuario ator, Long areaId, String novoNome) {
        Validacao.exigirPerfil(ator, Perfil.ADMINISTRADOR, "editar áreas");
        Area area = buscarArea(areaId);
        String nomeValido = Validacao.textoObrigatorio(novoNome, "Nome da área");

        // Pode manter o próprio nome (ex.: só mudar maiúsculas), mas não repetir o de outra área.
        Area existente = areaDAO.buscarPorNome(nomeValido);
        if (existente != null && !existente.getId().equals(areaId)) {
            throw new RegraNegocioException("Já existe uma área com este nome.");
        }

        area.setNome(nomeValido);
        areaDAO.atualizar(area);
        return area;
    }

    public void excluir(Usuario ator, Long areaId) {
        Validacao.exigirPerfil(ator, Perfil.ADMINISTRADOR, "excluir áreas");
        buscarArea(areaId);
        if (areaDAO.estaEmUso(areaId)) {
            throw new RegraNegocioException("A área está sendo usada por vagas e não pode ser excluída.");
        }
        areaDAO.excluir(areaId);
    }

    public List<Area> listar() {
        return areaDAO.listarTodas();
    }

    private Area buscarArea(Long areaId) {
        Area area = areaDAO.buscarPorId(areaId);
        if (area == null) {
            throw new RegraNegocioException("Área " + areaId + " não encontrada.");
        }
        return area;
    }
}

package com.projeto.estagio.mackenzie.dao.jpa;

import com.projeto.estagio.mackenzie.dao.AreaDAO;
import com.projeto.estagio.mackenzie.model.Area;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class JpaAreaDAO implements AreaDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Area cadastrar(Area area) {
        entityManager.persist(area);
        return area;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Area> listarTodas() {
        return entityManager.createQuery("SELECT a FROM Area a ORDER BY a.nome", Area.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Area buscarPorId(Long id) {
        return entityManager.find(Area.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public Area buscarPorNome(String nome) {
        List<Area> encontradas = entityManager.createQuery(
                        "SELECT a FROM Area a WHERE LOWER(a.nome) = LOWER(:nome)", Area.class)
                .setParameter("nome", nome)
                .setMaxResults(1)
                .getResultList();
        return encontradas.isEmpty() ? null : encontradas.get(0);
    }

    @Override
    public boolean atualizar(Area area) {
        Area existente = entityManager.find(Area.class, area.getId());
        if (existente == null) {
            return false;
        }
        existente.setNome(area.getNome());
        return true;
    }

    @Override
    public boolean excluir(Long id) {
        Area area = entityManager.find(Area.class, id);
        if (area == null) {
            return false;
        }
        entityManager.remove(area);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaEmUso(Long areaId) {
        Long quantidade = entityManager.createQuery(
                        "SELECT COUNT(v) FROM Vaga v WHERE v.area.id = :areaId", Long.class)
                .setParameter("areaId", areaId)
                .getSingleResult();
        return quantidade > 0;
    }
}
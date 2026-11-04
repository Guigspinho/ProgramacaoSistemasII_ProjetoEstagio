package com.projeto.estagio.mackenzie.dao.jpa;

import com.projeto.estagio.mackenzie.dao.CandidaturaDAO;
import com.projeto.estagio.mackenzie.model.Candidatura;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class JpaCandidaturaDAO implements CandidaturaDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Candidatura cadastrar(Candidatura candidatura) {
        entityManager.persist(candidatura);
        return candidatura;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Candidatura> listarPorEstudante(Long estudanteId) {
        return entityManager.createQuery(
                        "SELECT c FROM Candidatura c WHERE c.estudante.id = :estudanteId ORDER BY c.id",
                        Candidatura.class)
                .setParameter("estudanteId", estudanteId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Candidatura> listarPorVaga(Long vagaId) {
        return entityManager.createQuery(
                        "SELECT c FROM Candidatura c WHERE c.vaga.id = :vagaId ORDER BY c.id",
                        Candidatura.class)
                .setParameter("vagaId", vagaId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEstudanteEVaga(Long estudanteId, Long vagaId) {
        Long quantidade = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Candidatura c "
                                + "WHERE c.estudante.id = :estudanteId AND c.vaga.id = :vagaId", Long.class)
                .setParameter("estudanteId", estudanteId)
                .setParameter("vagaId", vagaId)
                .getSingleResult();
        return quantidade > 0;
    }
}
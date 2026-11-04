package com.projeto.estagio.mackenzie.dao.jpa;

import com.projeto.estagio.mackenzie.dao.VagaDAO;
import com.projeto.estagio.mackenzie.model.SituacaoVaga;
import com.projeto.estagio.mackenzie.model.Vaga;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class JpaVagaDAO implements VagaDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Vaga publicar(Vaga vaga) {
        entityManager.persist(vaga);
        return vaga;
    }

    @Override
    @Transactional(readOnly = true)
    public Vaga buscarPorId(Long id) {
        return entityManager.find(Vaga.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaga> listarAbertas() {
        return entityManager.createQuery(
                        "SELECT v FROM Vaga v WHERE v.situacao = :situacao ORDER BY v.id", Vaga.class)
                .setParameter("situacao", SituacaoVaga.ABERTA)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaga> listarPorEmpresa(Long empresaId) {
        return entityManager.createQuery(
                        "SELECT v FROM Vaga v WHERE v.empresa.id = :empresaId ORDER BY v.id", Vaga.class)
                .setParameter("empresaId", empresaId)
                .getResultList();
    }

    @Override
    public boolean atualizar(Vaga vaga) {
        Vaga existente = entityManager.find(Vaga.class, vaga.getId());
        if (existente == null || !existente.estaAberta()
                || !Objects.equals(existente.getEmpresa().getId(), vaga.getEmpresa().getId())) {
            return false;
        }
        existente.setArea(vaga.getArea());
        existente.setTitulo(vaga.getTitulo());
        existente.setDescricao(vaga.getDescricao());
        existente.setLocalizacao(vaga.getLocalizacao());
        existente.setModalidade(vaga.getModalidade());
        existente.setCargaHorariaSemanal(vaga.getCargaHorariaSemanal());
        return true;
    }

    @Override
    public boolean encerrar(Long vagaId) {
        Vaga vaga = entityManager.find(Vaga.class, vagaId);
        if (vaga == null || !vaga.estaAberta()) {
            return false;
        }
        vaga.setSituacao(SituacaoVaga.ENCERRADA);
        return true;
    }
}
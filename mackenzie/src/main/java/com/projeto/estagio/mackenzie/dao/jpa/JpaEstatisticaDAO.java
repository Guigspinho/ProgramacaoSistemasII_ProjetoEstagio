package com.projeto.estagio.mackenzie.dao.jpa;

import com.projeto.estagio.mackenzie.dao.EstatisticaDAO;
import com.projeto.estagio.mackenzie.model.Candidatura;
import com.projeto.estagio.mackenzie.model.Empresa;
import com.projeto.estagio.mackenzie.model.Estudante;
import com.projeto.estagio.mackenzie.model.SituacaoVaga;
import com.projeto.estagio.mackenzie.model.Vaga;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class JpaEstatisticaDAO implements EstatisticaDAO {
    @Autowired
    private EntityManager entityManager;

    @Override
    public long contarEstudantes() {
        return entityManager.createQuery("SELECT COUNT(e) FROM Estudante e", Long.class).getSingleResult();
    }

    @Override
    public long contarEmpresas() {
        return entityManager.createQuery("SELECT COUNT(e) FROM Empresa e", Long.class).getSingleResult();
    }

    @Override
    public long contarVagasPorSituacao(SituacaoVaga situacao) {
        return entityManager.createQuery(
                        "SELECT COUNT(v) FROM Vaga v WHERE v.situacao = :situacao", Long.class)
                .setParameter("situacao", situacao)
                .getSingleResult();
    }

    @Override
    public long contarCandidaturas() {
        return entityManager.createQuery("SELECT COUNT(c) FROM Candidatura c", Long.class).getSingleResult();
    }
}
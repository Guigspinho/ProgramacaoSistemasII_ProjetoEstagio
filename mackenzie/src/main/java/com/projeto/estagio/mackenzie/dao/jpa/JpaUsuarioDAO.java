package com.projeto.estagio.mackenzie.dao.jpa;

import com.projeto.estagio.mackenzie.dao.UsuarioDAO;
import com.projeto.estagio.mackenzie.model.Administrador;
import com.projeto.estagio.mackenzie.model.Empresa;
import com.projeto.estagio.mackenzie.model.Estudante;
import com.projeto.estagio.mackenzie.model.Perfil;
import com.projeto.estagio.mackenzie.model.Usuario;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class JpaUsuarioDAO implements UsuarioDAO {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Estudante cadastrarEstudante(Estudante estudante) {
        entityManager.persist(estudante);
        return estudante;
    }

    @Override
    public Empresa cadastrarEmpresa(Empresa empresa) {
        entityManager.persist(empresa);
        return empresa;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        List<Usuario> encontrados = entityManager.createQuery(
                        "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();
        return encontrados.isEmpty() ? null : converterParaDominio(encontrados.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        Usuario usuario = entityManager.find(Usuario.class, id);
        return usuario == null ? null : converterParaDominio(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return entityManager.createQuery("SELECT u FROM Usuario u ORDER BY u.id", Usuario.class)
                .getResultList().stream()
                .map(JpaUsuarioDAO::converterParaDominio)
                .toList();
    }

    private static Usuario converterParaDominio(Usuario usuario) {
        if (usuario.getPerfil() == Perfil.ADMINISTRADOR) {
            return new Administrador(usuario.getId(), usuario.getNome(), usuario.getEmail());
        }
        return usuario;
    }
}
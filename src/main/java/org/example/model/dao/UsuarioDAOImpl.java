// src/main/java/org/example/model/dao/UsuarioDAOImpl.java
package org.example.model.dao;

import jakarta.persistence.TypedQuery;
import org.example.model.Usuario;
import org.example.util.HibernateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

/**
 * Implementação de IUsuarioDAO. Garante:
 * - registro com GenericDAO.save(...)
 * - autenticação via query
 * - catch nos blocos JPA
 */
public class UsuarioDAOImpl implements IUsuarioDAO {
    private final GenericDAO<Usuario, Integer> generic = new GenericDAO<>(Usuario.class);

    @Override
    public void save(Usuario u) {
        generic.save(u);
    }

    @Override
    public void update(Usuario u) {
        generic.update(u);
    }

    @Override
    public void delete(Integer id) {
        generic.delete(id);
    }

    @Override
    public Usuario findById(Integer id) {
        return generic.findById(id);
    }

    @Override
    public List<Usuario> findAll() {
        return generic.findAll();
    }

    @Override
    public Usuario findByUserAndPass(String user, String pass) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Usuario> q = em.createQuery(
                    "FROM Usuario u WHERE u.usuario = :u AND u.senha = :s", Usuario.class);
            q.setParameter("u", user);
            q.setParameter("s", pass);
            return q.getResultStream().findFirst().orElse(null);
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }
}

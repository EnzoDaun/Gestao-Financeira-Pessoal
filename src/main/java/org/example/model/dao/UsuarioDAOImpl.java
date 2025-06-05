package org.example.model.dao;

import jakarta.persistence.TypedQuery;
import org.example.model.Usuario;
import org.example.util.HibernateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class UsuarioDAOImpl implements IUsuarioDAO {
    @Override
    public void save(Usuario u) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(u);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Usuario u) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(u);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Integer id) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Usuario ref = em.find(Usuario.class, id);
            if (ref != null) {
                em.remove(ref);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario findById(Integer id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Usuario> findAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Usuario> q = em.createQuery("FROM Usuario", Usuario.class);
            return q.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            em.close();
        }
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
        } catch (Exception ex) {
            throw ex;
        } finally {
            em.close();
        }
    }
}

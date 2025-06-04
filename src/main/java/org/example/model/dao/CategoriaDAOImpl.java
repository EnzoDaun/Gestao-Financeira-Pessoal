// src/main/java/org/example/model/dao/CategoriaDAOImpl.java
package org.example.model.dao;

import org.example.model.Categoria;
import org.example.util.HibernateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;

import java.util.List;

public class CategoriaDAOImpl implements ICategoriaDAO {

    @Override
    public void save(Categoria entity) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public void update(Categoria entity) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(entity);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public void delete(Integer id) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Categoria ref = em.find(Categoria.class, id);
            if (ref != null) {
                em.remove(ref);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Categoria findById(Integer id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Categoria> findAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Categoria> q = em.createQuery("FROM Categoria", Categoria.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Remove a categoria com id = :id somente se pertencer ao usuário com userId.
     * Retorna 1 se efetivamente removida, ou 0 caso a categoria não exista
     * ou não pertença a esse usuário.
     */
    @Override
    public int deleteByIdAndUser(Integer id, Integer userId) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Query q = em.createQuery(
                    "DELETE FROM Categoria c WHERE c.id = :catId AND c.usuario.id = :uid"
            );
            q.setParameter("catId", id);
            q.setParameter("uid", userId);
            int rowsAffected = q.executeUpdate();
            tx.commit();
            return rowsAffected;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}

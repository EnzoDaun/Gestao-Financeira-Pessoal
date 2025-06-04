// src/main/java/org/example/model/dao/GenericDAO.java
package org.example.model.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.util.HibernateUtil;

import java.util.List;

/**
 * DAO genérico para operações básicas de CRUD usando JPA/Hibernate.
 * Todos os métodos possuem try/catch/finally para tratar exceções corretamente
 * e garantir rollback e fechamento do EntityManager.
 *
 * @param <T> Tipo de entidade
 * @param <K> Tipo de chave primária
 */
public class GenericDAO<T, K> {
    private final Class<T> entityClass;

    public GenericDAO(Class<T> clazz) {
        this.entityClass = clazz;
    }

    public void save(T obj) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(obj);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e; // relança para que quem chamou saiba que falhou
        } finally {
            em.close();
        }
    }

    public void update(T obj) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(obj);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(K id) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T ref = em.find(entityClass, id);
            if (ref != null) {
                em.remove(ref);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public T findById(K id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(entityClass, id);
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                    .getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }
}

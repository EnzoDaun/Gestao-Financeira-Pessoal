package org.example.model.dao;

import jakarta.persistence.*;
import org.example.util.HibernateUtil;
import java.util.List;

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
        } finally {
            if (tx.isActive()) tx.rollback();
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
        } finally {
            if (tx.isActive()) tx.rollback();
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
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public T findById(K id) {
        EntityManager em = HibernateUtil.getEntityManager();
        T obj = em.find(entityClass, id);
        em.close();
        return obj;
    }

    public List<T> findAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        List<T> list = em.createQuery(
                "FROM " + entityClass.getSimpleName(), entityClass
        ).getResultList();
        em.close();
        return list;
    }
}

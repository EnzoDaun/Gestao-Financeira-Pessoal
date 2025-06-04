// src/main/java/org/example/model/dao/TransacaoDAOImpl.java
package org.example.model.dao;

import org.example.model.Transacao;
import org.example.util.HibernateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

/**
 * Implementação de ITransacaoDAO:
 * - saveOrUpdate(Transacao): faz persist quando id==null, ou merge quando id!=null.
 * - delete(id) e findAll() delegam a GenericDAO.
 */
public class TransacaoDAOImpl implements ITransacaoDAO {
    private final GenericDAO<Transacao, Integer> generic = new GenericDAO<>(Transacao.class);

    @Override
    public void saveOrUpdate(Transacao t) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (t.getId() == null) {
                em.persist(t);
            } else {
                em.merge(t);
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

    @Override
    public void delete(Integer id) {
        generic.delete(id);
    }

    @Override
    public Transacao findById(Integer id) {
        return generic.findById(id);
    }

    @Override
    public List<Transacao> findAll() {
        return generic.findAll();
    }
}

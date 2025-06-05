package org.example.model.dao;

import org.example.model.Transacao;
import org.example.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TransacaoDAOImpl implements ITransacaoDAO {

    @Override
    public void salvar(Transacao t) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Reatacha Categoria e Usuario antes de persistir:
            t.setCategoria(em.find(t.getCategoria().getClass(), t.getCategoria().getId()));
            t.setUsuario(em.find(t.getUsuario().getClass(), t.getUsuario().getId()));
            em.persist(t);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void atualizar(Transacao t) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            t.setCategoria(em.find(t.getCategoria().getClass(), t.getCategoria().getId()));
            t.setUsuario(em.find(t.getUsuario().getClass(), t.getUsuario().getId()));
            em.merge(t);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(Integer id) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transacao ref = em.find(Transacao.class, id);
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
    public List<Transacao> listarTodos() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Transacao> q = em.createQuery("FROM Transacao", Transacao.class);
            return q.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public Transacao findById(Integer id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Transacao.class, id);
        } catch (Exception ex) {
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Transacao> findByUserId(Integer userId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Transacao> q = em.createQuery(
                    "FROM Transacao t WHERE t.usuario.id = :uid", Transacao.class);
            q.setParameter("uid", userId);
            return q.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            em.close();
        }
    }
}

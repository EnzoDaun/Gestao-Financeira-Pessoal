package org.example.model.dao;

import org.example.model.Transacao;
import org.example.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class TransacaoDAOImpl implements ITransacaoDAO {
    private final GenericDAO<Transacao, Integer> generic = new GenericDAO<>(Transacao.class);

    @Override
    public void salvar(Transacao t) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            t.setCategoria(em.find(t.getCategoria().getClass(), t.getCategoria().getId()));
            t.setUsuario(em.find(t.getUsuario().getClass(), t.getUsuario().getId()));
            em.persist(t);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public void remover(Integer id) {
        generic.delete(id);
    }

    @Override
    public List<Transacao> listarTodos() {
        return generic.findAll();
    }

    @Override
    public Transacao buscarPorId(Integer id) {
        return generic.findById(id);
    }
}

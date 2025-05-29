package org.example.controller;

import org.example.dao.TransacaoDAO;
import org.example.model.Transacao;
import org.example.util.HibernateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class TransacaoController {
    private final TransacaoDAO dao = new TransacaoDAO();

    // Salvar corrigido: reatacha usuário e categoria antes de persistir
    public void salvar(Transacao t) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Reatacha Categoria
            t.setCategoria(em.find(t.getCategoria().getClass(), t.getCategoria().getId()));
            // Reatacha Usuario
            t.setUsuario(em.find(t.getUsuario().getClass(), t.getUsuario().getId()));
            em.persist(t);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void remover(Integer id) {
        dao.delete(id);
    }

    public List<Transacao> listarTodos() {
        return dao.findAll();
    }
}

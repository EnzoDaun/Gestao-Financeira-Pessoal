// src/main/java/org/example/controller/TransacaoController.java
package org.example.controller;

import org.example.model.Transacao;
import org.example.model.Categoria;
import org.example.model.Usuario;
import org.example.model.dao.TransacaoDAOImpl;
import org.example.util.HibernateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class TransacaoController {

    private final TransacaoDAOImpl dao = new TransacaoDAOImpl();

    public void salvar(Transacao t) {
        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Categoria cat = em.find(Categoria.class, t.getCategoria().getId());
            t.setCategoria(cat);

            Usuario usr = em.find(Usuario.class, t.getUsuario().getId());
            t.setUsuario(usr);

            if (t.getId() == null) {
                em.persist(t);
            } else {
                em.merge(t);
            }

            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    public void remover(Integer id) {
        dao.remover(id);
    }

    public List<Transacao> listarTodos() {
        return dao.listarTodos();
    }
}

// src/main/java/org/example/controller/TransacaoController.java
package org.example.controller;

import org.example.model.Transacao;
import org.example.model.dao.TransacaoDAOImpl;

import java.util.List;

/**
 * Controller para Transacao.
 * - saveOrUpdate(Transacao) delega ao DAO para persistir ou merge.
 * - Remove e listar também delegam ao DAO.
 */
public class TransacaoController {
    private final TransacaoDAOImpl dao = new TransacaoDAOImpl();

    public List<Transacao> listarTodos() {
        return dao.findAll();
    }
}

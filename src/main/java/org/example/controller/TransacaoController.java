package org.example.controller;

import org.example.model.Transacao;
import org.example.model.dao.ITransacaoDAO;
import org.example.model.dao.TransacaoDAOImpl;

import java.util.List;

public class TransacaoController {
    private final ITransacaoDAO dao;

    public TransacaoController() {
        this.dao = new TransacaoDAOImpl();
    }

    public void salvar(Transacao t) {
        dao.salvar(t);
    }

    public void remover(Integer id) {
        dao.remover(id);
    }

    public List<Transacao> listarTodos() {
        return dao.listarTodos();
    }

    public Transacao buscarPorId(Integer id) {
        return dao.buscarPorId(id);
    }
}

package org.example.controller;

import org.example.model.Transacao;
import org.example.model.Categoria;
import org.example.model.Usuario;
import org.example.model.dao.ITransacaoDAO;
import org.example.model.dao.TransacaoDAOImpl;
import org.example.model.dao.ICategoriaDAO;
import org.example.model.dao.CategoriaDAOImpl;

import java.util.List;

public class TransacoesController {
    private final ITransacaoDAO daoTransacao = new TransacaoDAOImpl();
    private final ICategoriaDAO daoCategoria = new CategoriaDAOImpl();

    public void salvar(Transacao t) {
        daoTransacao.salvar(t);
    }

    public void atualizar(Transacao t) {
        daoTransacao.atualizar(t);
    }

    public void remover(Integer id) {
        daoTransacao.remover(id);
    }

    public List<Transacao> listarTodos() {
        return daoTransacao.listarTodos();
    }

    public List<Transacao> listarPorUsuario(Usuario u) {
        return daoTransacao.findByUserId(u.getId());
    }

    public List<Categoria> listarCategoriasDoUsuario(Usuario u) {
        return daoCategoria.findByUserId(u.getId());
    }
}

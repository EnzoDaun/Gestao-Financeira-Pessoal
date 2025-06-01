package org.example.controller;

import org.example.model.Categoria;
import org.example.model.Transacao;
import org.example.model.Usuario;
import org.example.model.dao.ICategoriaDAO;
import org.example.model.dao.CategoriaDAOImpl;

import java.util.List;
import java.util.stream.Collectors;

public class CategoriaController {
    private final ICategoriaDAO dao;
    private final TransacaoController transacaoCtrl;

    public CategoriaController() {
        this.dao = new CategoriaDAOImpl();
        this.transacaoCtrl = new TransacaoController();
    }

    public void salvar(Categoria c) {
        dao.salvar(c);
    }

    public void remover(Integer categoriaId, Usuario currentUser) {
        Categoria c = dao.findById(categoriaId);
        if (c == null || !c.getUsuario().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("Categoria não encontrada para este usuário.");
        }

        List<Transacao> todasTransacoes = transacaoCtrl.listarTodos();
        for (Transacao t : todasTransacoes) {
            if (t.getCategoria() != null &&
                    t.getCategoria().getId() != null &&
                    t.getCategoria().getId().equals(categoriaId)) {
                throw new IllegalStateException(
                        "Não é possível excluir a categoria pois há transações vinculadas a ela."
                );
            }
        }

        dao.remover(categoriaId);
    }

    public List<Categoria> listarPorUsuario(Usuario currentUser) {
        return dao.findAll().stream()
                .filter(c -> c.getUsuario() != null &&
                        c.getUsuario().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());
    }

    public Categoria buscarPorId(Integer id) {
        return dao.findById(id);
    }
}

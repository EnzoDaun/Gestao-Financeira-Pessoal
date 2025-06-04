// src/main/java/org/example/controller/TransacaoViewController.java
package org.example.controller;

import org.example.model.Categoria;
import org.example.model.Transacao;
import org.example.model.Usuario;
import org.example.model.dao.CategoriaDAOImpl;
import org.example.model.dao.ICategoriaDAO;
import org.example.model.dao.ITransacaoDAO;
import org.example.model.dao.TransacaoDAOImpl;

import java.util.List;

/**
 * Controller para a View de Transações.
 * Internamente usa apenas DAOs: CategoriaDAOImpl (ICategoriaDAO) e TransacaoDAOImpl (ITransacaoDAO).
 * Não chama nenhum outro Controller.
 */
public class TransacaoViewController {

    private final ICategoriaDAO categoriaDao;
    private final ITransacaoDAO transacaoDao;

    public TransacaoViewController() {
        // Instância de suas implementações concretas
        this.categoriaDao = new CategoriaDAOImpl();
        this.transacaoDao = new TransacaoDAOImpl();
    }

    /**
     * Retorna apenas as categorias que pertencem ao usuário dado.
     * Usa findAll() de CategoriaDAOImpl e filtra pelo user.getId().
     */
    public List<Categoria> listarCategoriasDoUsuario(Usuario user) {
        List<Categoria> todas = categoriaDao.findAll();
        return todas.stream()
                .filter(cat -> cat.getUsuario().getId().equals(user.getId()))
                .toList();
    }

    /**
     * Retorna todas as transações do banco (sem filtrar por usuário).
     * A View depois faz a filtragem por currentUser.getId().
     */
    public List<Transacao> listarTodasTransacoes() {
        return transacaoDao.findAll();
    }

    /**
     * Salva (ou atualiza) a transação.
     * Dentro de TransacaoDAOImpl, o método save(Transacao t) já cuida de reatachar
     * categoria e usuário antes de dar merge/persist.
     */
    public void salvarOuAtualizarTransacao(Transacao t) {
        transacaoDao.saveOrUpdate(t);
    }

    /**
     * Remove a transação cujo ID foi passado.
     */
    public void removerTransacao(Integer id) {
        transacaoDao.delete(id);
    }
}

// src/main/java/org/example/controller/CategoriaController.java
package org.example.controller;

import org.example.model.Categoria;
import org.example.model.Usuario;
import org.example.model.dao.CategoriaDAOImpl;
import org.example.model.dao.ICategoriaDAO;

import java.util.List;

/**
 * Controller de Categorias:
 * – Usa apenas ICategoriaDAO (implementação CategoriaDAOImpl).
 * – Métodos para salvar, editar e remover categorias, além de buscar por ID e listar todas.
 */
public class CategoriaController {
    private final ICategoriaDAO dao;

    public CategoriaController() {
        this.dao = new CategoriaDAOImpl();
    }

    /** Insere uma nova categoria no banco. */
    public void salvar(Categoria c) {
        dao.save(c);
    }

    /** Edita (atualiza) uma categoria existente. */
    public void editarCategoria(Categoria c) {
        dao.update(c);
    }

    /**
     * Remove a categoria de ID `id`, somente se ela pertencer ao usuário `currentUser`.
     * Se nenhuma linha for removida, lança IllegalStateException.
     */
    public void remover(Integer id, Usuario currentUser) {
        int rows = dao.deleteByIdAndUser(id, currentUser.getId());
        if (rows == 0) {
            throw new IllegalStateException(
                    "Não foi possível remover: ou a categoria não existe ou não pertence a este usuário."
            );
        }
    }

    /** Retorna todas as categorias do banco; a View filtra pelo usuário. */
    public List<Categoria> listarTodos() {
        return dao.findAll();
    }

    /**
     * Busca e retorna a Categoria de ID `id`, ou null se não existir.
     */
    public Categoria findById(Integer id) {
        return dao.findById(id);
    }
}

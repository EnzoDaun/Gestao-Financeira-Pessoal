package org.example.controller;

import org.example.model.Categoria;
import org.example.model.Usuario;
import org.example.model.dao.ICategoriaDAO;
import org.example.model.dao.CategoriaDAOImpl;

import java.util.List;

public class CategoriasController {
    private final ICategoriaDAO dao = new CategoriaDAOImpl();

    public void salvar(Categoria c) {
        dao.save(c);
    }

    public void editar(Categoria c) {
        dao.update(c);
    }

    public void remover(Integer id, Usuario currentUser) {
        int rows = dao.deleteByIdAndUser(id, currentUser.getId());
        if (rows == 0) {
            throw new IllegalStateException("Não foi possível remover: ou a categoria não existe ou não pertence a este usuário.");
        }
    }

    public List<Categoria> listarPorUsuario(Usuario u) {
        return dao.findByUserId(u.getId());
    }

    public Categoria findById(Integer id) {
        return dao.findById(id);
    }
}

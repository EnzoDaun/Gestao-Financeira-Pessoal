package org.example.controller;

import org.example.dao.CategoriaDAO;
import org.example.model.Categoria;
import java.util.List;

public class CategoriaController {
    private final CategoriaDAO dao = new CategoriaDAO();

    public void salvar(Categoria c) { dao.save(c); }
    public void remover(Integer id) { dao.delete(id); }
    public List<Categoria> listarTodos() { return dao.findAll(); }
}
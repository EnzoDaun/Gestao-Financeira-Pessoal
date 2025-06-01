package org.example.model.dao;

import org.example.model.Categoria;

import java.util.List;

public interface ICategoriaDAO {

    void salvar(Categoria c);

    void remover(Integer id);

    Categoria findById(Integer id);

    List<Categoria> findAll();
}

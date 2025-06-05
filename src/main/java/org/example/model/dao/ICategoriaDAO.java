package org.example.model.dao;

import org.example.model.Categoria;

import java.util.List;

public interface ICategoriaDAO {
    void save(Categoria entity);

    void update(Categoria entity);

    void delete(Integer id);

    Categoria findById(Integer id);

    List<Categoria> findAll();

    int deleteByIdAndUser(Integer id, Integer userId);

    List<Categoria> findByUserId(Integer userId);
}

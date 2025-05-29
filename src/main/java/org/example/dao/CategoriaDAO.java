package org.example.dao;

import org.example.model.Categoria;

public class CategoriaDAO extends GenericDAO<Categoria,Integer> {
    public CategoriaDAO() { super(Categoria.class); }
}
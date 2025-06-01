package org.example.model.dao;

import org.example.model.Categoria;
import java.util.List;

public class CategoriaDAOImpl implements ICategoriaDAO {
    private final GenericDAO<Categoria, Integer> generic;

    public CategoriaDAOImpl() {
        this.generic = new GenericDAO<>(Categoria.class);
    }

    @Override
    public void salvar(Categoria c) {
        if (c.getId() == null) {
            generic.save(c);
        } else {
            generic.update(c);
        }
    }

    @Override
    public void remover(Integer id) {
        generic.delete(id);
    }

    @Override
    public Categoria findById(Integer id) {
        return generic.findById(id);
    }

    @Override
    public List<Categoria> findAll() {
        return generic.findAll();
    }
}

package org.example.model.dao;

import org.example.model.Usuario;

import java.util.List;

public interface IUsuarioDAO {
    void save(Usuario u);

    void update(Usuario u);

    void delete(Integer id);

    Usuario findById(Integer id);

    List<Usuario> findAll();

    Usuario findByUserAndPass(String usuario, String senha);
}

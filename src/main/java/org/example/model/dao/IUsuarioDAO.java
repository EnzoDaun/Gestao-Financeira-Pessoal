// src/main/java/org/example/model/dao/IUsuarioDAO.java
package org.example.model.dao;

import org.example.model.Usuario;
import java.util.List;

/**
 * Interface para operações de persistência de Usuario.
 */
public interface IUsuarioDAO {
    void save(Usuario u);
    void update(Usuario u);
    void delete(Integer id);
    Usuario findById(Integer id);
    List<Usuario> findAll();
    Usuario findByUserAndPass(String user, String pass);
}

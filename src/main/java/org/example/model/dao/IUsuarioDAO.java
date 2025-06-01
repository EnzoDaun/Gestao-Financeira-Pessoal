package org.example.model.dao;

import org.example.model.Usuario;
import java.util.List;

public interface IUsuarioDAO {
    void salvar(Usuario usuario);

    void remover(Integer id);

    List<Usuario> listarTodos();

    Usuario buscarPorId(Integer id);

    Usuario autenticar(String username, String password);
}

package org.example.controller;

import org.example.model.Usuario;
import org.example.model.dao.IUsuarioDAO;
import org.example.model.dao.UsuarioDAOImpl;

public class UsuarioController {
    private final IUsuarioDAO dao;

    public UsuarioController() {
        this.dao = new UsuarioDAOImpl();
    }

    public Usuario autenticar(String user, String pass) {
        return dao.autenticar(user, pass);
    }

    public void registrar(String user, String pass) {
        Usuario u = new Usuario(user, pass);
        dao.salvar(u);
    }

    public void remover(Integer id) {
        dao.remover(id);
    }

    public Usuario buscarPorId(Integer id) {
        return dao.buscarPorId(id);
    }
}

package org.example.controller;

import org.example.model.Usuario;
import org.example.model.dao.UsuarioDAOImpl;

public class UsuarioController {
    private final UsuarioDAOImpl dao = new UsuarioDAOImpl();

    public Usuario autenticar(String user, String pass) {
        return dao.findByUserAndPass(user, pass);
    }

    public void registrar(String user, String pass) {
        Usuario u = new Usuario(user, pass);
        dao.save(u);
    }

}

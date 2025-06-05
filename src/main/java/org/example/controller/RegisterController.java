package org.example.controller;

import org.example.model.Usuario;
import org.example.model.dao.UsuarioDAOImpl;
import org.example.model.dao.IUsuarioDAO;

public class RegisterController {
    private final IUsuarioDAO dao = new UsuarioDAOImpl();

    public void registrar(String user, String pass) {
        Usuario u = new Usuario(user, pass);
        dao.save(u);
    }
}

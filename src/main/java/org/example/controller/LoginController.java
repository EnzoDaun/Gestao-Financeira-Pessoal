package org.example.controller;

import org.example.model.Usuario;
import org.example.model.dao.UsuarioDAOImpl;
import org.example.model.dao.IUsuarioDAO;

public class LoginController {
    private final IUsuarioDAO dao = new UsuarioDAOImpl();

    public Usuario autenticar(String user, String pass) {
        return dao.findByUserAndPass(user, pass);
    }
}

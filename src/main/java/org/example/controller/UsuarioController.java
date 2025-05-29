package org.example.controller;

import org.example.dao.UsuarioDAO;
import org.example.model.Usuario;

public class UsuarioController {
    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario autenticar(String user, String pass) {
        return dao.findByUserAndPass(user, pass);
    }

    public void registrar(String user, String pass) {
        Usuario u = new Usuario(user, pass);
        dao.save(u);
    }
}
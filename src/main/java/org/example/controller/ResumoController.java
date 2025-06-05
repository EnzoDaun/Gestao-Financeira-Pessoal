package org.example.controller;

import org.example.model.Transacao;
import org.example.model.Usuario;

import java.util.List;

public class ResumoController {
    private final TransacoesController daoTransacao = new TransacoesController();

    public List<Transacao> listarPorUsuario(Usuario u) {
        return daoTransacao.listarPorUsuario(u);
    }
}

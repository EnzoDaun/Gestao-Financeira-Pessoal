package org.example.model.dao;

import org.example.model.Transacao;

import java.util.List;

public interface ITransacaoDAO {
    void salvar(Transacao t);

    void atualizar(Transacao t);

    void remover(Integer id);

    List<Transacao> listarTodos();

    Transacao findById(Integer id);

    List<Transacao> findByUserId(Integer userId);
}

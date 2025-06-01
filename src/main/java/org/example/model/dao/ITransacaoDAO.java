package org.example.model.dao;

import org.example.model.Transacao;
import java.util.List;

public interface ITransacaoDAO {
    void salvar(Transacao transacao);

    void remover(Integer id);

    List<Transacao> listarTodos();

    Transacao buscarPorId(Integer id);
}

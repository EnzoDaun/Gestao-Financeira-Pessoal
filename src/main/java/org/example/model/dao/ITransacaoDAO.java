// src/main/java/org/example/model/dao/ITransacaoDAO.java
package org.example.model.dao;

import org.example.model.Transacao;
import java.util.List;

/**
 * Interface para operações de persistência de Transacao.
 */
public interface ITransacaoDAO {
    /**
     * Insere ou atualiza (edição) a transação.
     * O DAO decide se será persist ou merge.
     */
    void saveOrUpdate(Transacao t);
    void delete(Integer id);
    Transacao findById(Integer id);
    List<Transacao> findAll();
}

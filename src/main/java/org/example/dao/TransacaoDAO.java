package org.example.dao;

import org.example.model.Transacao;

public class TransacaoDAO extends GenericDAO<Transacao,Integer> {
    public TransacaoDAO() { super(Transacao.class); }
}
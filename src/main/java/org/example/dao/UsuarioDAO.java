package org.example.dao;

import jakarta.persistence.TypedQuery;
import org.example.model.Usuario;
import org.example.util.HibernateUtil;

public class UsuarioDAO extends GenericDAO<Usuario,Integer> {
    public UsuarioDAO() { super(Usuario.class); }

    public Usuario findByUserAndPass(String user, String pass) {
        var em = HibernateUtil.getEntityManager();
        TypedQuery<Usuario> q = em.createQuery(
                "FROM Usuario u WHERE u.usuario = :u AND u.senha = :s", Usuario.class);
        q.setParameter("u", user);
        q.setParameter("s", pass);
        Usuario uObj = q.getResultStream().findFirst().orElse(null);
        em.close();
        return uObj;
    }
}
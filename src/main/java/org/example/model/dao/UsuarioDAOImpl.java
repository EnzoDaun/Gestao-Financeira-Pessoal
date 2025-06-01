package org.example.model.dao;

import org.example.model.Usuario;
import org.example.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class UsuarioDAOImpl implements IUsuarioDAO {
    private final GenericDAO<Usuario, Integer> generic = new GenericDAO<>(Usuario.class);

    @Override
    public void salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            generic.save(usuario);
        } else {
            generic.update(usuario);
        }
    }

    @Override
    public void remover(Integer id) {
        generic.delete(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        return generic.findAll();
    }

    @Override
    public Usuario buscarPorId(Integer id) {
        return generic.findById(id);
    }

    @Override
    public Usuario autenticar(String user, String pass) {
        EntityManager em = HibernateUtil.getEntityManager();
        TypedQuery<Usuario> q = em.createQuery(
                "FROM Usuario u WHERE u.usuario = :u AND u.senha = :s", Usuario.class
        );
        q.setParameter("u", user);
        q.setParameter("s", pass);
        Usuario resultado = q.getResultStream().findFirst().orElse(null);
        em.close();
        return resultado;
    }
}

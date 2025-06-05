package org.example.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "usuario",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"usuario"})})
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String usuario;

    @Column(nullable = false)
    private String senha;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "usuario")
    private List<Transacao> transacoes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "usuario")
    private List<Categoria> categorias = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
        this.categorias.add(new Categoria("Geral", this));
    }

    public static boolean senhaFormatoValido(String s) {
        return s != null && s.length() >= 6;
    }

    public Integer getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }
}

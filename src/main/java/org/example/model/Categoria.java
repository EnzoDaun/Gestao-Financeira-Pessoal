package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"nome", "usuario_id"})})
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Categoria() {
    }

    public Categoria(String nome, Usuario usuario) {
        this.nome = nome;
        this.usuario = usuario;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return nome;
    }
}

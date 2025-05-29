package org.example;

// Classe que representa uma categoria financeira
public class Categoria {
    private static int contador = 1;
    public final int id;
    public String nome;

    // Construtor da categoria. Inicializa o id automático e define o nome
    public Categoria(String nome) {
        this.id = contador++;
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}

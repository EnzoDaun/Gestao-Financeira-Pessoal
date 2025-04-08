package org.example;

// Classe categorias.
public class Categoria {
    private static int contador = 1;  // Contador para gerar IDs únicos para cada categoria
    public int id;
    public String nome;

    // Construtor que atribui um ID único e define o nome da categoria
    public Categoria(String nome) {
        this.nome = nome;
        this.id = contador++; // Incrementa o ID gerado
    }

    @Override
    public String toString() {
        return nome;
    }
}

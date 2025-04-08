package org.example;
import java.time.LocalDate;

// Classe que representa uma transação financeira.
public class Transacao {
    public double valor;
    public Categoria categoria;
    public LocalDate data;
    public String descricao;
    public String tipo; // "Receita" ou "Despesa"

    // Construtor para criar uma transação com todos os atributos
    public Transacao(double valor, Categoria categoria, LocalDate data, String descricao, String tipo) {
        this.valor = valor;
        this.categoria = categoria;
        this.data = data;
        this.descricao = descricao;
        this.tipo = tipo;
    }
}

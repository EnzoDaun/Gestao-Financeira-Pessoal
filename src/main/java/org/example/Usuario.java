package org.example;
import java.util.ArrayList;

//Classe usuário, contém suas transações e categorias
public class Usuario {
    public String usuario;
    public String senha;
    public ArrayList<Transacao> transacoes;
    public ArrayList<Categoria> categorias;

    // Construtor do usuário. Inicializa as listas de transações e categorias.
    public Usuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
        transacoes = new ArrayList<>(); // Inicializa a lista de transações
        categorias = new ArrayList<>(); // Inicializa a lista de categorias

        // Categoria default
        categorias.add(new Categoria("Geral"));
    }
}

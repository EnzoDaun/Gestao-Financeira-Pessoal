package org.example;
import java.util.ArrayList;

public class Usuario {
    public String usuario;
    public String senha;
    public ArrayList<Transacao> transacoes;
    public ArrayList<Categoria> categorias;

    // Construtor do usuário inicializa listas e cria categoria "Geral"
    public Usuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
        transacoes = new ArrayList<>();
        categorias = new ArrayList<>();
        categorias.add(new Categoria("Geral"));
    }

    // Método estático para validação de senha
    public static boolean senhaFormatoValido(String senha) {
        return senha != null && senha.length() >= 6;
    }
}

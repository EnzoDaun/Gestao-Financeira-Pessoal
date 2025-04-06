package org.example;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main {

    static class Usuario {
        String usuario;
        String senha;
        ArrayList<Transacao> transacoes;
        ArrayList<Categoria> categorias;

        public Usuario(String usuario, String senha) {
            this.usuario = usuario;
            this.senha = senha;
            transacoes = new ArrayList<>(); // Inicializa a lista de transações
            categorias = new ArrayList<>(); // Inicializa a lista de categorias

            // Categoria default
            categorias.add(new Categoria("Geral"));
        }
    }

    static class Categoria {
        private static int contador = 1;  // Contador para gerar IDs únicos para cada categoria
        int id;
        String nome;

        public Categoria(String nome) {
            this.nome = nome;
            this.id = contador++; // Incrementa o ID gerado
        }

        @Override
        public String toString() {
            return nome;
        }
    }

    static class Transacao {
        double valor;
        Categoria categoria;
        LocalDate data;
        String descricao;
        String tipo; // "Receita" ou "Despesa"

        public Transacao(double valor, Categoria categoria, LocalDate data, String descricao, String tipo) {
            this.valor = valor;
            this.categoria = categoria;
            this.data = data;
            this.descricao = descricao;
            this.tipo = tipo;
        }
    }

    // Dados em memória
    private static ArrayList<Usuario> usuarios = new ArrayList<>();
    private Usuario currentUser;

    // Componentes da interface
    private JFrame loginFrame;                      // Janela de login
    private JFrame mainFrame;                       // Janela principal
    private JTable transacaoTable;                  // Tabela para exibir transações
    private DefaultTableModel transacaoTableModel;  // Modelo da tabela de transações
    private JTable ctgTable;                        // Tabela para exibir categorias
    private DefaultTableModel ctgTableModel;        // Modelo da tabela de categorias
    private JLabel labelTotal;                      // Label para exibir o saldo total
    private JLabel labelReceitas;                   // Label para exibir o total de receitas
    private JLabel labelDespesas;                   // Label para exibir o total de despesas

    // Atualiza o comboBox de categorias na aba de transações
    private JComboBox<Categoria> cbCategoriaTransacao;

    // Construtor
    public Main() {
        showLogin();  // Inicia a tela de login
    }

    // Tela de Login e Registro
    private void showLogin() {
        loginFrame = new JFrame("Login - Gestão Financeira Pessoal");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLocationRelativeTo(null); // Centraliza a janela

        JPanel panel = new JPanel(new GridLayout(4, 1));
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        panel.add(new JLabel("Usuário:"));
        panel.add(txtUser);
        panel.add(new JLabel("Senha:"));
        panel.add(txtPass);

        JPanel panelButtons = new JPanel();
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Registrar");
        panelButtons.add(btnLogin);
        panelButtons.add(btnRegister);

        loginFrame.add(panel, BorderLayout.CENTER);
        loginFrame.add(panelButtons, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> {
            String usuario = txtUser.getText().trim();
            String senha = new String(txtPass.getPassword());
            Usuario user = autenticar(usuario, senha);
            if (user != null) {
                currentUser = user;  // Atribui o usuário como atual
                loginFrame.dispose(); // Fecha a tela de login
                showMainFrame(); // Abre tela principal
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Usuário ou senha incorretos!");
            }
        });

        btnRegister.addActionListener(e -> {
            String usuario = txtUser.getText().trim();
            String senha = new String(txtPass.getPassword());
            if (usuario.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Preencha usuário e senha para registrar!");
                return;
            }
            if (autenticar(usuario, senha) != null) {
                JOptionPane.showMessageDialog(loginFrame, "Usuário já existe!");
                return;
            }
            Usuario novo = new Usuario(usuario, senha);
            usuarios.add(novo);
            JOptionPane.showMessageDialog(loginFrame, "Registrado com sucesso! Faça login.");
        });

        loginFrame.setVisible(true);
    }

    private Usuario autenticar(String usuario, String senha) {
        for (Usuario u : usuarios) {
            if (u.usuario.equals(usuario) && u.senha.equals(senha)) {
                return u;
            }
        }
        return null; // Se o usuário não for encontrado retorna nulo
    }

    // Tela Principal
    private void showMainFrame() {
        mainFrame = new JFrame("Gestão Financeira Pessoal - " + currentUser.usuario);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);

        // Abas da aplicação
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Transações", createTransacoesPanel());
        tabbedPane.addTab("Resumo", createResumoPanel());
        tabbedPane.addTab("Categorias", createCategoriasPanel());

        mainFrame.add(tabbedPane); // Adiciona as abas para que sejam exibidas
        mainFrame.setVisible(true);
    }

    // Painel de Transações: permite adicionar, visualizar e remover transações financeiras
    private JPanel createTransacoesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabela para exibir transações
        transacaoTableModel = new DefaultTableModel(new Object[]{"Tipo", "Valor", "Categoria", "Data", "Descrição"}, 0);
        transacaoTable = new JTable(transacaoTableModel);
        JScrollPane scrollPane = new JScrollPane(transacaoTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Formulário para adicionar transação
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.add(new JLabel("Tipo:"));
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Receita", "Despesa"});
        formPanel.add(cbTipo);

        formPanel.add(new JLabel("Valor:"));
        JTextField txtValor = new JTextField();
        formPanel.add(txtValor);

        formPanel.add(new JLabel("Categoria:"));
        // Armazena o combo de categorias para atualizações futuras
        cbCategoriaTransacao = new JComboBox<>();
        atualizarCategoriasCombo(cbCategoriaTransacao);
        formPanel.add(cbCategoriaTransacao);

        formPanel.add(new JLabel("Data (YYYY-MM-DD):"));
        JTextField txtData = new JTextField(LocalDate.now().toString());
        formPanel.add(txtData);

        formPanel.add(new JLabel("Descrição:"));
        JTextField txtDesc = new JTextField();
        formPanel.add(txtDesc);

        // Botão para adicionar transação
        JButton btnAdd = new JButton("Adicionar Transação");
        formPanel.add(btnAdd);
        // Botão para remover transação
        JButton btnRemover = new JButton("Remover Transação Selecionada");
        formPanel.add(btnRemover);

        panel.add(formPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            try {
                String tipo = (String) cbTipo.getSelectedItem();
                double valor = Double.parseDouble(txtValor.getText());
                Categoria cat = (Categoria) cbCategoriaTransacao.getSelectedItem();
                LocalDate data = LocalDate.parse(txtData.getText());
                String desc = txtDesc.getText();

                Transacao t = new Transacao(valor, cat, data, desc, tipo);
                currentUser.transacoes.add(t);

                transacaoTableModel.addRow(new Object[]{tipo, valor, cat.nome, data.toString(), desc});
                atualizarResumo();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erro ao adicionar transação: " + ex.getMessage());
            }
        });

        btnRemover.addActionListener(e -> {
            int selectedRow = transacaoTable.getSelectedRow();
            if (selectedRow != -1) {
                // Remove do modelo da tabela
                transacaoTableModel.removeRow(selectedRow);
                // Remove da lista do usuário
                currentUser.transacoes.remove(selectedRow);
                atualizarResumo();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Selecione uma transação para remover.");
            }
        });

        return panel;
    }

    // Painel de Resumo Financeiro: exibe os totais de receitas, despesas e o saldo
    private JPanel createResumoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        labelTotal = new JLabel("Saldo Total: 0.0");
        labelReceitas = new JLabel("Total Receitas: 0.0");
        labelDespesas = new JLabel("Total Despesas: 0.0");

        infoPanel.add(labelReceitas);
        infoPanel.add(labelDespesas);
        infoPanel.add(labelTotal);
        panel.add(infoPanel, BorderLayout.NORTH);

        JButton btnAtualizar = new JButton("Atualizar Resumo");
        btnAtualizar.addActionListener(e -> atualizarResumo());
        panel.add(btnAtualizar, BorderLayout.SOUTH);
        return panel;
    }

    // Painel de Categorias: permite adicionar e remover categorias financeiras
    private JPanel createCategoriasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        ctgTableModel = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0);
        ctgTable = new JTable(ctgTableModel);
        JScrollPane scrollPane = new JScrollPane(ctgTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.add(new JLabel("Nome da Categoria:"));
        JTextField txtCatNome = new JTextField();
        formPanel.add(txtCatNome);
        JButton btnAddCat = new JButton("Adicionar");
        formPanel.add(btnAddCat);
        JButton btnRemoverCat = new JButton("Remover Selecionada");
        formPanel.add(btnRemoverCat);
        panel.add(formPanel, BorderLayout.SOUTH);

        btnAddCat.addActionListener(e -> {
            String nome = txtCatNome.getText().trim();
            if (!nome.isEmpty()) {
                Categoria cat = new Categoria(nome);
                currentUser.categorias.add(cat);
                ctgTableModel.addRow(new Object[]{cat.id, cat.nome});
                txtCatNome.setText("");
                atualizarCategoriasCombo(cbCategoriaTransacao);// Atualiza o comboBox na aba de transações
            }
        });

        btnRemoverCat.addActionListener(e -> {
            int selectedRow = ctgTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) ctgTableModel.getValueAt(selectedRow, 0);
                currentUser.categorias.removeIf(c -> c.id == id);
                ctgTableModel.removeRow(selectedRow);
                atualizarCategoriasCombo(cbCategoriaTransacao);
            }
        });

        return panel;
    }

    // Atualiza o combobox de categorias na aba de transações
    private void atualizarCategoriasCombo(JComboBox<Categoria> cb) {
        if (cb != null) {
            cb.removeAllItems(); // Remove itens antigos
            for (Categoria c : currentUser.categorias) {
                cb.addItem(c); // Adiciona cada categoria disponível do usuário
            }
        }
    }

    // Atualiza os valores do resumo financeiro
    private void atualizarResumo() {
        double totalReceitas = 0;
        double totalDespesas = 0;
        for (Transacao t : currentUser.transacoes) {  // Percorre todas as transações do usuário
            if (t.tipo.equals("Receita"))
                totalReceitas += t.valor;
            else
                totalDespesas += t.valor;
        }
        double saldo = totalReceitas - totalDespesas;
        // Atualiza as labels com os novos valores
        labelReceitas.setText("Total Receitas: " + totalReceitas);
        labelDespesas.setText("Total Despesas: " + totalDespesas);
        labelTotal.setText("Saldo Total: " + saldo);
    }

    public static void main(String[] args) {
        usuarios.add(new Usuario("admin", "admin")); // Usuário padrão para testar
        SwingUtilities.invokeLater(() -> new Main());  // Inicia a interface gráfica
    }
}
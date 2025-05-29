package org.example;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    // Formatos de data, hora e moeda
    private static final DateTimeFormatter BR_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HR_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // Categoria "Todas" para filtro
    private static final Categoria ALL_CATEGORY = new Categoria("Todas");

    // Dados em memória
    private static ArrayList<Usuario> usuarios = new ArrayList<>();
    private Usuario currentUser;

    // Frames e componentes principais
    private JFrame welcomeFrame, registerFrame, mainFrame;
    private JLabel lblUsuarioHora;

    // Aba Transações
    private JTable transacaoTable;
    private DefaultTableModel transacaoModel;
    private TableRowSorter<TableModel> transacaoSorter;
    private JComboBox<String> filtroTipo;
    private JTextField filtroValor;
    private JComboBox<Categoria> filtroCategoria;
    private JFormattedTextField filtroData;
    private JTextField filtroDesc;
    private JComboBox<Categoria> cbCategoriaTransacao;

    // Aba Resumo
    private JLabel labelReceitas, labelDespesas, labelTotal;
    private JButton btnAtualizarResumo;

    // Aba Categorias
    private JTable ctgTable;
    private DefaultTableModel ctgModel;
    private TableRowSorter<TableModel> ctgSorter;
    private JTextField filtroCtgId, filtroCtgNome;

    public Main() {
        // Usuário padrão
        usuarios.add(new Usuario("admin", "admin"));
        SwingUtilities.invokeLater(this::showWelcome);
    }

    private void showWelcome() {
        welcomeFrame = new JFrame("Bem-vindo");
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setResizable(false);

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(
                "<html><div style='text-align:center;'>Bem-vindo ao Sistema<br/>de Gestão Financeira Pessoal</div></html>",
                SwingConstants.CENTER
        );
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(15));

        JPanel pnlLogin = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        pnlLogin.add(new JLabel("Usuário:"));
        pnlLogin.add(txtUser);
        pnlLogin.add(new JLabel("Senha:"));
        pnlLogin.add(txtPass);
        pnlLogin.setMaximumSize(pnlLogin.getPreferredSize());
        content.add(pnlLogin);
        content.add(Box.createVerticalStrut(15));

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnLogin = new JButton("Login");
        JButton btnOpenRegister = new JButton("Registrar");
        pnlBtns.add(btnLogin);
        pnlBtns.add(btnOpenRegister);
        content.add(pnlBtns);

        welcomeFrame.setContentPane(content);
        welcomeFrame.pack();
        welcomeFrame.setLocationRelativeTo(null);
        welcomeFrame.setVisible(true);

        btnLogin.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String s = new String(txtPass.getPassword());
            if (u.isEmpty() || s.isEmpty()) {
                JOptionPane.showMessageDialog(welcomeFrame, "Digite usuário e senha!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Usuario auth = autenticar(u, s);
            if (auth != null) {
                currentUser = auth;
                welcomeFrame.dispose();
                showMainFrame();
            } else {
                JOptionPane.showMessageDialog(welcomeFrame, "Usuário ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnOpenRegister.addActionListener(e -> {
            welcomeFrame.setEnabled(false);
            showRegister();
        });
    }

    private void showRegister() {
        registerFrame = new JFrame("Registrar Novo Usuário");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setResizable(false);

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel pnl = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JPasswordField txtPassConf = new JPasswordField(15);
        pnl.add(new JLabel("Usuário:"));
        pnl.add(txtUser);
        pnl.add(new JLabel("Senha:"));
        pnl.add(txtPass);
        pnl.add(new JLabel("Confirmar Senha:"));
        pnl.add(txtPassConf);
        pnl.setMaximumSize(pnl.getPreferredSize());
        content.add(pnl);
        content.add(Box.createVerticalStrut(10));

        JButton btnRegister = new JButton("Registrar");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(btnRegister);

        registerFrame.setContentPane(content);
        registerFrame.pack();
        registerFrame.setLocationRelativeTo(welcomeFrame);
        registerFrame.setVisible(true);

        registerFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                welcomeFrame.setEnabled(true);
            }
        });

        btnRegister.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String s = new String(txtPass.getPassword());
            String sc = new String(txtPassConf.getPassword());
            if (u.isEmpty() || s.isEmpty() || sc.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!s.equals(sc)) {
                JOptionPane.showMessageDialog(registerFrame, "Senhas não conferem!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Usuario.senhaFormatoValido(s)) {
                JOptionPane.showMessageDialog(registerFrame, "Senha deve ter ao menos 6 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (autenticar(u, s) != null) {
                JOptionPane.showMessageDialog(registerFrame, "Usuário já existe!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            usuarios.add(new Usuario(u, s));
            JOptionPane.showMessageDialog(registerFrame, "Registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            registerFrame.dispose();
            welcomeFrame.setEnabled(true);
        });
    }

    private Usuario autenticar(String usuario, String senha) {
        return usuarios.stream()
                .filter(u -> u.usuario.equals(usuario) && u.senha.equals(senha))
                .findFirst().orElse(null);
    }

    private void showMainFrame() {
        mainFrame = new JFrame("Gestão Financeira - " + currentUser.usuario);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 650);
        mainFrame.setLocationRelativeTo(null);

        JPanel top = new JPanel(new BorderLayout());
        lblUsuarioHora = new JLabel();
        lblUsuarioHora.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        top.add(lblUsuarioHora, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(mainFrame,
                    "Deseja mesmo sair do sistema?",
                    "Confirmar Logout",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                showWelcome();
            }
        });
        top.add(btnLogout, BorderLayout.EAST);

        new Timer(60_000, ev ->
                lblUsuarioHora.setText(currentUser.usuario + " — " + LocalTime.now().format(HR_FORMAT))
        ).start();
        lblUsuarioHora.setText(currentUser.usuario + " — " + LocalTime.now().format(HR_FORMAT));
        mainFrame.add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Transações", createTransacoesPanel());
        tabs.addTab("Resumo", createResumoPanel());
        tabs.addTab("Categorias", createCategoriasPanel());
        mainFrame.add(tabs, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    private JPanel createTransacoesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtroTipo = new JComboBox<>(new String[]{"Todas", "Receita", "Despesa"});
        filtroTipo.setPreferredSize(new Dimension(100, 25));
        filtros.add(new JLabel("Tipo:"));
        filtros.add(filtroTipo);

        filtroValor = new JTextField();
        filtroValor.setPreferredSize(new Dimension(80, 25));
        filtroValor.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != ',' && c != '.' && c != KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
        });
        filtros.add(new JLabel("Valor:"));
        filtros.add(filtroValor);

        filtroCategoria = new JComboBox<>();
        filtroCategoria.setPreferredSize(new Dimension(120, 25));
        atualizarCategoriasComboFiltro(filtroCategoria);
        filtros.add(new JLabel("Categoria:"));
        filtros.add(filtroCategoria);

        try {
            MaskFormatter mf = new MaskFormatter("##/##/####");
            mf.setPlaceholderCharacter('_');
            filtroData = new JFormattedTextField(new DefaultFormatterFactory(mf));
        } catch (ParseException ex) {
            filtroData = new JFormattedTextField();
        }
        filtroData.setColumns(8);
        filtros.add(new JLabel("Data:"));
        filtros.add(filtroData);

        filtroDesc = new JTextField();
        filtroDesc.setPreferredSize(new Dimension(120, 25));
        filtros.add(new JLabel("Desc:"));
        filtros.add(filtroDesc);

        JButton btnClearFiltros = new JButton("Limpar Filtros");
        filtros.add(btnClearFiltros);

        panel.add(filtros, BorderLayout.NORTH);

        // Tabela
        String[] cols = {"Tipo", "Valor", "Categoria", "Data", "Descrição"};
        transacaoModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c != 2;
            }
        };
        transacaoTable = new JTable(transacaoModel);
        transacaoSorter = new TableRowSorter<>(transacaoModel);
        transacaoTable.setRowSorter(transacaoSorter);
        panel.add(new JScrollPane(transacaoTable), BorderLayout.CENTER);

        // Carrega transações existentes
        for (Transacao t : currentUser.transacoes) {
            transacaoModel.addRow(new Object[]{
                    t.tipo,
                    CURRENCY.format(t.valor),
                    t.categoria.nome,
                    t.data.format(BR_FORMAT),
                    t.descricao
            });
        }

        // ENTER dispara filtro
        ActionListener filtrarEnter = e -> aplicarFiltroTransacoes();
        filtroTipo.addActionListener(filtrarEnter);
        filtroValor.addActionListener(filtrarEnter);
        filtroCategoria.addActionListener(filtrarEnter);
        filtroData.addActionListener(filtrarEnter);
        filtroDesc.addActionListener(filtrarEnter);

        btnClearFiltros.addActionListener(e -> {
            filtroTipo.setSelectedIndex(0);
            filtroValor.setText("");
            filtroCategoria.setSelectedItem(ALL_CATEGORY);
            filtroData.setText("");
            filtroDesc.setText("");
            transacaoSorter.setRowFilter(null);
        });

        // Formulário de nova transação
        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Receita", "Despesa"});
        JTextField txtValor = new JTextField();
        txtValor.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != ',' && c != '.') e.consume();
            }
        });
        cbCategoriaTransacao = new JComboBox<>();
        atualizarCategoriasComboTransacao(cbCategoriaTransacao);
        JTextField txtData = new JTextField(LocalDate.now().format(BR_FORMAT));
        JTextField txtDesc = new JTextField();
        form.add(new JLabel("Tipo:"));
        form.add(cbTipo);
        form.add(new JLabel("Valor:"));
        form.add(txtValor);
        form.add(new JLabel("Categoria:"));
        form.add(cbCategoriaTransacao);
        form.add(new JLabel("Data:"));
        form.add(txtData);
        form.add(new JLabel("Descrição:"));
        form.add(txtDesc);
        JButton btnAdd = new JButton("Adicionar Transação");
        JButton btnRem = new JButton("Remover Selecionada");
        form.add(btnAdd);
        form.add(btnRem);
        panel.add(form, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            if (txtValor.getText().isEmpty() || txtData.getText().isEmpty() || txtDesc.getText().isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double val;
            try {
                val = Double.parseDouble(txtValor.getText().replace(',', '.'));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate d;
            try {
                d = LocalDate.parse(txtData.getText(), BR_FORMAT);
                if (d.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(mainFrame, "Data não pode ser passada!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Data inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String tipo = (String) cbTipo.getSelectedItem();
            Categoria c = (Categoria) cbCategoriaTransacao.getSelectedItem();
            Transacao t = new Transacao(val, c, d, txtDesc.getText(), tipo);
            currentUser.transacoes.add(t);
            transacaoModel.addRow(new Object[]{
                    tipo, CURRENCY.format(val), c.nome, d.format(BR_FORMAT), txtDesc.getText()
            });
            txtValor.setText("");
            txtDesc.setText("");
        });

        btnRem.addActionListener(e -> {
            int sel = transacaoTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(mainFrame, "Selecione uma transação!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(mainFrame,
                    "Deseja mesmo excluir a transação?", "Confirmar", JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION) return;
            int idx = transacaoTable.convertRowIndexToModel(sel);
            transacaoModel.removeRow(idx);
            currentUser.transacoes.remove(idx);
        });

        return panel;
    }

    private void aplicarFiltroTransacoes() {
        List<RowFilter<Object, Object>> filtros = new ArrayList<>();
        String ft = (String) filtroTipo.getSelectedItem();
        if (ft != null && !ft.equals("Todas")) filtros.add(RowFilter.regexFilter("^" + ft + "$", 0));
        String fv = filtroValor.getText().trim();
        if (!fv.isEmpty()) filtros.add(RowFilter.regexFilter(fv.replace(".", "\\.").replace(",", "\\."), 1));
        Categoria fc = (Categoria) filtroCategoria.getSelectedItem();
        if (fc != null && fc != ALL_CATEGORY) filtros.add(RowFilter.regexFilter("^" + fc.nome + "$", 2));
        String fd = filtroData.getText();
        if (fd != null && !fd.contains("_")) filtros.add(RowFilter.regexFilter("^" + fd + "$", 3));
        String fdsc = filtroDesc.getText().trim();
        if (!fdsc.isEmpty()) filtros.add(RowFilter.regexFilter("(?i)" + fdsc, 4));
        transacaoSorter.setRowFilter(filtros.isEmpty() ? null : RowFilter.andFilter(filtros));
    }

    private JPanel createResumoPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel info = new JPanel(new GridLayout(3, 1, 5, 5));
        labelReceitas = new JLabel("Total Receitas: 0.0");
        labelDespesas = new JLabel("Total Despesas: 0.0");
        labelTotal = new JLabel("Saldo Total: 0.0");
        Font f = labelTotal.getFont().deriveFont(Font.BOLD, labelTotal.getFont().getSize() + 2f);
        labelReceitas.setFont(f);
        labelDespesas.setFont(f);
        labelTotal.setFont(f);
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        info.add(labelReceitas);
        info.add(labelDespesas);
        info.add(labelTotal);
        p.add(info, BorderLayout.CENTER);

        btnAtualizarResumo = new JButton("Atualizar Resumo");
        btnAtualizarResumo.addActionListener(e -> atualizarResumo());
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnl.add(btnAtualizarResumo);
        p.add(pnl, BorderLayout.SOUTH);

        return p;
    }

    private void atualizarResumo() {
        double r = 0, d = 0;
        for (Transacao t : currentUser.transacoes) {
            if ("Receita".equals(t.tipo)) r += t.valor;
            else d += t.valor;
        }
        labelReceitas.setText("Total Receitas: " + CURRENCY.format(r));
        labelDespesas.setText("Total Despesas: " + CURRENCY.format(d));
        labelTotal.setText("Saldo Total: " + CURRENCY.format(r - d));
    }

    private JPanel createCategoriasPanel() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtros.add(new JLabel("ID:"));
        filtroCtgId = new JTextField(5);
        filtros.add(filtroCtgId);
        filtros.add(new JLabel("Nome:"));
        filtroCtgNome = new JTextField(10);
        filtros.add(filtroCtgNome);
        JButton btnClearCtg = new JButton("Limpar Filtros");
        filtros.add(btnClearCtg);
        p.add(filtros, BorderLayout.NORTH);

        ctgModel = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0);
        ctgTable = new JTable(ctgModel);
        ctgSorter = new TableRowSorter<>(ctgModel);
        ctgTable.setRowSorter(ctgSorter);
        currentUser.categorias.forEach(c -> ctgModel.addRow(new Object[]{c.id, c.nome}));
        p.add(new JScrollPane(ctgTable), BorderLayout.CENTER);

        ActionListener filCtg = e -> {
            List<RowFilter<Object, Object>> fl = new ArrayList<>();
            String i = filtroCtgId.getText().trim();
            if (!i.isEmpty()) {
                try {
                    fl.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, Integer.parseInt(i), 0));
                } catch (NumberFormatException ignore) {
                }
            }
            String n = filtroCtgNome.getText().trim();
            if (!n.isEmpty()) {
                fl.add(RowFilter.regexFilter("(?i)" + n, 1));
            }
            ctgSorter.setRowFilter(fl.isEmpty() ? null : RowFilter.andFilter(fl));
        };
        filtroCtgId.addActionListener(filCtg);
        filtroCtgNome.addActionListener(filCtg);

        btnClearCtg.addActionListener(e -> {
            filtroCtgId.setText("");
            filtroCtgNome.setText("");
            ctgSorter.setRowFilter(null);
        });

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField txtCat = new JTextField(15);
        JButton btnAddC = new JButton("Adicionar");
        JButton btnRemC = new JButton("Remover Selecionada");
        form.add(new JLabel("Nome da Categoria:"));
        form.add(txtCat);
        form.add(btnAddC);
        form.add(btnRemC);
        p.add(form, BorderLayout.SOUTH);

        btnAddC.addActionListener(e -> {
            String nome = txtCat.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Preencha o nome da categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Categoria c = new Categoria(nome);
            currentUser.categorias.add(c);
            ctgModel.addRow(new Object[]{c.id, c.nome});
            txtCat.setText("");
            atualizarCategoriasComboTransacao(cbCategoriaTransacao);
            atualizarCategoriasComboFiltro(filtroCategoria);
        });

        btnRemC.addActionListener(e -> {
            int sel = ctgTable.getSelectedRow();
            if (sel < 0) return;
            if (JOptionPane.showConfirmDialog(mainFrame,
                    "Deseja mesmo excluir a categoria?", "Confirmar", JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION) return;
            int idx = ctgTable.convertRowIndexToModel(sel);
            int id = (int) ctgModel.getValueAt(idx, 0);
            currentUser.categorias.removeIf(x -> x.id == id);
            ctgModel.removeRow(idx);
            atualizarCategoriasComboTransacao(cbCategoriaTransacao);
            atualizarCategoriasComboFiltro(filtroCategoria);
        });

        return p;
    }

    // Atualiza combo de categorias para FILTRO, incluindo ALL_CATEGORY
    private void atualizarCategoriasComboFiltro(JComboBox<Categoria> cb) {
        cb.removeAllItems();
        cb.addItem(ALL_CATEGORY);
        for (Categoria c : currentUser.categorias) {
            cb.addItem(c);
        }
    }

    // Atualiza combo de categorias para NOVA TRANSAÇÃO, sem ALL_CATEGORY
    private void atualizarCategoriasComboTransacao(JComboBox<Categoria> cb) {
        cb.removeAllItems();
        for (Categoria c : currentUser.categorias) {
            cb.addItem(c);
        }
    }
}
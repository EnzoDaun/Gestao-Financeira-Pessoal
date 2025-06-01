package org.example.view;

import org.example.controller.CategoriaController;
import org.example.controller.TransacaoController;
import org.example.model.Categoria;
import org.example.model.Transacao;
import org.example.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransacoesView {
    private final Usuario currentUser;
    private final CategoriaController categoriaCtrl;
    private final TransacaoController transacaoCtrl;

    private static final DateTimeFormatter BR_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale LOCALE_BR = new Locale("pt", "BR");

    private static final Categoria ALL_CATEGORY = new Categoria("Todas", null);

    private JTable transacaoTable;
    private DefaultTableModel transacaoModel;
    private TableRowSorter<DefaultTableModel> transacaoSorter;

    private JComboBox<String> filtroTipo;
    private JTextField filtroValor;
    private JComboBox<Categoria> filtroCategoria;
    private JFormattedTextField filtroData;
    private JTextField filtroDesc;

    private JComboBox<Categoria> cbCategoriaTransacao;

    public TransacoesView(Usuario currentUser,
                          CategoriaController categoriaCtrl,
                          TransacaoController transacaoCtrl) {
        this.currentUser = currentUser;
        this.categoriaCtrl = categoriaCtrl;
        this.transacaoCtrl = transacaoCtrl;
    }

    public JPanel getPanel() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtroTipo = new JComboBox<>(new String[]{"Todas", "Receita", "Despesa"});
        filtroValor = new JTextField(8);
        filtroValor.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != ',' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });

        filtroCategoria = new JComboBox<>();
        recarregarFiltroCategoriaCombo();

        try {
            MaskFormatter mf = new MaskFormatter("##/##/####");
            mf.setPlaceholderCharacter('_');
            filtroData = new JFormattedTextField(new DefaultFormatterFactory(mf));
        } catch (ParseException ex) {
            filtroData = new JFormattedTextField();
        }

        filtroDesc = new JTextField(10);

        filtros.add(new JLabel("Tipo:"));
        filtros.add(filtroTipo);
        filtros.add(new JLabel("Valor:"));
        filtros.add(filtroValor);
        filtros.add(new JLabel("Categoria:"));
        filtros.add(filtroCategoria);
        filtros.add(new JLabel("Data:"));
        filtros.add(filtroData);
        filtros.add(new JLabel("Desc:"));
        filtros.add(filtroDesc);

        JButton btnClearFiltros = new JButton("Limpar Filtros");
        btnClearFiltros.addActionListener(e -> limparFiltros());
        filtros.add(btnClearFiltros);

        p.add(filtros, BorderLayout.NORTH);

        String[] colunas = {"ID", "Tipo", "Valor", "Categoria", "Data", "Descrição"};
        transacaoModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        transacaoTable = new JTable(transacaoModel);
        transacaoSorter = new TableRowSorter<>(transacaoModel);
        transacaoTable.setRowSorter(transacaoSorter);
        p.add(new JScrollPane(transacaoTable), BorderLayout.CENTER);

        ActionListener aoFiltrar = e -> aplicarFiltro();
        filtroTipo.addActionListener(aoFiltrar);
        filtroValor.addActionListener(aoFiltrar);
        filtroCategoria.addActionListener(aoFiltrar);
        filtroData.addActionListener(aoFiltrar);
        filtroDesc.addActionListener(aoFiltrar);

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Receita", "Despesa"});

        JTextField txtValor = new JTextField();
        txtValor.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != ',') {
                    e.consume();
                }
            }
        });

        JTextField txtData = new JTextField(LocalDate.now().format(BR_FORMAT));
        JTextField txtDesc = new JTextField();

        cbCategoriaTransacao = new JComboBox<>();
        recarregarTransacaoCategoriaCombo();

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
        p.add(form, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            if (txtValor.getText().isEmpty() ||
                    txtData.getText().isEmpty() ||
                    txtDesc.getText().isEmpty()) {
                JOptionPane.showMessageDialog(p,
                        "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double val;
            try {
                val = Double.parseDouble(txtValor.getText().replace(',', '.'));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p,
                        "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate d;
            try {
                d = LocalDate.parse(txtData.getText(), BR_FORMAT);
                if (d.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(p,
                            "Data não pode ser anterior!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p,
                        "Data inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Categoria escolhida = (Categoria) cbCategoriaTransacao.getSelectedItem();
            Transacao nova = new Transacao(
                    val,
                    escolhida,
                    d,
                    txtDesc.getText(),
                    (String) cbTipo.getSelectedItem(),
                    currentUser
            );
            transacaoCtrl.salvar(nova);
            recarregarTabela();
            txtValor.setText("");
            txtDesc.setText("");
        });

        btnRem.addActionListener(e -> {
            int sel = transacaoTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(p,
                        "Selecione uma transação!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(p,
                    "Deseja mesmo excluir a transação?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            int modelIdx = transacaoTable.convertRowIndexToModel(sel);
            Integer id = (Integer) transacaoModel.getValueAt(modelIdx, 0);
            transacaoCtrl.remover(id);
            recarregarTabela();
        });

        recarregarTabela();
        return p;
    }

    public void recarregarCategorias() {
        recarregarFiltroCategoriaCombo();
        recarregarTransacaoCategoriaCombo();
    }

    private void recarregarFiltroCategoriaCombo() {
        filtroCategoria.removeAllItems();
        filtroCategoria.addItem(ALL_CATEGORY);
        for (Categoria c : categoriaCtrl.listarPorUsuario(currentUser)) {
            filtroCategoria.addItem(c);
        }
    }

    private void recarregarTransacaoCategoriaCombo() {
        cbCategoriaTransacao.removeAllItems();
        for (Categoria c : categoriaCtrl.listarPorUsuario(currentUser)) {
            cbCategoriaTransacao.addItem(c);
        }
    }

    private void limparFiltros() {
        filtroTipo.setSelectedIndex(0);
        filtroValor.setText("");
        filtroCategoria.setSelectedIndex(0); // “Todas”
        filtroData.setText("");
        filtroDesc.setText("");
        transacaoSorter.setRowFilter(null);
    }

    private void aplicarFiltro() {
        List<RowFilter<Object, Object>> lista = new ArrayList<>();

        String ft = (String) filtroTipo.getSelectedItem();
        if (ft != null && !ft.equals("Todas")) {
            lista.add(RowFilter.regexFilter("^" + ft + "$", 1));
        }

        String fv = filtroValor.getText().trim();
        if (!fv.isEmpty()) {
            fv = fv.replace(".", "\\.").replace(",", "\\.");
            lista.add(RowFilter.regexFilter(fv, 2));
        }

        Categoria fc = (Categoria) filtroCategoria.getSelectedItem();
        if (fc != null && fc.getUsuario() != null) {
            lista.add(RowFilter.regexFilter("^" + fc.getNome() + "$", 3));
        }

        String fd = filtroData.getText();
        if (fd != null && !fd.contains("_")) {
            lista.add(RowFilter.regexFilter("^" + fd + "$", 4));
        }

        String fdesc = filtroDesc.getText().trim();
        if (!fdesc.isEmpty()) {
            lista.add(RowFilter.regexFilter("(?i)" + fdesc, 5));
        }

        transacaoSorter.setRowFilter(lista.isEmpty() ? null : RowFilter.andFilter(lista));
    }

    private void recarregarTabela() {
        transacaoModel.setRowCount(0);
        for (Transacao t : transacaoCtrl.listarTodos()) {
            if (!t.getUsuario().getId().equals(currentUser.getId())) continue;
            transacaoModel.addRow(new Object[]{
                    t.getId(),
                    t.getTipo(),
                    String.format(LOCALE_BR, "%.2f", t.getValor()),
                    t.getCategoria().getNome(),
                    t.getData().format(BR_FORMAT),
                    t.getDescricao()
            });
        }
    }
}

package org.example.view;

import org.example.controller.TransacoesController;
import org.example.model.Categoria;
import org.example.model.Transacao;
import org.example.model.Usuario;

import javax.swing.*;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransacoesView {
    private final Usuario currentUser;
    private final TransacoesController controller;

    private static final DateTimeFormatter BR_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTable transacaoTable;
    private DefaultTableModel transacaoModel;
    private TableRowSorter<DefaultTableModel> transacaoSorter;

    private JComboBox<String> filtroTipo;
    private JTextField filtroValor;
    private JComboBox<Categoria> filtroCategoria;
    private JTextField filtroDesc;
    private JFormattedTextField filtroDataDe;
    private JFormattedTextField filtroDataAte;

    private JFormattedTextField txtValor;
    private JTextField txtData;
    private JTextField txtDesc;
    private JComboBox<Categoria> cbCategoriaTransacao;

    public TransacoesView(Usuario currentUser, TransacoesController controller) {
        this.currentUser = currentUser;
        this.controller = controller;
    }

    public JPanel getPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filtrosContainer = new JPanel();
        filtrosContainer.setLayout(new BoxLayout(filtrosContainer, BoxLayout.Y_AXIS));

        JPanel filtrosLinha1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtroTipo = new JComboBox<>(new String[]{"Todas", "Receita", "Despesa"});
        filtroValor = new JTextField(8);
        filtroValor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != ',' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        filtroCategoria = new JComboBox<>();
        recarregarFiltroCategoriaCombo();
        filtroDesc = new JTextField(10);

        filtrosLinha1.add(new JLabel("Tipo:"));
        filtrosLinha1.add(filtroTipo);
        filtrosLinha1.add(new JLabel("Valor:"));
        filtrosLinha1.add(filtroValor);
        filtrosLinha1.add(new JLabel("Categoria:"));
        filtrosLinha1.add(filtroCategoria);
        filtrosLinha1.add(new JLabel("Desc:"));
        filtrosLinha1.add(filtroDesc);

        filtrosContainer.add(filtrosLinha1);

        JPanel filtrosLinha2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtroDataDe = criarMaskFormatterDataFiltro(8);
        filtroDataAte = criarMaskFormatterDataFiltro(8);
        JButton btnClearFiltros = new JButton("Limpar Filtros");
        btnClearFiltros.addActionListener(e -> limparFiltros());

        filtrosLinha2.add(new JLabel("Data Início:"));
        filtrosLinha2.add(filtroDataDe);
        filtrosLinha2.add(new JLabel("Data Final:"));
        filtrosLinha2.add(filtroDataAte);
        filtrosLinha2.add(btnClearFiltros);

        filtrosContainer.add(filtrosLinha2);

        p.add(filtrosContainer, BorderLayout.NORTH);

        String[] colunas = {"ID", "Tipo", "Valor", "Categoria", "Data", "Descrição"};
        transacaoModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        transacaoTable = new JTable(transacaoModel);
        transacaoTable.setRowHeight(24);
        transacaoTable.getTableHeader().setFont(transacaoTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        transacaoSorter = new TableRowSorter<>(transacaoModel);
        transacaoTable.setRowSorter(transacaoSorter);

        p.add(new JScrollPane(transacaoTable), BorderLayout.CENTER);

        ActionListener aoFiltrar = e -> aplicarFiltro();
        filtroTipo.addActionListener(aoFiltrar);
        filtroValor.addActionListener(aoFiltrar);
        filtroCategoria.addActionListener(aoFiltrar);
        filtroDesc.addActionListener(aoFiltrar);
        filtroDataDe.addActionListener(aoFiltrar);
        filtroDataAte.addActionListener(aoFiltrar);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JPanel camposLinha = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Receita", "Despesa"});
        txtValor = criarFormatterNumerico(10); // mantém formato numérico para o formulário
        txtData = new JTextField(LocalDate.now().format(BR_FORMAT));
        txtDesc = new JTextField(10);
        cbCategoriaTransacao = new JComboBox<>();
        recarregarTransacaoCategoriaCombo();

        camposLinha.add(new JLabel("Tipo:"));
        camposLinha.add(cbTipo);
        camposLinha.add(new JLabel("Valor:"));
        camposLinha.add(txtValor);
        camposLinha.add(new JLabel("Categoria:"));
        camposLinha.add(cbCategoriaTransacao);
        camposLinha.add(new JLabel("Data:"));
        camposLinha.add(txtData);
        camposLinha.add(new JLabel("Descrição:"));
        camposLinha.add(txtDesc);

        formPanel.add(camposLinha);

        JButton btnAdd = new JButton("Adicionar Transação");
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(btnAdd);

        JPanel botoesLinhaFinal = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnRem = new JButton("Remover Selecionada");
        JButton btnEditar = new JButton("Editar Selecionada");
        botoesLinhaFinal.add(btnRem);
        botoesLinhaFinal.add(btnEditar);
        formPanel.add(botoesLinhaFinal);

        p.add(formPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            String strValor = txtValor.getText().trim();
            String strData = txtData.getText().trim();
            String strDesc = txtDesc.getText().trim();
            Categoria escolhida = (Categoria) cbCategoriaTransacao.getSelectedItem();

            if (strValor.isEmpty() || strData.isEmpty() || strDesc.isEmpty() || escolhida == null) {
                JOptionPane.showMessageDialog(p, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double val;
            try {
                val = Double.parseDouble(strValor.replace(",", "."));
                if (val < 0) throw new NumberFormatException("Negativo");
            } catch (NumberFormatException ex1) {
                JOptionPane.showMessageDialog(p, "Valor inválido! Use apenas números e vírgula/ponto.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate d;
            try {
                d = LocalDate.parse(strData, BR_FORMAT);
            } catch (Exception ex2) {
                JOptionPane.showMessageDialog(p, "Data inválida! Use dd/MM/yyyy.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Transacao nova = new Transacao(val, escolhida, d, strDesc, (String) cbTipo.getSelectedItem(), currentUser);

            try {
                controller.salvar(nova);
            } catch (Exception ex3) {
                JOptionPane.showMessageDialog(p, "Erro ao salvar transação: " + ex3.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            recarregarTabela();
            txtValor.setValue(null);
            txtData.setText(LocalDate.now().format(BR_FORMAT));
            txtDesc.setText("");
        });

        btnRem.addActionListener(e -> {
            int sel = transacaoTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(p, "Selecione uma transação para remover!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelIdx = transacaoTable.convertRowIndexToModel(sel);
            Integer id = (Integer) transacaoModel.getValueAt(modelIdx, 0);

            int opcConfirm = JOptionPane.showConfirmDialog(p, "Deseja mesmo excluir a transação?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (opcConfirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                controller.remover(id);
            } catch (Exception ex7) {
                JOptionPane.showMessageDialog(p, "Erro ao remover transação: " + ex7.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }

            recarregarTabela();
        });

        btnEditar.addActionListener(e -> {
            int sel = transacaoTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(p, "Selecione uma transação para editar!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelIdx = transacaoTable.convertRowIndexToModel(sel);
            Integer id = (Integer) transacaoModel.getValueAt(modelIdx, 0);

            Transacao t = null;
            try {
                for (Transacao tr : controller.listarPorUsuario(currentUser)) {
                    if (tr.getId().equals(id)) {
                        t = tr;
                        break;
                    }
                }
            } catch (Exception ex0) {
                JOptionPane.showMessageDialog(p, "Erro ao carregar transação: " + ex0.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (t == null) {
                JOptionPane.showMessageDialog(p, "Transação não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JPanel painelEdicao = new JPanel(new GridLayout(5, 2, 5, 5));
            JComboBox<String> editTipo = new JComboBox<>(new String[]{"Receita", "Despesa"});
            editTipo.setSelectedItem(t.getTipo());

            JTextField editValor = new JTextField(new DecimalFormat("#,##0.00").format(t.getValor()).replace(".", ","));

            JComboBox<Categoria> editCategoria = new JComboBox<>();
            for (Categoria c : controller.listarCategoriasDoUsuario(currentUser)) {
                editCategoria.addItem(c);
                if (c.getId().equals(t.getCategoria().getId())) {
                    editCategoria.setSelectedItem(c);
                }
            }

            JTextField editData = new JTextField(t.getData().format(BR_FORMAT));
            JTextField editDescField = new JTextField(t.getDescricao());

            painelEdicao.add(new JLabel("Tipo:"));
            painelEdicao.add(editTipo);
            painelEdicao.add(new JLabel("Valor:"));
            painelEdicao.add(editValor);
            painelEdicao.add(new JLabel("Categoria:"));
            painelEdicao.add(editCategoria);
            painelEdicao.add(new JLabel("Data:"));
            painelEdicao.add(editData);
            painelEdicao.add(new JLabel("Descrição:"));
            painelEdicao.add(editDescField);

            int opc = JOptionPane.showConfirmDialog(p, painelEdicao, "Editar Transação", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opc != JOptionPane.OK_OPTION) {
                return;
            }

            String novoTipo = (String) editTipo.getSelectedItem();
            String strNovoValor = editValor.getText().trim();
            String strNovaData = editData.getText().trim();
            String novaDesc = editDescField.getText().trim();
            Categoria novaCat = (Categoria) editCategoria.getSelectedItem();

            if (strNovoValor.isEmpty() || strNovaData.isEmpty() || novaDesc.isEmpty() || novaCat == null) {
                JOptionPane.showMessageDialog(p, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double novoVal;
            try {
                novoVal = Double.parseDouble(strNovoValor.replace(",", "."));
                if (novoVal < 0) throw new NumberFormatException("Negativo");
            } catch (NumberFormatException ex4) {
                JOptionPane.showMessageDialog(p, "Valor inválido! Use apenas números e vírgula/ponto.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate novaData;
            try {
                novaData = LocalDate.parse(strNovaData, BR_FORMAT);
            } catch (Exception ex5) {
                JOptionPane.showMessageDialog(p, "Data inválida! Use dd/MM/yyyy.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            t.setTipo(novoTipo);
            t.setValor(novoVal);
            t.setData(novaData);
            t.setCategoria(novaCat);
            t.setDescricao(novaDesc);

            try {
                controller.atualizar(t);
                JOptionPane.showMessageDialog(p, "Transação editada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex6) {
                JOptionPane.showMessageDialog(p, "Erro ao atualizar transação: " + ex6.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }

            recarregarTabela();
        });

        recarregarTabela();
        return p;
    }

    private JFormattedTextField criarMaskFormatterDataFiltro(int cols) {
        MaskFormatter mf;
        try {
            mf = new MaskFormatter("##/##/####");
            mf.setPlaceholderCharacter('_');
        } catch (Exception e) {
            return new JFormattedTextField();
        }
        var factory = new DefaultFormatterFactory(null, mf, mf);
        JFormattedTextField campo = new JFormattedTextField(factory);
        campo.setColumns(cols);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String texto = campo.getText().trim();
                if (texto.contains("_") || texto.length() < 10) {
                    campo.setValue(null);
                }
            }
        });
        return campo;
    }

    private JFormattedTextField criarFormatterNumerico(int cols) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(false);

        NumberFormatter rawFormatter = new NumberFormatter(nf);
        rawFormatter.setAllowsInvalid(true);
        rawFormatter.setOverwriteMode(false);
        rawFormatter.setCommitsOnValidEdit(false);

        NumberFormatter commitFormatter = new NumberFormatter(nf);
        commitFormatter.setAllowsInvalid(false);
        commitFormatter.setOverwriteMode(false);
        commitFormatter.setCommitsOnValidEdit(true);

        DefaultFormatterFactory factory = new DefaultFormatterFactory(rawFormatter, rawFormatter, commitFormatter);

        JFormattedTextField campo = new JFormattedTextField(factory);
        campo.setColumns(cols);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    campo.commitEdit();
                } catch (Exception ex) {
                    campo.setValue(null);
                }
            }
        });
        return campo;
    }

    private void limparFiltros() {
        filtroTipo.setSelectedIndex(0);
        filtroValor.setText("");
        filtroCategoria.setSelectedIndex(0);
        filtroDesc.setText("");
        filtroDataDe.setValue(null);
        filtroDataAte.setValue(null);
        transacaoSorter.setRowFilter(null);
    }

    private void aplicarFiltro() {
        List<RowFilter<Object, Object>> lista = new ArrayList<>();

        String ft = (String) filtroTipo.getSelectedItem();
        if (ft != null && !ft.equals("Todas")) {
            lista.add(RowFilter.regexFilter("^" + ft + "$", 1));
        }

        String fv = (filtroValor.getText() == null ? "" : filtroValor.getText().trim());
        if (!fv.isEmpty()) {
            String filterDigits = fv.replaceAll("[^0-9]", "");
            lista.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    String cellValue = entry.getStringValue(2);
                    String cellDigits = cellValue.replaceAll("[^0-9]", "");
                    return cellDigits.startsWith(filterDigits);
                }
            });
        }

        Categoria fc = (Categoria) filtroCategoria.getSelectedItem();
        if (fc != null && fc.getUsuario() != null && fc.getUsuario().getId().equals(currentUser.getId())) {
            lista.add(RowFilter.regexFilter("^" + fc.getNome() + "$", 3));
        }

        LocalDate dataDe = null, dataAte = null;
        try {
            String strDe = (filtroDataDe.getText() == null ? "" : filtroDataDe.getText().trim());
            if (!strDe.isEmpty()) {
                dataDe = LocalDate.parse(strDe, BR_FORMAT);
            }
        } catch (Exception ignore) {
        }
        try {
            String strAte = (filtroDataAte.getText() == null ? "" : filtroDataAte.getText().trim());
            if (!strAte.isEmpty()) {
                dataAte = LocalDate.parse(strAte, BR_FORMAT);
            }
        } catch (Exception ignore) {
        }

        final LocalDate finalDe = dataDe, finalAte = dataAte;
        if (finalDe != null || finalAte != null) {
            lista.add(new RowFilter<>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    try {
                        String dataStr = (String) entry.getValue(4);
                        LocalDate d = LocalDate.parse(dataStr, BR_FORMAT);
                        if (finalDe != null && d.isBefore(finalDe)) return false;
                        if (finalAte != null && d.isAfter(finalAte)) return false;
                        return true;
                    } catch (Exception ex) {
                        return false;
                    }
                }
            });
        }

        String fdesc = filtroDesc.getText().trim();
        if (!fdesc.isEmpty()) {
            lista.add(RowFilter.regexFilter("(?i)" + fdesc, 5));
        }

        transacaoSorter.setRowFilter(lista.isEmpty() ? null : RowFilter.andFilter(lista));
    }

    public void recarregarTabela() {
        transacaoModel.setRowCount(0);
        try {
            for (Transacao t : controller.listarPorUsuario(currentUser)) {
                String valorFormatado = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(t.getValor());
                transacaoModel.addRow(new Object[]{t.getId(), t.getTipo(), valorFormatado, t.getCategoria().getNome(), t.getData().format(BR_FORMAT), t.getDescricao()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar transações: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void recarregarFiltroCategoriaCombo() {
        filtroCategoria.removeAllItems();
        filtroCategoria.addItem(new Categoria("Todas", null));
        for (Categoria c : controller.listarCategoriasDoUsuario(currentUser)) {
            filtroCategoria.addItem(c);
        }
    }

    public void recarregarTransacaoCategoriaCombo() {
        cbCategoriaTransacao.removeAllItems();
        for (Categoria c : controller.listarCategoriasDoUsuario(currentUser)) {
            cbCategoriaTransacao.addItem(c);
        }
    }
}

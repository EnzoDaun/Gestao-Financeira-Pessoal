// src/main/java/org/example/view/TransacoesView.java
package org.example.view;

import org.example.controller.TransacaoViewController;
import org.example.model.Categoria;
import org.example.model.Transacao;
import org.example.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * View de Transações:
 * – Filtros: Tipo | Valor (BRL) | Categoria | Período Data (De/Ate) | Descrição
 * – Formulário: adicionar, editar e remover transação.
 * – Usa JFormattedTextField com NumberFormatter para valores em R$.
 * – Após qualquer ação (add/edit/remove), chama recarregarTabela() para atualizar.
 */
public class TransacoesView {
    private final Usuario currentUser;
    private final TransacaoViewController controller;

    private static final DateTimeFormatter BR_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTable transacaoTable;
    private DefaultTableModel transacaoModel;
    private TableRowSorter<DefaultTableModel> transacaoSorter;

    private JComboBox<String> filtroTipo;
    private JFormattedTextField filtroValor;       // uso de moeda BRL
    private JComboBox<Categoria> filtroCategoria;
    private JFormattedTextField filtroDataDe;
    private JFormattedTextField filtroDataAte;
    private JTextField filtroDesc;

    private JFormattedTextField txtValor;          // campo “Valor” no form adicionar
    private JFormattedTextField txtData;           // campo “Data” no form adicionar
    private JTextField txtDesc;                    // campo “Descrição” no form adicionar
    private JComboBox<Categoria> cbCategoriaTransacao;

    public TransacoesView(Usuario currentUser,
                          TransacaoViewController controller) {
        this.currentUser = currentUser;
        this.controller  = controller;
    }

    /**
     * Constrói e retorna o JPanel completo desta View.
     */
    public JPanel getPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        // === Painel de filtros ===
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtroTipo = new JComboBox<>(new String[]{"Todas", "Receita", "Despesa"});

        // Formatação de moeda brasileira (R$ #,##0.00)
        NumberFormat valorFormat = DecimalFormat.getCurrencyInstance(new Locale("pt", "BR"));
        valorFormat.setMinimumFractionDigits(2);
        valorFormat.setMaximumFractionDigits(2);
        NumberFormatter nf = new NumberFormatter(valorFormat);
        nf.setAllowsInvalid(false);
        nf.setOverwriteMode(true);

        filtroValor = new JFormattedTextField(new DefaultFormatterFactory(nf));
        filtroValor.setColumns(8);

        filtroCategoria = new JComboBox<>();
        recarregarFiltroCategoriaCombo();

        try {
            MaskFormatter mf = new MaskFormatter("##/##/####");
            mf.setPlaceholderCharacter('_');
            filtroDataDe  = new JFormattedTextField(new DefaultFormatterFactory(mf));
            filtroDataAte = new JFormattedTextField(new DefaultFormatterFactory(mf));
        } catch (ParseException ex) {
            filtroDataDe  = new JFormattedTextField();
            filtroDataAte = new JFormattedTextField();
        }
        filtroDesc = new JTextField(10);

        filtros.add(new JLabel("Tipo:"));      filtros.add(filtroTipo);
        filtros.add(new JLabel("Valor:"));     filtros.add(filtroValor);
        filtros.add(new JLabel("Categoria:")); filtros.add(filtroCategoria);
        filtros.add(new JLabel("Data De:"));   filtros.add(filtroDataDe);
        filtros.add(new JLabel("Até:"));       filtros.add(filtroDataAte);
        filtros.add(new JLabel("Desc:"));      filtros.add(filtroDesc);

        JButton btnClearFiltros = new JButton("Limpar Filtros");
        btnClearFiltros.addActionListener(e -> limparFiltros());
        filtros.add(btnClearFiltros);

        p.add(filtros, BorderLayout.NORTH);

        // === Tabela de Transações ===
        String[] colunas = {"ID", "Tipo", "Valor", "Categoria", "Data", "Descrição"};
        transacaoModel  = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        transacaoTable  = new JTable(transacaoModel);
        transacaoSorter = new TableRowSorter<>(transacaoModel);
        transacaoTable.setRowSorter(transacaoSorter);
        JScrollPane scroll = new JScrollPane(transacaoTable);
        p.add(scroll, BorderLayout.CENTER);

        // Toda vez que qualquer filtro mudar, chamamos applyFilter():
        ActionListener aoFiltrar = e -> aplicarFiltro();
        filtroTipo.addActionListener(aoFiltrar);
        filtroValor.addActionListener(aoFiltrar);
        filtroCategoria.addActionListener(aoFiltrar);
        filtroDataDe.addActionListener(aoFiltrar);
        filtroDataAte.addActionListener(aoFiltrar);
        filtroDesc.addActionListener(aoFiltrar);

        // === Formulário de adicionar / editar / remover ===
        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Receita", "Despesa"});

        txtValor = new JFormattedTextField(new DefaultFormatterFactory(nf));
        txtValor.setColumns(10);

        txtData = new JFormattedTextField(LocalDate.now().format(BR_FORMAT));
        txtData.setColumns(10);

        txtDesc = new JTextField();

        cbCategoriaTransacao = new JComboBox<>();
        recarregarTransacaoCategoriaCombo();

        form.add(new JLabel("Tipo:"));      form.add(cbTipo);
        form.add(new JLabel("Valor:"));     form.add(txtValor);
        form.add(new JLabel("Categoria:")); form.add(cbCategoriaTransacao);
        form.add(new JLabel("Data:"));      form.add(txtData);
        form.add(new JLabel("Descrição:")); form.add(txtDesc);

        JButton btnAdd    = new JButton("Adicionar Transação");
        JButton btnEditar = new JButton("Editar Selecionada");
        JButton btnRem    = new JButton("Remover Selecionada");
        form.add(btnAdd);    form.add(btnEditar);
        form.add(btnRem);    form.add(new JLabel()); // célula vazia

        p.add(form, BorderLayout.SOUTH);

        // --- AÇÃO “ADICIONAR TRANSACÃO” ---
        btnAdd.addActionListener(e -> {
            if (txtValor.getValue() == null ||
                    txtData.getText().trim().isEmpty() ||
                    txtDesc.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(p,
                        "Preencha todos os campos!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double val = ((Number) txtValor.getValue()).doubleValue();
            LocalDate d;
            try {
                d = LocalDate.parse(txtData.getText(), BR_FORMAT);
            } catch (Exception ex2) {
                JOptionPane.showMessageDialog(p,
                        "Data inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Categoria escolhida = (Categoria) cbCategoriaTransacao.getSelectedItem();
            Transacao nova = new Transacao(
                    val,
                    escolhida,
                    d,
                    txtDesc.getText().trim(),
                    (String) cbTipo.getSelectedItem(),
                    currentUser
            );
            try {
                controller.salvarOuAtualizarTransacao(nova);
            } catch (Exception ex3) {
                JOptionPane.showMessageDialog(p,
                        "Erro ao salvar transação: " + ex3.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
            recarregarTabela();
            txtValor.setValue(null);
            txtData.setText(LocalDate.now().format(BR_FORMAT));
            txtDesc.setText("");
        });

        // --- AÇÃO “EDITAR SELECIONADA” ---
        btnEditar.addActionListener(e -> {
            int sel = transacaoTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(p,
                        "Selecione uma transação para editar!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelIdx = transacaoTable.convertRowIndexToModel(sel);
            Integer id = (Integer) transacaoModel.getValueAt(modelIdx, 0);

            Transacao t = controller.listarTodasTransacoes().stream()
                    .filter(tr -> tr.getId().equals(id))
                    .findFirst().orElse(null);
            if (t == null) {
                JOptionPane.showMessageDialog(p,
                        "Transação não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Painel de edição, já preenchendo todos os campos corretamente:
            JPanel painelEdicao = new JPanel(new GridLayout(5, 2, 5, 5));
            JComboBox<String> editTipo = new JComboBox<>(new String[]{"Receita", "Despesa"});
            editTipo.setSelectedItem(t.getTipo());

            JFormattedTextField editValor = new JFormattedTextField(new DefaultFormatterFactory(nf));
            editValor.setValue(t.getValor());

            JComboBox<Categoria> editCategoria = new JComboBox<>();
            for (Categoria c : controller.listarCategoriasDoUsuario(currentUser)) {
                editCategoria.addItem(c);
                if (c.getId().equals(t.getCategoria().getId())) {
                    editCategoria.setSelectedItem(c);
                }
            }

            JFormattedTextField editData = new JFormattedTextField(t.getData().format(BR_FORMAT));
            JTextField editDesc = new JTextField(t.getDescricao());

            painelEdicao.add(new JLabel("Tipo:"));      painelEdicao.add(editTipo);
            painelEdicao.add(new JLabel("Valor:"));     painelEdicao.add(editValor);
            painelEdicao.add(new JLabel("Categoria:")); painelEdicao.add(editCategoria);
            painelEdicao.add(new JLabel("Data:"));      painelEdicao.add(editData);
            painelEdicao.add(new JLabel("Descrição:")); painelEdicao.add(editDesc);

            int opc = JOptionPane.showConfirmDialog(
                    p,
                    painelEdicao,
                    "Editar Transação",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );
            if (opc != JOptionPane.OK_OPTION) {
                return;
            }

            // Validações de edição:
            String novoTipo = (String) editTipo.getSelectedItem();
            Double novoVal;
            try {
                novoVal = ((Number) editValor.getValue()).doubleValue();
            } catch (Exception ex4) {
                JOptionPane.showMessageDialog(p,
                        "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate novaData;
            try {
                novaData = LocalDate.parse(editData.getText(), BR_FORMAT);
            } catch (Exception ex5) {
                JOptionPane.showMessageDialog(p,
                        "Data inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String novaDesc = editDesc.getText().trim();
            Categoria novaCat = (Categoria) editCategoria.getSelectedItem();

            if (novaDesc.isEmpty()) {
                JOptionPane.showMessageDialog(p,
                        "Preencha a descrição!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Atualiza e persiste:
            t.setTipo(novoTipo);
            t.setValor(novoVal);
            t.setData(novaData);
            t.setCategoria(novaCat);
            t.setDescricao(novaDesc);
            try {
                controller.salvarOuAtualizarTransacao(t);
            } catch (Exception ex6) {
                JOptionPane.showMessageDialog(p,
                        "Erro ao atualizar transação: " + ex6.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
            recarregarTabela();
        });

        // --- AÇÃO “REMOVER SELECIONADA” ---
        btnRem.addActionListener(e -> {
            int sel = transacaoTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(p,
                        "Selecione uma transação para remover!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelIdx = transacaoTable.convertRowIndexToModel(sel);
            Integer id = (Integer) transacaoModel.getValueAt(modelIdx, 0);

            if (JOptionPane.showConfirmDialog(p,
                    "Deseja mesmo excluir a transação?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                controller.removerTransacao(id);
            } catch (Exception ex7) {
                JOptionPane.showMessageDialog(p,
                        "Erro ao remover transação: " + ex7.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
            recarregarTabela();
        });

        // Carrega dados iniciais:
        recarregarTabela();
        return p;
    }

    /** Recarrega o combo de filtro de categorias (incluindo “Todas”). */
    public void recarregarFiltroCategoriaCombo() {
        filtroCategoria.removeAllItems();
        filtroCategoria.addItem(new Categoria("Todas", null));
        for (Categoria c : controller.listarCategoriasDoUsuario(currentUser)) {
            filtroCategoria.addItem(c);
        }
    }

    /** Recarrega o combo de categorias para o formulário de adicionar/editar. */
    public void recarregarTransacaoCategoriaCombo() {
        cbCategoriaTransacao.removeAllItems();
        for (Categoria c : controller.listarCategoriasDoUsuario(currentUser)) {
            cbCategoriaTransacao.addItem(c);
        }
    }

    private void limparFiltros() {
        filtroTipo.setSelectedIndex(0);
        filtroValor.setValue(null);
        filtroCategoria.setSelectedIndex(0);
        filtroDataDe.setText("");
        filtroDataAte.setText("");
        filtroDesc.setText("");
        transacaoSorter.setRowFilter(null);
    }

    /**
     * Aplica todos os filtros na tabela:
     * 1) Tipo (coluna 1);
     * 2) Valor (coluna 2);
     * 3) Categoria (coluna 3);
     * 4) Período de Data [De, Até] (coluna 4);
     * 5) Descrição (coluna 5).
     */
    private void aplicarFiltro() {
        List<RowFilter<Object, Object>> lista = new ArrayList<>();

        // 1) Filtro por Tipo
        String ft = (String) filtroTipo.getSelectedItem();
        if (ft != null && !ft.equals("Todas")) {
            lista.add(RowFilter.regexFilter("^" + ft + "$", 1));
        }

        // 2) Filtro por Valor
        Object valObj = filtroValor.getValue();
        if (valObj instanceof Number) {
            String moeda = DecimalFormat.getCurrencyInstance(new Locale("pt", "BR"))
                    .format(((Number) valObj).doubleValue());
            // remove “R$ ” e escapa caracteres para regex
            moeda = moeda.replace("R$", "").replace(".", "\\.").replace(",", "\\,").trim();
            lista.add(RowFilter.regexFilter(moeda, 2));
        }

        // 3) Filtro por Categoria
        Categoria fc = (Categoria) filtroCategoria.getSelectedItem();
        if (fc != null && fc.getUsuario() != null) {
            lista.add(RowFilter.regexFilter("^" + fc.getNome() + "$", 3));
        }

        // 4) Filtro por Data no período [dataDe, dataAte]
        LocalDate tempDe = null, tempAte = null;
        try {
            String dDe = filtroDataDe.getText();
            if (dDe != null && !dDe.contains("_")) {
                tempDe = LocalDate.parse(dDe, BR_FORMAT);
            }
        } catch (Exception ignore) {
        }
        try {
            String dAte = filtroDataAte.getText();
            if (dAte != null && !dAte.contains("_")) {
                tempAte = LocalDate.parse(dAte, BR_FORMAT);
            }
        } catch (Exception ignore) {
        }

        final LocalDate dataDeFinal = tempDe;
        final LocalDate dataAteFinal = tempAte;

        if (dataDeFinal != null || dataAteFinal != null) {
            lista.add(new RowFilter<>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    try {
                        String dataStr = (String) entry.getValue(4); // coluna “Data”
                        LocalDate d = LocalDate.parse(dataStr, BR_FORMAT);
                        if (dataDeFinal != null && d.isBefore(dataDeFinal)) return false;
                        if (dataAteFinal != null && d.isAfter(dataAteFinal)) return false;
                        return true;
                    } catch (Exception ex) {
                        return false;
                    }
                }
            });
        }

        // 5) Filtro por Descrição (case‐insensitive)
        String fdesc = filtroDesc.getText().trim();
        if (!fdesc.isEmpty()) {
            lista.add(RowFilter.regexFilter("(?i)" + fdesc, 5));
        }

        transacaoSorter.setRowFilter(lista.isEmpty() ? null : RowFilter.andFilter(lista));
    }

    /**
     * Recarrega a tabela de transações puxando todas do banco
     * e exibindo apenas as que pertencem ao usuário logado.
     */
    public void recarregarTabela() {
        transacaoModel.setRowCount(0);
        try {
            for (Transacao t : controller.listarTodasTransacoes()) {
                if (!t.getUsuario().getId().equals(currentUser.getId())) continue;
                transacaoModel.addRow(new Object[]{
                        t.getId(),
                        t.getTipo(),
                        DecimalFormat.getCurrencyInstance(new Locale("pt", "BR"))
                                .format(t.getValor()),
                        t.getCategoria().getNome(),
                        t.getData().format(BR_FORMAT),
                        t.getDescricao()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao carregar transações: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package org.example.view;

import org.example.controller.CategoriaController;
import org.example.model.Categoria;
import org.example.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriasView {
    private final Usuario currentUser;
    private final CategoriaController categoriaCtrl;

    private JTable ctgTable;
    private DefaultTableModel ctgModel;
    private TableRowSorter<DefaultTableModel> ctgSorter;

    private JTextField filtroCtgId;
    private JTextField filtroCtgNome;

    public CategoriasView(Usuario currentUser, CategoriaController categoriaCtrl) {
        this.currentUser = currentUser;
        this.categoriaCtrl = categoriaCtrl;
    }

    public JPanel getPanel() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtroCtgId = new JTextField(5);
        filtroCtgNome = new JTextField(10);
        filtros.add(new JLabel("ID:"));
        filtros.add(filtroCtgId);
        filtros.add(new JLabel("Nome:"));
        filtros.add(filtroCtgNome);

        JButton btnClear = new JButton("Limpar Filtros");
        btnClear.addActionListener(e -> {
            filtroCtgId.setText("");
            filtroCtgNome.setText("");
            ctgSorter.setRowFilter(null);
        });
        filtros.add(btnClear);

        p.add(filtros, BorderLayout.NORTH);

        ctgModel = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0);
        ctgTable = new JTable(ctgModel);
        ctgSorter = new TableRowSorter<>(ctgModel);
        ctgTable.setRowSorter(ctgSorter);
        p.add(new JScrollPane(ctgTable), BorderLayout.CENTER);

        ActionListener aoFiltrar = e -> aplicarFiltro();
        filtroCtgId.addActionListener(aoFiltrar);
        filtroCtgNome.addActionListener(aoFiltrar);

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField txtCat = new JTextField();
        JButton btnAdd = new JButton("Adicionar");
        JButton btnRem = new JButton("Remover Selecionada");
        form.add(new JLabel("Nome da Categoria:"));
        form.add(txtCat);
        form.add(btnAdd);
        form.add(btnRem);
        p.add(form, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            String nome = txtCat.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(p,
                        "Preencha o nome da categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Categoria nova = new Categoria(nome, currentUser);
            categoriaCtrl.salvar(nova);
            txtCat.setText("");
            recarregarTabela();
        });

        btnRem.addActionListener(e -> {
            int sel = ctgTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(p,
                        "Selecione uma categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelIdx = ctgTable.convertRowIndexToModel(sel);
            Integer id = (Integer) ctgModel.getValueAt(modelIdx, 0);

            if (JOptionPane.showConfirmDialog(p,
                    "Deseja mesmo excluir a categoria?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                categoriaCtrl.remover(id, currentUser);
                recarregarTabela();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(p,
                        ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        });

        recarregarTabela();
        return p;
    }

    private void aplicarFiltro() {
        List<RowFilter<Object, Object>> fl = new ArrayList<>();
        String idTxt = filtroCtgId.getText().trim();
        if (!idTxt.isEmpty()) {
            try {
                fl.add(RowFilter.numberFilter(
                        RowFilter.ComparisonType.EQUAL,
                        Integer.parseInt(idTxt), 0
                ));
            } catch (Exception ignore) {
            }
        }
        String nomeTxt = filtroCtgNome.getText().trim();
        if (!nomeTxt.isEmpty()) {
            fl.add(RowFilter.regexFilter("(?i)" + nomeTxt, 1));
        }
        ctgSorter.setRowFilter(fl.isEmpty() ? null : RowFilter.andFilter(fl));
    }

    private void recarregarTabela() {
        ctgModel.setRowCount(0);
        for (Categoria c : categoriaCtrl.listarPorUsuario(currentUser)) {
            ctgModel.addRow(new Object[]{c.getId(), c.getNome()});
        }
    }
}

package org.example.view;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.controller.CategoriasController;
import org.example.model.Categoria;
import org.example.model.Usuario;

import javax.swing.*;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriasView {
    private final Usuario currentUser;
    private final CategoriasController controller;
    private final TransacoesView transacoesView;

    private JTable ctgTable;
    private DefaultTableModel ctgModel;
    private TableRowSorter<DefaultTableModel> ctgSorter;
    private JTextField filtroCtgId;
    private JTextField filtroCtgNome;

    public CategoriasView(Usuario user, CategoriasController catCtrl, TransacoesView transacoesVw) {
        this.currentUser = user;
        this.controller = catCtrl;
        this.transacoesView = transacoesVw;
    }

    public JPanel getPanel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtroCtgId = new JTextField(5);
        filtroCtgNome = new JTextField(10);

        filtros.add(new JLabel("ID:"));
        filtros.add(filtroCtgId);
        filtros.add(new JLabel("Nome:"));
        filtros.add(filtroCtgNome);

        JButton btnClearCtg = new JButton("Limpar Filtros");
        btnClearCtg.addActionListener(e -> {
            filtroCtgId.setText("");
            filtroCtgNome.setText("");
            ctgSorter.setRowFilter(null);
        });
        filtros.add(btnClearCtg);

        p.add(filtros, BorderLayout.NORTH);

        ctgModel = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        ctgTable = new JTable(ctgModel);
        ctgTable.setRowHeight(24);
        ctgSorter = new TableRowSorter<>(ctgModel);
        ctgTable.setRowSorter(ctgSorter);
        JScrollPane scroll = new JScrollPane(ctgTable);
        p.add(scroll, BorderLayout.CENTER);

        ActionListener filCtg = e -> {
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            String idTxt = filtroCtgId.getText().trim();
            if (!idTxt.isEmpty()) {
                try {
                    filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, Integer.parseInt(idTxt), 0));
                } catch (NumberFormatException ignored) {
                }
            }
            String nomeTxt = filtroCtgNome.getText().trim();
            if (!nomeTxt.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + nomeTxt, 1));
            }
            ctgSorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
        };
        filtroCtgId.addActionListener(filCtg);
        filtroCtgNome.addActionListener(filCtg);

        JPanel form = new JPanel(new GridLayout(2, 3, 5, 5));
        JTextField txtCat = new JTextField();
        JButton btnAddCtg = new JButton("Adicionar");
        JButton btnEditarCtg = new JButton("Editar Selecionada");
        JButton btnRemCtg = new JButton("Remover Selecionada");

        form.add(new JLabel("Nome da Categoria:"));
        form.add(txtCat);
        form.add(btnAddCtg);
        form.add(btnEditarCtg);
        form.add(btnRemCtg);
        form.add(new JLabel());

        p.add(form, BorderLayout.SOUTH);

        btnAddCtg.addActionListener(e -> {
            String nome = txtCat.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(p, "Preencha o nome da categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Categoria nova = new Categoria(nome, currentUser);
            try {
                controller.salvar(nova);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Erro ao adicionar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            txtCat.setText("");
            loadCategorias();

            if (transacoesView != null) {
                transacoesView.recarregarFiltroCategoriaCombo();
                transacoesView.recarregarTransacaoCategoriaCombo();
            }
        });

        btnEditarCtg.addActionListener(e -> {
            int sel = ctgTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(p, "Selecione uma categoria para editar!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelIdx = ctgTable.convertRowIndexToModel(sel);
            Integer id = (Integer) ctgModel.getValueAt(modelIdx, 0);

            Categoria cEntidade;
            try {
                cEntidade = controller.findById(id);
            } catch (Exception ex0) {
                JOptionPane.showMessageDialog(p, "Erro ao buscar categoria: " + ex0.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cEntidade == null || !cEntidade.getUsuario().getId().equals(currentUser.getId())) {
                JOptionPane.showMessageDialog(p, "Categoria não encontrada ou não pertence a este usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String nomeAtual = cEntidade.getNome();
            JTextField editNomeField = new JTextField(nomeAtual);
            Object[] msg = {"Novo nome da categoria:", editNomeField};

            int opc = JOptionPane.showConfirmDialog(p, msg, "Editar Categoria", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opc != JOptionPane.OK_OPTION) {
                return;
            }
            String novoNome = editNomeField.getText().trim();
            if (novoNome.isEmpty()) {
                JOptionPane.showMessageDialog(p, "Nome não pode ficar vazio!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            cEntidade.setNome(novoNome);
            try {
                controller.editar(cEntidade);
                loadCategorias();
                if (transacoesView != null) {
                    transacoesView.recarregarFiltroCategoriaCombo();
                    transacoesView.recarregarTransacaoCategoriaCombo();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Erro ao editar categoria: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRemCtg.addActionListener(e -> {
            int sel = ctgTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(p, "Selecione uma categoria para remover!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelIdx = ctgTable.convertRowIndexToModel(sel);
            Integer id = (Integer) ctgModel.getValueAt(modelIdx, 0);

            int opc = JOptionPane.showConfirmDialog(p, "Deseja mesmo excluir a categoria?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (opc != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                controller.remover(id, currentUser);
                loadCategorias();
                if (transacoesView != null) {
                    transacoesView.recarregarFiltroCategoriaCombo();
                    transacoesView.recarregarTransacaoCategoriaCombo();
                }
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(p, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Erro ao remover: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadCategorias();
        return p;
    }

    private void loadCategorias() {
        ctgModel.setRowCount(0);
        List<Categoria> todas;
        try {
            todas = controller.listarPorUsuario(currentUser);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar categorias: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (Categoria c : todas) {
            ctgModel.addRow(new Object[]{c.getId(), c.getNome()});
        }
    }
}

// src/main/java/org/example/view/CategoriasView.java
package org.example.view;

import org.example.controller.CategoriaController;
import org.example.model.Categoria;
import org.example.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * View de Categorias:
 * – Exibe lista de categorias do usuário atual.
 * – Permite adicionar, editar e remover categorias.
 * – Notifica TransacoesView (se injetada) para recarregar combo de categorias imediatamente.
 */
public class CategoriasView {
    private final Usuario currentUser;
    private final CategoriaController controller;
    private final TransacoesView transacoesView; // Pode ser null se não houver TransacoesView

    private JTable ctgTable;
    private DefaultTableModel ctgModel;
    private TableRowSorter<DefaultTableModel> ctgSorter;
    private JTextField filtroCtgId;
    private JTextField filtroCtgNome;

    public CategoriasView(Usuario user,
                          CategoriaController catCtrl,
                          TransacoesView transacoesVw) {
        this.currentUser    = user;
        this.controller     = catCtrl;
        this.transacoesView = transacoesVw;
    }

    /**
     * Constrói e retorna o painel completo de Categorias.
     */
    public JPanel getPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        // --- Filtros na parte superior ---
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filtroCtgId   = new JTextField(5);
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

        // --- Tabela de Categorias no centro ---
        ctgModel = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ctgTable = new JTable(ctgModel);
        ctgSorter = new TableRowSorter<>(ctgModel);
        ctgTable.setRowSorter(ctgSorter);
        JScrollPane scroll = new JScrollPane(ctgTable);
        p.add(scroll, BorderLayout.CENTER);

        // Ação de filtro (ID e Nome) sobre a tabela
        ActionListener filCtg = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<RowFilter<Object, Object>> filters = new ArrayList<>();
                String idTxt = filtroCtgId.getText().trim();
                if (!idTxt.isEmpty()) {
                    try {
                        filters.add(RowFilter.numberFilter(
                                RowFilter.ComparisonType.EQUAL,
                                Integer.parseInt(idTxt), 0));
                    } catch (NumberFormatException ignored) {
                    }
                }
                String nomeTxt = filtroCtgNome.getText().trim();
                if (!nomeTxt.isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + nomeTxt, 1));
                }
                ctgSorter.setRowFilter(
                        filters.isEmpty() ? null : RowFilter.andFilter(filters)
                );
            }
        };
        filtroCtgId.addActionListener(filCtg);
        filtroCtgNome.addActionListener(filCtg);

        // --- Formulário + botões na parte inferior ---
        JPanel form = new JPanel(new GridLayout(2, 3, 5, 5));
        JTextField txtCat = new JTextField();
        JButton btnAddCtg    = new JButton("Adicionar");
        JButton btnEditarCtg = new JButton("Editar Selecionada");
        JButton btnRemCtg    = new JButton("Remover Selecionada");

        form.add(new JLabel("Nome da Categoria:"));
        form.add(txtCat);
        form.add(btnAddCtg);
        form.add(btnEditarCtg);
        form.add(btnRemCtg);
        form.add(new JLabel()); // célula vazia para alinhar grid

        p.add(form, BorderLayout.SOUTH);

        // --- AÇÃO “Adicionar Categoria” ---
        btnAddCtg.addActionListener(e -> {
            String nome = txtCat.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(p,
                        "Preencha o nome da categoria!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Categoria nova = new Categoria(nome, currentUser);
            controller.salvar(nova);
            txtCat.setText("");
            loadCategorias();

            // Notifica TransacoesView para recarregar combos, se existir
            if (transacoesView != null) {
                transacoesView.recarregarFiltroCategoriaCombo();
                transacoesView.recarregarTransacaoCategoriaCombo();
            }
        });

        // --- AÇÃO “Editar Selecionada” ---
        btnEditarCtg.addActionListener(e -> {
            int sel = ctgTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(p,
                        "Selecione uma categoria para editar!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelIdx = ctgTable.convertRowIndexToModel(sel);
            Integer id = (Integer) ctgModel.getValueAt(modelIdx, 0);

            // Carrega a entidade existente do DAO
            Categoria cEntidade = controller.findById(id);
            if (cEntidade == null || !cEntidade.getUsuario().getId().equals(currentUser.getId())) {
                JOptionPane.showMessageDialog(p,
                        "Categoria não encontrada ou não pertence a este usuário!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String nomeAtual = cEntidade.getNome();

            JTextField editNomeField = new JTextField(nomeAtual);
            Object[] msg = {
                    "Novo nome da categoria:", editNomeField
            };
            int opc = JOptionPane.showConfirmDialog(
                    p,
                    msg,
                    "Editar Categoria",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );
            if (opc != JOptionPane.OK_OPTION) {
                return;
            }
            String novoNome = editNomeField.getText().trim();
            if (novoNome.isEmpty()) {
                JOptionPane.showMessageDialog(p,
                        "Nome não pode ficar vazio!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Apenas altera o nome na entidade já carregada
            cEntidade.setNome(novoNome);
            try {
                controller.editarCategoria(cEntidade);
                loadCategorias();
                if (transacoesView != null) {
                    transacoesView.recarregarFiltroCategoriaCombo();
                    transacoesView.recarregarTransacaoCategoriaCombo();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p,
                        "Erro ao editar categoria: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- AÇÃO “Remover Selecionada” ---
        btnRemCtg.addActionListener(e -> {
            int sel = ctgTable.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(p,
                        "Selecione uma categoria para remover!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
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
                controller.remover(id, currentUser);
                loadCategorias();
                if (transacoesView != null) {
                    transacoesView.recarregarFiltroCategoriaCombo();
                    transacoesView.recarregarTransacaoCategoriaCombo();
                }
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(p,
                        ex.getMessage(),
                        "Atenção",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Carrega categorias na inicialização
        loadCategorias();
        return p;
    }

    /**
     * Carrega no DefaultTableModel apenas as categorias que pertencem ao usuário atual.
     */
    private void loadCategorias() {
        ctgModel.setRowCount(0);
        List<Categoria> todas = controller.listarTodos();
        for (Categoria c : todas) {
            if (c.getUsuario().getId().equals(currentUser.getId())) {
                ctgModel.addRow(new Object[]{c.getId(), c.getNome()});
            }
        }
    }
}

// src/main/java/org/example/view/ResumoView.java
package org.example.view;

import org.example.controller.TransacaoController;
import org.example.model.Transacao;
import org.example.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Aba “Resumo” que mostra totais de receitas, despesas e saldo, com um botão para atualizar.
 */
public class ResumoView {
    private final Usuario currentUser;
    private final TransacaoController transacaoCtrl;

    private JLabel lblReceitas, lblDespesas, lblTotal;

    public ResumoView(Usuario currentUser, TransacaoController transacaoCtrl) {
        this.currentUser = currentUser;
        this.transacaoCtrl = transacaoCtrl;
    }

    public JPanel getPanel() {
        JPanel p = new JPanel(new BorderLayout());
        lblReceitas = new JLabel("Total Receitas: R$ 0.00");
        lblDespesas = new JLabel("Total Despesas: R$ 0.00");
        lblTotal    = new JLabel("Saldo Total:   R$ 0.00");

        Font fnt = lblTotal.getFont().deriveFont(Font.BOLD, lblTotal.getFont().getSize() + 2f);
        lblReceitas.setFont(fnt);
        lblDespesas.setFont(fnt);
        lblTotal.setFont(fnt);

        JPanel info = new JPanel(new GridLayout(3, 1, 5, 5));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        info.add(lblReceitas);
        info.add(lblDespesas);
        info.add(lblTotal);
        p.add(info, BorderLayout.CENTER);

        JButton btnAtual = new JButton("Atualizar Resumo");
        btnAtual.addActionListener(e -> atualizarResumo());
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnl.add(btnAtual);
        p.add(pnl, BorderLayout.SOUTH);

        // Preenche imediatamente
        atualizarResumo();
        return p;
    }

    private void atualizarResumo() {
        double r = 0, d = 0;
        try {
            for (Transacao t : transacaoCtrl.listarTodos()) {
                if (!t.getUsuario().getId().equals(currentUser.getId())) {
                    continue;
                }
                if ("Receita".equals(t.getTipo())) {
                    r += t.getValor();
                } else {
                    d += t.getValor();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao calcular resumo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        lblReceitas.setText("Total Receitas: R$ " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", r));
        lblDespesas.setText("Total Despesas: R$ " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", d));
        lblTotal.setText("Saldo Total:   R$ " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", r - d));
    }
}

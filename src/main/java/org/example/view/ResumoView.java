package org.example.view;

import org.example.controller.TransacaoController;
import org.example.model.Transacao;
import org.example.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;


public class ResumoView {
    private final Usuario currentUser;
    private final TransacaoController transacaoCtrl;

    private static final Locale LOCALE_BR = new Locale("pt", "BR");

    private JLabel lblReceitas;
    private JLabel lblDespesas;
    private JLabel lblSaldo;

    public ResumoView(Usuario currentUser, TransacaoController transacaoCtrl) {
        this.currentUser = currentUser;
        this.transacaoCtrl = transacaoCtrl;
    }

    public JPanel getPanel() {
        JPanel p = new JPanel(new BorderLayout());

        lblReceitas = new JLabel("Total Receitas: R$ 0,00");
        lblDespesas = new JLabel("Total Despesas: R$ 0,00");
        lblSaldo = new JLabel("Saldo Total: R$ 0,00");

        Font fnt = lblSaldo.getFont().deriveFont(Font.BOLD, lblSaldo.getFont().getSize() + 2f);
        lblReceitas.setFont(fnt);
        lblDespesas.setFont(fnt);
        lblSaldo.setFont(fnt);

        JPanel info = new JPanel(new GridLayout(3, 1, 5, 5));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        info.add(lblReceitas);
        info.add(lblDespesas);
        info.add(lblSaldo);
        p.add(info, BorderLayout.CENTER);

        JButton btnAtual = new JButton("Atualizar Resumo");
        btnAtual.addActionListener(e -> atualizar());
        JPanel sul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sul.add(btnAtual);
        p.add(sul, BorderLayout.SOUTH);

        atualizar();
        return p;
    }

    private void atualizar() {
        double somaR = 0, somaD = 0;
        for (Transacao t : transacaoCtrl.listarTodos()) {
            if (!t.getUsuario().getId().equals(currentUser.getId())) continue;
            if ("Receita".equalsIgnoreCase(t.getTipo())) {
                somaR += t.getValor();
            } else {
                somaD += t.getValor();
            }
        }
        lblReceitas.setText("Total Receitas: R$ " + String.format(LOCALE_BR, "%.2f", somaR));
        lblDespesas.setText("Total Despesas: R$ " + String.format(LOCALE_BR, "%.2f", somaD));
        lblSaldo.setText("Saldo Total: R$ " + String.format(LOCALE_BR, "%.2f", somaR - somaD));
    }
}

// src/main/java/org/example/view/ResumoView.java
package org.example.view;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.controller.TransacoesController;
import org.example.model.Transacao;
import org.example.model.Usuario;
import org.example.util.ExportUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ResumoView {
    private final Usuario currentUser;
    private final TransacoesController controller;

    private JLabel lblReceitas;
    private JLabel lblDespesas;
    private JLabel lblSaldo;

    public ResumoView(Usuario user, TransacoesController controller) {
        this.currentUser = user;
        this.controller = controller;
    }

    public JPanel getPanel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblReceitas = new JLabel("Total Receitas: R$ 0,00");
        lblDespesas = new JLabel("Total Despesas: R$ 0,00");
        lblSaldo = new JLabel("Saldo Final: R$ 0,00");

        Font fnt = lblSaldo.getFont().deriveFont(Font.BOLD, 16f);
        lblReceitas.setFont(fnt);
        lblDespesas.setFont(fnt);
        lblSaldo.setFont(fnt);

        lblReceitas.setForeground(new Color(0, 100, 0));
        lblDespesas.setForeground(new Color(180, 0, 0));
        lblSaldo.setForeground(new Color(0, 0, 150));

        JPanel info = new JPanel(new GridLayout(3, 1, 5, 5));
        info.add(lblReceitas);
        info.add(lblDespesas);
        info.add(lblSaldo);
        p.add(info, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnExcel = new JButton("Gerar Planilha (.xlsx)");
        JButton btnPDF = new JButton("Gerar PDF");
        botoes.add(btnExcel);
        botoes.add(btnPDF);
        p.add(botoes, BorderLayout.SOUTH);

        JButton btnAtualizar = new JButton("Atualizar Resumo");
        JPanel pnlTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlTopo.add(btnAtualizar);
        p.add(pnlTopo, BorderLayout.NORTH);

        btnAtualizar.addActionListener(e -> atualizarResumo());

        btnExcel.addActionListener(e -> {
            List<Transacao> lista;
            try {
                lista = controller.listarPorUsuario(currentUser);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Erro ao obter transações: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salvar como XLSX");
            chooser.setSelectedFile(new File("resumo_transacoes.xlsx"));
            int userChoice = chooser.showSaveDialog(p);
            if (userChoice == JFileChooser.APPROVE_OPTION) {
                try {
                    ExportUtil.gerarPlanilhaExcel(lista, chooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(p, "Planilha gerada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(p, "Erro ao gerar Excel: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnPDF.addActionListener(e -> {
            List<Transacao> lista;
            try {
                lista = controller.listarPorUsuario(currentUser);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Erro ao obter transações: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Salvar como PDF");
            chooser.setSelectedFile(new File("resumo_transacoes.pdf"));
            int userChoice = chooser.showSaveDialog(p);
            if (userChoice == JFileChooser.APPROVE_OPTION) {
                try {
                    ExportUtil.gerarPdf(lista, chooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(p, "PDF gerado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(p, "Erro ao gerar PDF: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        atualizarResumo();
        return p;
    }

    public void atualizarResumo() {
        double totalR = 0, totalD = 0;
        try {
            for (Transacao t : controller.listarPorUsuario(currentUser)) {
                if ("Receita".equals(t.getTipo())) totalR += t.getValor();
                else totalD += t.getValor();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao calcular resumo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        lblReceitas.setText(String.format("Total Receitas: R$ %.2f", totalR));
        lblDespesas.setText(String.format("Total Despesas:   R$ %.2f", totalD));
        lblSaldo.setText(String.format("Saldo Final:     R$ %.2f", totalR - totalD));
    }
}

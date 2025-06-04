// src/main/java/org/example/view/MainView.java
package org.example.view;

import org.example.controller.CategoriaController;
import org.example.controller.TransacaoController;
import org.example.controller.TransacaoViewController;
import org.example.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Janela principal com abas: Transações, Resumo e Categorias.
 * – Cada aba conversa com exatamente um Controller.
 * – Passamos a mesma instância de TransacoesView para a aba de Categorias,
 *   de modo que, ao adicionar/remover categoria, ela possa recarregar imediatamente
 *   os combos daquela View de Transações.
 */
public class MainView {
    private final Usuario currentUser;
    private JFrame frame;

    private TransacoesView transacoesView;
    private ResumoView     resumoView;
    private CategoriasView categoriasView;

    public MainView(Usuario u) {
        this.currentUser = u;
    }

    public void show() {
        frame = new JFrame("Gestão Financeira - " + currentUser.getUsuario());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);

        // === Painel superior com usuário e relógio e botão Logout ===
        JPanel top = new JPanel(new BorderLayout());
        JLabel lblUsuarioHora = new JLabel(
                currentUser.getUsuario() + " — " +
                        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        lblUsuarioHora.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        top.add(lblUsuarioHora, BorderLayout.WEST);

        // Atualiza o relógio a cada minuto
        new javax.swing.Timer(60_000, e ->
                lblUsuarioHora.setText(
                        currentUser.getUsuario() + " — " +
                                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                )
        ).start();

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(frame,
                    "Deseja mesmo sair do sistema?",
                    "Confirmar Logout",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                frame.dispose();
                new LoginView().show();
            }
        });
        top.add(btnLogout, BorderLayout.EAST);
        frame.add(top, BorderLayout.NORTH);

        // === Abas principais ===
        JTabbedPane tabs = new JTabbedPane();

        // 1) Aba Transações → usa TransacaoViewController e TransacoesView
        TransacaoViewController txViewController = new TransacaoViewController();
        transacoesView = new TransacoesView(currentUser, txViewController);
        tabs.addTab("Transações", transacoesView.getPanel());

        // 2) Aba Resumo → usa TransacaoController e ResumoView
        resumoView = new ResumoView(currentUser, new TransacaoController());
        tabs.addTab("Resumo", resumoView.getPanel());

        // 3) Aba Categorias → usa CategoriaController e CategoriasView.
        //    Passamos a mesma instância de transacoesView como terceiro argumento.
        categoriasView = new CategoriasView(
                currentUser,
                new CategoriaController(),
                transacoesView
        );
        tabs.addTab("Categorias", categoriasView.getPanel());

        // Quando o usuário voltar para a aba “Transações”, recarregamos tudo:
        tabs.addChangeListener(evt -> {
            if (tabs.getSelectedComponent() == transacoesView.getPanel()) {
                transacoesView.recarregarFiltroCategoriaCombo();
                transacoesView.recarregarTransacaoCategoriaCombo();
                transacoesView.recarregarTabela();
            }
        });

        frame.add(tabs, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}

// src/main/java/org/example/view/MainView.java
package org.example.view;

import org.example.controller.CategoriaController;
import org.example.controller.TransacaoController;
import org.example.model.Usuario;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainView {
    private final Usuario currentUser;
    private final CategoriaController categoriaCtrl = new CategoriaController();
    private final TransacaoController transacaoCtrl = new TransacaoController();
    private JFrame frame;

    private static final DateTimeFormatter HR_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public MainView(Usuario currentUser) {
        this.currentUser = currentUser;
    }

    public void show() {
        frame = new JFrame("Gestão Financeira - " + currentUser.getUsuario());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);

        JPanel top = new JPanel(new BorderLayout());
        JLabel lblUsuarioHora = new JLabel(
                currentUser.getUsuario() + " — " + LocalTime.now().format(HR_FORMAT)
        );
        lblUsuarioHora.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        top.add(lblUsuarioHora, BorderLayout.WEST);

        new javax.swing.Timer(60_000, e -> lblUsuarioHora.setText(
                currentUser.getUsuario() + " — " + LocalTime.now().format(HR_FORMAT))
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

        JTabbedPane tabs = new JTabbedPane();

        TransacoesView transacoesView = new TransacoesView(currentUser, categoriaCtrl, transacaoCtrl);
        ResumoView resumoView = new ResumoView(currentUser, transacaoCtrl);
        CategoriasView categoriasView = new CategoriasView(currentUser, categoriaCtrl);

        tabs.addTab("Transações", transacoesView.getPanel());
        tabs.addTab("Resumo", resumoView.getPanel());
        tabs.addTab("Categorias", categoriasView.getPanel());

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int idx = tabs.getSelectedIndex();
                if (idx == 0) {
                    transacoesView.recarregarCategorias();
                }
            }
        });

        frame.add(tabs, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}

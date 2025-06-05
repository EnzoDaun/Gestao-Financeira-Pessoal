// src/main/java/org/example/view/MainView.java
package org.example.view;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.controller.CategoriasController;
import org.example.controller.TransacoesController;
import org.example.controller.ResumoController;
import org.example.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class MainView {
    private final Usuario currentUser;

    private JFrame frame;
    private TransacoesView transacoesView;
    private CategoriasView categoriasView;
    private ResumoView resumoView;

    public MainView(Usuario user) {
        this.currentUser = user;
    }

    public void show() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        frame = new JFrame("Gestão Financeira - " + currentUser.getUsuario());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);

        JPanel top = new JPanel(new BorderLayout());
        JLabel lblUserHour = new JLabel(currentUser.getUsuario() + " — " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        lblUserHour.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        top.add(lblUserHour, BorderLayout.WEST);

        new Timer(60_000, e -> lblUserHour.setText(currentUser.getUsuario() + " — " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")))).start();

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            int opc = JOptionPane.showConfirmDialog(frame, "Deseja mesmo sair do sistema?", "Confirmar Logout", JOptionPane.YES_NO_OPTION);
            if (opc == JOptionPane.YES_OPTION) {
                frame.dispose();
                new LoginView().show();
            }
        });
        top.add(btnLogout, BorderLayout.EAST);
        frame.add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        transacoesView = new TransacoesView(currentUser, new TransacoesController());
        tabs.addTab("Transações", transacoesView.getPanel());

        resumoView = new ResumoView(currentUser, new TransacoesController());
        tabs.addTab("Resumo", resumoView.getPanel());

        categoriasView = new CategoriasView(currentUser, new CategoriasController(), transacoesView);
        tabs.addTab("Categorias", categoriasView.getPanel());

        frame.add(tabs, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}

package org.example.view;

import org.example.controller.UsuarioController;
import org.example.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginView {
    private final UsuarioController usuarioCtrl = new UsuarioController();
    private JFrame frame;

    public void show() {
        frame = new JFrame("Bem-vindo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(
                "<html><div style='text-align:center;'>Bem-vindo ao Sistema<br/>de Gestão Financeira Pessoal</div></html>",
                SwingConstants.CENTER
        );
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(15));

        JPanel pnlLogin = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        pnlLogin.add(new JLabel("Usuário:")); pnlLogin.add(txtUser);
        pnlLogin.add(new JLabel("Senha:")); pnlLogin.add(txtPass);
        pnlLogin.setMaximumSize(pnlLogin.getPreferredSize());
        content.add(pnlLogin);
        content.add(Box.createVerticalStrut(15));

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Registrar");
        pnlBtns.add(btnLogin); pnlBtns.add(btnRegister);
        content.add(pnlBtns);

        frame.setContentPane(content);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        btnLogin.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String s = new String(txtPass.getPassword());
            if (u.isEmpty() || s.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Digite usuário e senha!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Usuario auth = usuarioCtrl.autenticar(u, s);
            if (auth != null) {
                frame.dispose();
                new MainView(auth).show();
            } else {
                JOptionPane.showMessageDialog(frame, "Usuário ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRegister.addActionListener(e -> new RegisterView().show(frame));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().show());
    }
}
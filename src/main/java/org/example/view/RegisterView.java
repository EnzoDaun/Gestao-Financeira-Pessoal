// src/main/java/org/example/view/RegisterView.java
package org.example.view;

import org.example.controller.UsuarioController;
import org.example.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterView {
    private final UsuarioController usuarioCtrl = new UsuarioController();
    private JFrame frame;

    public void show(JFrame parent) {
        frame = new JFrame("Registrar Novo Usuário");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel pnl = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JPasswordField txtPassConf = new JPasswordField(15);
        pnl.add(new JLabel("Usuário:"));         pnl.add(txtUser);
        pnl.add(new JLabel("Senha:"));           pnl.add(txtPass);
        pnl.add(new JLabel("Confirmar Senha:")); pnl.add(txtPassConf);
        content.add(pnl);
        content.add(Box.createVerticalStrut(10));

        JButton btnRegister = new JButton("Registrar");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(btnRegister);

        frame.setContentPane(content);
        frame.pack();
        frame.setLocationRelativeTo(parent);
        frame.setVisible(true);

        btnRegister.addActionListener(e -> {
            String u  = txtUser.getText().trim();
            String s  = new String(txtPass.getPassword());
            String sc = new String(txtPassConf.getPassword());

            if (u.isEmpty() || s.isEmpty() || sc.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!s.equals(sc)) {
                JOptionPane.showMessageDialog(frame, "Senhas não conferem!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Usuario.senhaFormatoValido(s)) {
                JOptionPane.showMessageDialog(frame, "Senha deve ter ao menos 6 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            btnRegister.setEnabled(false);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    usuarioCtrl.registrar(u, s); // já cria a categoria “Geral”
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get(); // captura exceções de doInBackground
                        JOptionPane.showMessageDialog(frame, "Registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                        parent.setEnabled(true);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Erro ao registrar (usuário pode já existir)!", "Erro", JOptionPane.ERROR_MESSAGE);
                        btnRegister.setEnabled(true);
                    }
                }
            }.execute();
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.setEnabled(true);
            }
        });
        parent.setEnabled(false);
    }
}

// src/main/java/org/example/view/RegisterView.java
package org.example.view;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.controller.RegisterController;
import org.example.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterView extends JDialog {
    private final RegisterController registerCtrl = new RegisterController();
    private final JFrame parent;

    public RegisterView(JFrame parent) {
        super(parent, "Registrar Novo Usuário", true);
        this.parent = parent;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel pnl = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JPasswordField txtPassConf = new JPasswordField(15);
        pnl.add(new JLabel("Usuário:"));
        pnl.add(txtUser);
        pnl.add(new JLabel("Senha:"));
        pnl.add(txtPass);
        pnl.add(new JLabel("Confirmar Senha:"));
        pnl.add(txtPassConf);
        content.add(pnl);
        content.add(Box.createVerticalStrut(15));

        JButton btnRegister = new JButton("Registrar");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(btnRegister);

        setContentPane(content);
        pack();
        setLocationRelativeTo(parent);

        parent.setEnabled(false);

        btnRegister.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            String passConf = new String(txtPassConf.getPassword());

            if (user.isEmpty() || pass.isEmpty() || passConf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!pass.equals(passConf)) {
                JOptionPane.showMessageDialog(this, "Senhas não conferem!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Usuario.senhaFormatoValido(pass)) {
                JOptionPane.showMessageDialog(this, "Senha deve ter ao menos 6 caracteres!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            btnRegister.setEnabled(false);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    registerCtrl.registrar(user, pass);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(RegisterView.this, "Registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(RegisterView.this, "Erro ao registrar usuário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        btnRegister.setEnabled(true);
                    }
                }
            };
            worker.execute();
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.setEnabled(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                parent.setEnabled(true);
            }
        });
    }
}

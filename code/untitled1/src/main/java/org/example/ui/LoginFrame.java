package org.example.ui;

import org.example.model.User;
import org.example.service.AuthService;
import org.example.service.BookStorage;

import javax.swing.*;

public class LoginFrame extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;
    private AuthService authService = new AuthService();

    public LoginFrame() {
        initialize();
    }

    private void initialize() {
        setTitle("Авторизация");
        setSize(420, 230);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(null);

        JLabel loginLabel = new JLabel("Логин");
        loginLabel.setBounds(20, 35, 60, 25);

        loginField = new JTextField();
        loginField.setBounds(70, 35, 290, 25);

        JLabel passwordLabel = new JLabel("Пароль");
        passwordLabel.setBounds(20, 65, 60, 25);

        passwordField = new JPasswordField();
        passwordField.setBounds(70, 65, 290, 25);

        // --- КНОПКА РЕГИСТРАЦИИ ---
        JButton registerButton = new JButton("Зарегистрироваться");
        registerButton.setBounds(50, 130, 125, 30);
        registerButton.addActionListener(e -> {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля");
                return;
            }

            boolean success = authService.register(login, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Регистрация успешна");
            } else {
                JOptionPane.showMessageDialog(this, "Пользователь уже существует");
            }
        });

        // --- КНОПКА ВХОДА (ОБНОВЛЕНА) ---
        JButton loginButton = new JButton("Войти в систему");
        loginButton.setBounds(240, 130, 130, 30);
        loginButton.addActionListener(e -> {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля для входа");
                return;
            }

            // Авторизуем пользователя через базу данных/репозиторий
            User authenticatedUser = authService.login(login, password);

            if (authenticatedUser != null) {
                // Инициализируем хранилище книг для этого пользователя
                BookStorage.initUserSession(authenticatedUser.getLogin());

                // Передаем авторизованного пользователя со всеми ролями в MainFrame
                MainFrame frame = new MainFrame(authenticatedUser);
                frame.setVisible(true);

                dispose(); // Закрываем окно логина
            } else {
                JOptionPane.showMessageDialog(this, "Неверный логин или пароль", "Ошибка авторизации", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(registerButton);
        panel.add(loginButton);

        add(panel);
    }
}

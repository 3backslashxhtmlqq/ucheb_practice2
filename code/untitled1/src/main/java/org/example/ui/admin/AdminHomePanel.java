package org.example.ui.admin;

import org.example.model.User;
import org.example.service.AuthService;

import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminHomePanel extends JPanel {

    private JTable usersTable;
    private DefaultTableModel tableModel;
    private AuthService authService = new AuthService();

    public AdminHomePanel() {
        setLayout(null);

        // Верхняя плашка "Главная"
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        titlePanel.setBounds(20, 20, 620, 40);

        JLabel title = new JLabel("Главная");
        title.setFont(new Font("Arial", Font.PLAIN, 18));
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titlePanel.add(title, BorderLayout.WEST);
        add(titlePanel);

        // Приветствие администратора
        JLabel hello = new JLabel("Здравствуйте, администратор!", SwingConstants.CENTER);
        hello.setFont(new Font("Arial", Font.PLAIN, 18));
        hello.setBounds(20, 100, 620, 30);
        add(hello);

        // Подпись к таблице пользователей
        JLabel usersLabel = new JLabel("Последние зарегистрированные пользователи");
        usersLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usersLabel.setBounds(25, 230, 400, 30);
        add(usersLabel);

        // --- СОЗДАНИЕ ТАБЛИЦЫ ПОЛЬЗОВАТЕЛЕЙ ---
        String[] columns = {"Логин", "Роль"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещаем редактировать ячейки прямо в таблице
            }
        };

        usersTable = new JTable(tableModel);
        usersTable.getTableHeader().setReorderingAllowed(false); // Запрещаем двигать колонки

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollPane.setBounds(20, 270, 620, 340); // Четко по размерам из макета
        add(scrollPane);

        refreshUsers();
    }

    public void refreshUsers() {
        tableModel.setRowCount(0);

        try {
            List<User> users = authService.getAllUsers();

            if (users != null) {
                for (int i = users.size() - 1; i >= 0; i--) {
                    User u = users.get(i);
                    tableModel.addRow(new Object[]{
                            u.getLogin(),
                            u.getRole() != null ? u.getRole().getDisplayName() : "Не указана"
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении списка пользователей: " + e.getMessage());
        }
    }
}

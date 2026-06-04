package org.example.ui.admin;

import org.example.model.Role;
import org.example.model.User;
import org.example.service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsersPanel extends JPanel {

    private JTable usersTable;
    private DefaultTableModel tableModel;
    private AuthService authService = new AuthService();
    private List<User> currentUsersList; // Храним список для быстрого доступа по индексу строки

    public UsersPanel() {
        setLayout(null);

        // Верхняя плашка "Список пользователей"
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        titlePanel.setBounds(20, 20, 620, 40);

        JLabel title = new JLabel("Список пользователей");
        title.setFont(new Font("Arial", Font.PLAIN, 18));
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titlePanel.add(title, BorderLayout.WEST);
        add(titlePanel);

        // --- ТАБЛИЦА ПОЛЬЗОВАТЕЛЕЙ ---
        // Добавим колонку Статус, чтобы админ видел, кто забанен
        String[] columns = {"Логин", "Роль", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        usersTable = new JTable(tableModel);
        usersTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollPane.setBounds(20, 80, 620, 360);
        add(scrollPane);

        JButton blockButton = new JButton("Заблокировать");
        blockButton.setBounds(40, 465, 150, 30);
        blockButton.addActionListener(e -> {
            User selectedUser = getSelectedUser();
            if (selectedUser == null) return;

            if (selectedUser.getRole() == Role.ADMIN) {
                JOptionPane.showMessageDialog(this, "Нельзя заблокировать другого администратора!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            selectedUser.setBlocked(true);
            authService.updateUser(selectedUser);
            refreshUsers();
            JOptionPane.showMessageDialog(this, "Пользователь " + selectedUser.getLogin() + " успешно заблокирован.");
        });
        add(blockButton);

        // 2. РАЗБЛОКИРОВАТЬ
        JButton unblockButton = new JButton("Разблокировать");
        unblockButton.setBounds(40, 510, 150, 30);
        unblockButton.addActionListener(e -> {
            User selectedUser = getSelectedUser();
            if (selectedUser == null) return;

            selectedUser.setBlocked(false);
            authService.updateUser(selectedUser);
            refreshUsers();
            JOptionPane.showMessageDialog(this, "Блокировка с пользователя " + selectedUser.getLogin() + " снята.");
        });
        add(unblockButton);

        // 3. НАЗНАЧИТЬ АДМИНИСТРАТОРОМ
        JButton roleButton = new JButton("<html><center>Назначить роль<br>администратора</center></html>");
        roleButton.setBounds(40, 555, 150, 45);
        roleButton.addActionListener(e -> {
            User selectedUser = getSelectedUser();
            if (selectedUser == null) return;

            if (selectedUser.getRole() == Role.ADMIN) {
                JOptionPane.showMessageDialog(this, "Этот пользователь уже является администратором.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите выдать пользователю " + selectedUser.getLogin() + " права АДМИНИСТРАТОРА?",
                    "Смена роли", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                selectedUser.setRole(Role.ADMIN);
                authService.updateUser(selectedUser);
                refreshUsers();
                JOptionPane.showMessageDialog(this, "Пользователю успешно присвоена роль Администратора.");
            }
        });
        add(roleButton);

        // --- ПРАВАЯ КОЛОНКА КНОПОК ---

        // 4. ДОБАВИТЬ ПОЛЬЗОВАТЕЛЯ
        JButton addUserButton = new JButton("<html><center>Добавить<br>пользователя</center></html>");
        addUserButton.setBounds(220, 465, 150, 40);
        addUserButton.addActionListener(e -> {
            // Создаем мини-форму внутри диалога
            JDialog createDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Новый пользователь", true);
            createDialog.setSize(300, 220);
            createDialog.setLocationRelativeTo(this);
            createDialog.setLayout(null);

            JLabel lLabel = new JLabel("Логин:"); lLabel.setBounds(20, 20, 80, 25);
            JTextField lField = new JTextField(); lField.setBounds(100, 20, 160, 25);

            JLabel pLabel = new JLabel("Пароль:"); pLabel.setBounds(20, 60, 80, 25);
            JTextField pField = new JTextField(); pField.setBounds(100, 60, 160, 25);

            JLabel rLabel = new JLabel("Роль:"); rLabel.setBounds(20, 100, 80, 25);
            JComboBox<Role> rCombo = new JComboBox<>(Role.values()); rCombo.setBounds(100, 100, 160, 25);

            JButton saveBtn = new JButton("Создать"); saveBtn.setBounds(90, 145, 120, 30);

            saveBtn.addActionListener(saveEvt -> {
                String login = lField.getText().trim();
                String pass = pField.getText().trim();
                Role role = (Role) rCombo.getSelectedItem();

                if (login.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(createDialog, "Заполните все поля!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = authService.registerNewUser(login, pass, role);
                if (success) {
                    refreshUsers();
                    createDialog.dispose();
                    JOptionPane.showMessageDialog(this, "Пользователь " + login + " успешно добавлен в систему.");
                } else {
                    JOptionPane.showMessageDialog(createDialog, "Пользователь с таким логином уже существует!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });

            createDialog.add(lLabel); createDialog.add(lField);
            createDialog.add(pLabel); createDialog.add(pField);
            createDialog.add(rLabel); createDialog.add(rCombo);
            createDialog.add(saveBtn);
            createDialog.setVisible(true);
        });
        add(addUserButton);

        // 5. УДАЛИТЬ ПОЛЬЗОВАТЕЛЯ
        JButton deleteUserButton = new JButton("<html><center>Удалить<br>пользователя</center></html>");
        deleteUserButton.setBounds(220, 520, 150, 40);
        deleteUserButton.addActionListener(e -> {
            User selectedUser = getSelectedUser();
            if (selectedUser == null) return;

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить учетную запись " + selectedUser.getLogin() + "? Это действие необратимо!",
                    "Удаление пользователя", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                authService.deleteUser(selectedUser.getLogin());
                refreshUsers();
                JOptionPane.showMessageDialog(this, "Пользователь удален из системы.");
            }
        });
        add(deleteUserButton);

        refreshUsers();
    }

    public void refreshUsers() {
        tableModel.setRowCount(0);
        try {
            currentUsersList = authService.getAllUsers();
            if (currentUsersList != null) {
                for (User u : currentUsersList) {
                    tableModel.addRow(new Object[]{
                            u.getLogin(),
                            u.getRole() != null ? u.getRole().getDisplayName() : "Не указана",
                            u.isBlocked() ? "Заблокирован" : "Активен"
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка обновления панели пользователей: " + e.getMessage());
        }
    }

    private User getSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пользователя из таблицы!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return currentUsersList.get(selectedRow);
    }
}

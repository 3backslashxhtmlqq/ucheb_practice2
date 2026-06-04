package org.example.ui;

import org.example.model.Role;
import org.example.model.User;
import org.example.ui.admin.AdminHomePanel;
import org.example.ui.admin.AdminLogsPanel;
import org.example.ui.admin.UserCatalogPanel;
import org.example.ui.admin.UsersPanel;
import org.example.ui.panels.AboutPanel;
import org.example.ui.panels.HomePanel;
import org.example.ui.panels.SearchPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private User user;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    private HomePanel homePanel = new HomePanel();
    private SearchPanel searchPanel = new SearchPanel(homePanel);
    private AdminHomePanel adminHomePanel = new AdminHomePanel();
    private UserCatalogPanel userCatalogPanel = new UserCatalogPanel();
    private UsersPanel usersPanel = new UsersPanel();
    private AdminLogsPanel adminLogsPanel = new AdminLogsPanel();

    public MainFrame(User user) {
        this.user = user;
        initialize();
    }

    private void initialize() {
        setTitle("Каталог книг");
        setSize(675, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createContent();

        createHeader();

        showStartScreen();
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Используем FlowLayout с выравниванием по левому краю
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));

        // Настройка шрифта для кнопок админа (сделать чуть компактнее)
        Font adminBtnFont = new Font("Arial", Font.PLAIN, 12);

        // --- ЕСЛИ ВОШЕЛ АДМИНИСТРАТОР (Адаптировано под ширину 675) ---
        if (user.getRole() == Role.ADMIN) {
            contentPanel.add(adminHomePanel, "ADMIN_HOME");
            contentPanel.add(new UserCatalogPanel(), "USER_CATALOGS");
            contentPanel.add(usersPanel, "ADMIN_USERS");
            contentPanel.add(adminLogsPanel, "ADMIN_LOGS");

            JButton adminHomeButton = new JButton("Главная");

            // Переносим текст длинных кнопок на 2 строки через HTML
            JButton userCatalogsButton = new JButton("<html><center>Каталоги<br>пользователей</center></html>");
            JButton usersButton = new JButton("Пользователи");
            JButton logsButton = new JButton("Логи");
            JButton aboutButton = new JButton("О приложении");

            // Применяем компактный шрифт
            adminHomeButton.setFont(adminBtnFont);
            userCatalogsButton.setFont(adminBtnFont);
            usersButton.setFont(adminBtnFont);
            logsButton.setFont(adminBtnFont);
            aboutButton.setFont(adminBtnFont);

            // Навешиваем события переключения вкладок
            adminHomeButton.addActionListener(e -> {
                adminHomePanel.refreshUsers();
                cardLayout.show(contentPanel, "ADMIN_HOME");
            });
            userCatalogsButton.addActionListener(e -> {
                userCatalogPanel.refreshCatalogs();
                cardLayout.show(contentPanel, "USER_CATALOGS");
            });
            usersButton.addActionListener(e -> {
                usersPanel.refreshUsers();
                cardLayout.show(contentPanel, "ADMIN_USERS");
            });
            logsButton.addActionListener(e -> {
                adminLogsPanel.refreshLogs();
                cardLayout.show(contentPanel, "ADMIN_LOGS");
            });
            aboutButton.addActionListener(e -> cardLayout.show(contentPanel, "ABOUT"));

            menuPanel.add(adminHomeButton);
            menuPanel.add(userCatalogsButton);
            menuPanel.add(usersButton);
            menuPanel.add(logsButton);
            menuPanel.add(aboutButton);

        } else {
            JButton homeButton = new JButton("Главная");
            JButton searchButton = new JButton("Поиск книг");
            JButton aboutButton = new JButton("О приложении");

            homeButton.addActionListener(e -> {
                homePanel.refreshBooks();
                cardLayout.show(contentPanel, "HOME");
            });
            searchButton.addActionListener(e -> cardLayout.show(contentPanel, "SEARCH"));
            aboutButton.addActionListener(e -> cardLayout.show(contentPanel, "ABOUT"));

            menuPanel.add(homeButton);
            menuPanel.add(searchButton);
            menuPanel.add(aboutButton);
        }

        // --- ПРАВЫЙ БЛОК ИНФОРМАЦИИ (Фиксируем, чтобы рамка не сжималась) ---
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));

        // Выравниваем текст внутри правого блока по правому краю (как на макете)
        JLabel loginLabel = new JLabel(user.getLogin(), SwingConstants.RIGHT);
        loginLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        String roleDisplayName = user.getRole() == Role.ADMIN ? "Администратор" : user.getRole().getDisplayName();
        JLabel roleLabel = new JLabel(roleDisplayName, SwingConstants.RIGHT);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        roleLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        userPanel.add(loginLabel);
        userPanel.add(roleLabel);

        headerPanel.add(menuPanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Общие панели
        contentPanel.add(new AboutPanel(), "ABOUT");

        // Панели обычного пользователя
        contentPanel.add(homePanel, "HOME");
        contentPanel.add(searchPanel, "SEARCH");

        // Панели администратора
        if (user.getRole() == Role.ADMIN) {
            contentPanel.add(new AdminHomePanel(), "ADMIN_HOME");
            contentPanel.add(new UserCatalogPanel(), "USER_CATALOGS");

            // Заглушки для новых разделов из макета (создайте для них классы позже, пока используем пустые JPanel)
            contentPanel.add(new JPanel(), "ADMIN_USERS");
            contentPanel.add(new JPanel(), "ADMIN_LOGS");
        }

        add(contentPanel, BorderLayout.CENTER);
    }

    private void showStartScreen() {
        if (user.getRole() == Role.ADMIN) {
            cardLayout.show(contentPanel, "ADMIN_HOME");
        } else {
            cardLayout.show(contentPanel, "HOME");
        }
    }
}

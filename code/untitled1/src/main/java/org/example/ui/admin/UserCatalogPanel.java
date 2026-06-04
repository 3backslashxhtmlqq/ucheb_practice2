package org.example.ui.admin;

import org.example.model.Book;
import org.example.service.BookStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class UserCatalogPanel extends JPanel {

    private JTable catalogTable;
    private DefaultTableModel tableModel;

    public UserCatalogPanel() {
        setLayout(null);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        titlePanel.setBounds(20, 20, 620, 40);

        JLabel title = new JLabel("Каталоги пользователей");
        title.setFont(new Font("Arial", Font.PLAIN, 18));
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titlePanel.add(title, BorderLayout.WEST);
        add(titlePanel);

        // --- СОЗДАНИЕ ТАБЛИЦЫ КАТАЛОГОВ ---
        String[] columns = {"Пользователь (Владелец каталога)", "Имя файла сохраненного каталога"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        catalogTable = new JTable(tableModel);
        catalogTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane catalogScroll = new JScrollPane(catalogTable);
        catalogScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        catalogScroll.setBounds(20, 80, 620, 410);
        add(catalogScroll);

        // --- КНОПКИ ДЕЙСТВИЙ ---

        // 1. РЕДАКТИРОВАТЬ КАТАЛОГ (Посмотреть книги пользователя)
        // 1. РЕДАКТИРОВАТЬ КАТАЛОГ (Управление составом и метаданными)
        JButton editButton = new JButton("Редактировать");
        editButton.setBounds(20, 515, 140, 35);
        editButton.addActionListener(e -> {
            String selectedUser = getSelectedUser();
            if (selectedUser == null) return;

            File userFile = new File(selectedUser + "_catalog.json");
            List<Book> userBooks = BookStorage.loadBooksFromFile(userFile);

            // Создаем модальное диалоговое окно для редактирования книг пользователя
            JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Редактирование каталога: " + selectedUser, true);
            editDialog.setSize(600, 450);
            editDialog.setLocationRelativeTo(this);
            editDialog.setLayout(new BorderLayout());

            // Создаем таблицу для отображения книг внутри этого окна
            String[] bookColumns = {"Название", "Автор", "Издательство"};
            DefaultTableModel bookTableModel = new DefaultTableModel(bookColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            JTable booksTable = new JTable(bookTableModel);
            booksTable.getTableHeader().setReorderingAllowed(false);

            Runnable updateBooksTable = () -> {
                bookTableModel.setRowCount(0);
                for (Book b : userBooks) {
                    bookTableModel.addRow(new Object[]{b.getTitle(), b.getAuthors(), b.getPublisher()});
                }
            };
            updateBooksTable.run(); // Заполняем при старте

            editDialog.add(new JScrollPane(booksTable), BorderLayout.CENTER);

            // Панель управления книгами (кнопки внизу диалога)
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            JButton modifyBookBtn = new JButton("Изменить данные");
            JButton deleteBookBtn = new JButton("Удалить книгу");

            actionPanel.add(modifyBookBtn);
            actionPanel.add(deleteBookBtn);
            editDialog.add(actionPanel, BorderLayout.SOUTH);

            // --- ЛОГИКА КНОПКИ "УДАЛИТЬ КНИГУ" ---
            deleteBookBtn.addActionListener(delEvt -> {
                int selectedBookRow = booksTable.getSelectedRow();
                if (selectedBookRow == -1) {
                    JOptionPane.showMessageDialog(editDialog, "Выберите книгу для удаления!");
                    return;
                }

                Book bookToRemove = userBooks.get(selectedBookRow);
                int confirm = JOptionPane.showConfirmDialog(editDialog,
                        "Удалить книгу '" + bookToRemove.getTitle() + "' из каталога пользователя?",
                        "Удаление книги", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    userBooks.remove(selectedBookRow); // Удаляем из локального списка
                    BookStorage.saveBooksToFile(userFile, userBooks); // Сохраняем изменения в JSON файл
                    updateBooksTable.run(); // Обновляем внутреннюю таблицу
                    JOptionPane.showMessageDialog(editDialog, "Книга успешно удалена.");
                }
            });

            // --- ЛОГИКА КНОПКИ "ИЗМЕНИТЬ ДАННЫЕ" ---
            modifyBookBtn.addActionListener(modEvt -> {
                int selectedBookRow = booksTable.getSelectedRow();
                if (selectedBookRow == -1) {
                    JOptionPane.showMessageDialog(editDialog, "Выберите книгу для редактирования!");
                    return;
                }

                Book bookToEdit = userBooks.get(selectedBookRow);

                // Создаем еще одно маленькое окошко-форму для ввода новых метаданных
                JDialog formDialog = new JDialog(editDialog, "Редактирование метаданных", true);
                formDialog.setSize(400, 300);
                formDialog.setLocationRelativeTo(editDialog);
                formDialog.setLayout(null);

                JLabel tLabel = new JLabel("Название:"); tLabel.setBounds(20, 20, 80, 25);
                JTextField tField = new JTextField(bookToEdit.getTitle()); tField.setBounds(100, 20, 260, 25);

                JLabel aLabel = new JLabel("Автор:"); aLabel.setBounds(20, 60, 80, 25);
                JTextField aField = new JTextField(bookToEdit.getAuthors()); aField.setBounds(100, 60, 260, 25);

                JLabel dLabel = new JLabel("Описание:"); dLabel.setBounds(20, 100, 80, 25);
                JTextArea dArea = new JTextArea(bookToEdit.getDescription());
                dArea.setLineWrap(true); dArea.setWrapStyleWord(true);
                JScrollPane dScroll = new JScrollPane(dArea); dScroll.setBounds(100, 100, 260, 90);

                JButton saveBtn = new JButton("Сохранить"); saveBtn.setBounds(140, 210, 120, 30);

                saveBtn.addActionListener(saveEvt -> {
                    if (tField.getText().trim().isEmpty() || aField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(formDialog, "Название и Автор не могут быть пустыми!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    bookToEdit.setTitle(tField.getText().trim());
                    bookToEdit.setAuthors(aField.getText().trim());
                    bookToEdit.setDescription(dArea.getText().trim());

                    // Записываем обновленный список книг обратно в JSON-файл пользователя
                    BookStorage.saveBooksToFile(userFile, userBooks);

                    updateBooksTable.run(); // Обновляем таблицу книг в основном окне редактирования
                    formDialog.dispose(); // Закрываем форму
                    JOptionPane.showMessageDialog(editDialog, "Данные книги успешно обновлены!");
                });

                formDialog.add(tLabel); formDialog.add(tField);
                formDialog.add(aLabel); formDialog.add(aField);
                formDialog.add(dLabel); formDialog.add(dScroll);
                formDialog.add(saveBtn);
                formDialog.setVisible(true);
            });

            editDialog.setVisible(true);
        });
        add(editButton);

        // 2. УДАЛИТЬ КАТАЛОГ (Стереть файл пользователя)
        JButton deleteButton = new JButton("Удалить");
        deleteButton.setBounds(175, 515, 140, 35);
        deleteButton.addActionListener(e -> {
            String selectedUser = getSelectedUser();
            if (selectedUser == null) return;

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите полностью удалить каталог пользователя " + selectedUser + "?",
                    "Удаление каталога", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                File userFile = new File(selectedUser + "_catalog.json");
                if (userFile.exists() && userFile.delete()) {
                    JOptionPane.showMessageDialog(this, "Каталог успешно удален.");
                    refreshCatalogs(); // Перерисовываем список файлов
                } else {
                    JOptionPane.showMessageDialog(this, "Не удалось удалить файл каталога.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(deleteButton);

        JButton addBookButton = new JButton("<html><center>Добавить книгу в<br>каталог</center></html>");
        addBookButton.setBounds(440, 510, 200, 45);
        addBookButton.addActionListener(e -> {
            String selectedUser = getSelectedUser();
            if (selectedUser == null) return;

            // Окно-выбор источника книги
            String[] options = {"Импортировать из файла (.txt)", "Найти в Google Books API", "Отмена"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Выберите источник для добавления книги пользователю " + selectedUser + ":",
                    "Выбор источника данных",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            File userFile = new File(selectedUser + "_catalog.json");

            // --- ВАРИАНТ 1: ИМПОРТ ИЗ ФАЙЛА ---
            if (choice == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Выберите .txt файл книги для пользователя " + selectedUser);
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt"));

                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(new java.io.FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {

                        String titleLine = reader.readLine();
                        String authorLine = reader.readLine();
                        String descriptionLine = reader.readLine();

                        if (titleLine != null && authorLine != null) {
                            StringBuilder contentBuilder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                contentBuilder.append(line).append("\n");
                            }

                            Book newBook = new Book(titleLine.trim(), authorLine.trim(), "Добавлено Админом",
                                    (descriptionLine != null ? descriptionLine.trim() : "-"), contentBuilder.toString().trim());

                            List<Book> userBooks = BookStorage.loadBooksFromFile(userFile);
                            userBooks.add(newBook);
                            BookStorage.saveBooksToFile(userFile, userBooks);

                            JOptionPane.showMessageDialog(this, "Книга '" + newBook.getTitle() + "' успешно добавлена из файла!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Неверный формат текстового файла.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Ошибка при обработке файла: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }

                // --- ВАРИАНТ 2: ИНТЕГРАЦИЯ С GOOGLE BOOKS API ---
            } else if (choice == JOptionPane.NO_OPTION) {
                // Создаем модальное окно поиска в Google Books
                JDialog searchDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Поиск в Google Books для " + selectedUser, true);
                searchDialog.setSize(600, 450);
                searchDialog.setLocationRelativeTo(this);
                searchDialog.setLayout(new BorderLayout());

                // Верхняя панель поиска
                JPanel topPanel = new JPanel(new BorderLayout(5, 5));
                topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                JTextField searchField = new JTextField();
                JButton startSearchBtn = new JButton("Найти");
                topPanel.add(new JLabel("Название книги: "), BorderLayout.WEST);
                topPanel.add(searchField, BorderLayout.CENTER);
                topPanel.add(startSearchBtn, BorderLayout.EAST);
                searchDialog.add(topPanel, BorderLayout.NORTH);

                // Таблица результатов поиска
                String[] columns1 = {"Название", "Автор", "Издательство"};
                DefaultTableModel searchTableModel = new DefaultTableModel(columns1, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) { return false; }
                };
                JTable resultsTable = new JTable(searchTableModel);
                resultsTable.getTableHeader().setReorderingAllowed(false);
                searchDialog.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

                // Нижняя панель с кнопкой добавления
                JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                JButton addSelectedBtn = new JButton("Добавить выбранную книгу");
                bottomPanel.add(addSelectedBtn);
                searchDialog.add(bottomPanel, BorderLayout.SOUTH);

                // Список для хранения найденных объектов Book в памяти этого окна
                final java.util.List<Book> foundBooks = new java.util.ArrayList<>();
                org.example.service.GoogleBooksService googleBooksService = new org.example.service.GoogleBooksService();

                // Логика кнопки "Найти"
                startSearchBtn.addActionListener(searchEvt -> {
                    String query = searchField.getText().trim();
                    if (query.isEmpty()) {
                        JOptionPane.showMessageDialog(searchDialog, "Введите поисковый запрос!");
                        return;
                    }
                    try {
                        searchTableModel.setRowCount(0);
                        foundBooks.clear();

                        // Вызываем ваш существующий сервис Google Books API
                        java.util.List<Book> apiResults = googleBooksService.searchBooks(query);
                        if (apiResults != null && !apiResults.isEmpty()) {
                            foundBooks.addAll(apiResults);
                            for (Book b : foundBooks) {
                                searchTableModel.addRow(new Object[]{b.getTitle(), b.getAuthors(), b.getPublisher()});
                            }
                        } else {
                            JOptionPane.showMessageDialog(searchDialog, "Книги не найдены.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(searchDialog, "Ошибка поиска: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                });

                // Логика кнопки "Добавить выбранную книгу"
                addSelectedBtn.addActionListener(addEvt -> {
                    int selectedRow = resultsTable.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(searchDialog, "Выберите книгу из результатов поиска!");
                        return;
                    }

                    // Берем книгу из списка результатов
                    Book apiBook = foundBooks.get(selectedRow);

                    // Загружаем каталог пользователя, добавляем книгу и сохраняем
                    List<Book> userBooks = BookStorage.loadBooksFromFile(userFile);
                    userBooks.add(apiBook);
                    BookStorage.saveBooksToFile(userFile, userBooks);

                    JOptionPane.showMessageDialog(searchDialog, "Книга '" + apiBook.getTitle() + "' успешно добавлена в каталог " + selectedUser + "!");
                    searchDialog.dispose(); // Закрываем окно поиска
                });

                searchDialog.setVisible(true);
            }
        });
        add(addBookButton);

        // Первичный запуск загрузки списка
        refreshCatalogs();
    }

    public void refreshCatalogs() {
        tableModel.setRowCount(0);
        java.util.List<String> usersWithCatalogs = BookStorage.getAllUserCatalogs();

        for (String username : usersWithCatalogs) {
            tableModel.addRow(new Object[]{
                    username,
                    username + "_catalog.json"
            });
        }
    }

    /**
     * Проверяет, выбрана ли строка в таблице, и возвращает имя пользователя
     */
    private String getSelectedUser() {
        int selectedRow = catalogTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите пользователя из таблицы!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return tableModel.getValueAt(selectedRow, 0).toString();
    }
}

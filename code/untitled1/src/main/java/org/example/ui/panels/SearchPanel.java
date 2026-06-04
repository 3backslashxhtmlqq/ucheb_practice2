package org.example.ui.panels;

import org.example.model.Book;
import org.example.service.BookStorage;
import org.example.service.GoogleBooksService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchPanel extends JPanel {

    private List<Book> foundBooks = new ArrayList<>();
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private HomePanel homePanel;

    public SearchPanel(HomePanel homePanel) {
        this.homePanel = homePanel;
        setLayout(null);

        java.util.function.Consumer<JButton> styleButton = (button) -> {
            button.setBackground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            button.setFont(new Font("Arial", Font.PLAIN, 14));
        };

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        titlePanel.setBounds(20, 20, 620, 50);

        JLabel title = new JLabel("Поиск книг");
        title.setFont(new Font("Arial", Font.PLAIN, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        titlePanel.add(title, BorderLayout.WEST);
        add(titlePanel);

        JLabel searchLabel = new JLabel("Поиск");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        searchLabel.setBounds(20, 100, 60, 30);
        add(searchLabel);

        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBounds(80, 100, 560, 30);
        add(searchField);

        JButton searchButton = new JButton("Выполнить поиск");
        searchButton.setBounds(100, 150, 150, 30);
        add(searchButton);

        GoogleBooksService googleBooksService = new GoogleBooksService();

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите название книги");
                return;
            }

            try {
                if (sorter != null) sorter.setRowFilter(null);

                model.setRowCount(0);
                foundBooks = googleBooksService.searchBooks(query);

                for (Book book : foundBooks) {
                    model.addRow(new Object[]{
                            book.getTitle(),
                            book.getAuthors(),
                            book.getPublisher()
                    });
                }

                if (foundBooks.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Книги не найдены");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка поиска: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        JButton filterButton = new JButton("Фильтрация");
        filterButton.setBounds(490, 150, 150, 30);
        add(filterButton);

        filterButton.addActionListener(e -> {
            String[] options = {"Все колонки", "Название", "Автор", "Издательство", "Сбросить фильтр"};
            String choice = (String) JOptionPane.showInputDialog(
                    this,
                    "Выберите поле для фильтрации результатов:",
                    "Фильтрация",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == null) return;

            if (choice.equals("Сбросить фильтр")) {
                sorter.setRowFilter(null);
                return;
            }

            String filterText = JOptionPane.showInputDialog(this, "Введите текст для фильтра (" + choice + "):");
            if (filterText == null) return;
            filterText = filterText.trim();

            if (filterText.isEmpty()) {
                sorter.setRowFilter(null);
                return;
            }

            try {
                RowFilter<DefaultTableModel, Object> rf;
                switch (choice) {
                    case "Название" -> rf = RowFilter.regexFilter("(?i)" + filterText, 0);
                    case "Автор" -> rf = RowFilter.regexFilter("(?i)" + filterText, 1);
                    case "Издательство" -> rf = RowFilter.regexFilter("(?i)" + filterText, 2);
                    default -> rf = RowFilter.regexFilter("(?i)" + filterText); // По всем колонкам
                }
                sorter.setRowFilter(rf);
            } catch (java.util.regex.PatternSyntaxException ex) {
                JOptionPane.showMessageDialog(this, "Некорректное регулярное выражение", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton importButton = new JButton("Импорт книги");
        importButton.setBounds(490, 210, 150, 30);
        add(importButton);

        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Выберите файл для импорта книги");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt"));

            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                // ИСПРАВЛЕНО ТУТ: Заменили FileReader на связку с явным указанием UTF_8
                try (BufferedReader reader = new BufferedReader(
                        new java.io.InputStreamReader(
                                new java.io.FileInputStream(selectedFile), java.nio.charset.StandardCharsets.UTF_8))) {

                    String titleLine = reader.readLine();
                    String authorLine = reader.readLine();
                    String descriptionLine = reader.readLine();

                    if (titleLine != null && authorLine != null) {
                        String titleBook = titleLine.trim();
                        String authorBook = authorLine.trim();
                        String publisherBook = "Из файла";
                        String descriptionBook = (descriptionLine != null) ? descriptionLine.trim() : "Без описания";

                        // ЧИТАЕМ ВЕСЬ ОСТАВШИЙСЯ ТЕКСТ КНИГИ
                        StringBuilder contentBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            contentBuilder.append(line).append("\n");
                        }
                        String fullContent = contentBuilder.toString().trim();

                        if (sorter != null) sorter.setRowFilter(null);

                        // Создаем книгу с текстом контента
                        Book importedBook = new Book(titleBook, authorBook, publisherBook, descriptionBook, fullContent);
                        foundBooks.add(importedBook);
                        model.addRow(new Object[]{titleBook, authorBook, publisherBook});

                        JOptionPane.showMessageDialog(this,
                                "Книга успешно импортирована в результаты поиска:\n" + titleBook,
                                "Успех",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Неверный формат файла.", "Ошибка формата", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при чтении файла: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JLabel resultLabel = new JLabel("Результат поиска");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        resultLabel.setBounds(30, 260, 200, 30);
        add(resultLabel);

        String[] columns = {"Название", "Автор", "Издательство"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        scrollPane.setBounds(20, 300, 620, 260);
        add(scrollPane);

        JButton addBookButton = new JButton("Добавить книгу");
        addBookButton.setBounds(255, 580, 150, 30);

        addBookButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите книгу из результатов поиска");
                return;
            }

            int modelRow = table.convertRowIndexToModel(selectedRow);

            Book selectedBook = foundBooks.get(modelRow);
            BookStorage.add(selectedBook);

            homePanel.refreshBooks();

            JOptionPane.showMessageDialog(this, "Книга добавлена в каталог:\n" + selectedBook.getTitle());
        });

        add(addBookButton);
    }
}

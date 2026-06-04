package org.example.ui.panels;

import org.example.debug.Logger;
import org.example.model.Book;
import org.example.service.BookReaderFrame;
import org.example.service.BookStorage;
import org.example.ui.BookReviewViewDialog;

import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HomePanel extends JPanel {

    private JTable catalogTable;
    private DefaultTableModel tableModel;

    public HomePanel() {
        setLayout(null);

        // 1. Верхний заголовок "Главная"
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        titlePanel.setBounds(20, 20, 620, 50);

        JLabel title = new JLabel("Главная");
        title.setFont(new Font("Arial", Font.PLAIN, 24));
        title.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        titlePanel.add(title, BorderLayout.WEST);

        // 2. Приветствие
        JLabel hello = new JLabel("Здравствуй, пользователь! Что будем сегодня читать?", SwingConstants.CENTER);
        hello.setBounds(20, 100, 620, 30);
        hello.setFont(new Font("Arial", Font.PLAIN, 18));

        // 3. Метка "Ваш каталог книг"
        JLabel catalogLabel = new JLabel("Ваш каталог книг");
        catalogLabel.setBounds(20, 210, 200, 30);
        catalogLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // 4. Инициализация таблицы каталога вместо обычной JPanel
        String[] columns = {"Название", "Автор", "Издательство"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещаем редактирование ячеек напрямую через дабл-клик
            }
        };
        catalogTable = new JTable(tableModel);

        // Оборачиваем таблицу в прокрутку и ставим строго на координаты из макета
        JScrollPane scrollPane = new JScrollPane(catalogTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollPane.setBounds(20, 250, 620, 340);

        // 5. Создание и настройка контекстного меню (Всплывает по правому клику)
        initPopupMenu();

        // Первоначальная загрузка книг в таблицу
        refreshBooks();

        // Добавление компонентов на панель
        add(titlePanel);
        add(hello);
        add(catalogLabel);
        add(scrollPane); // Добавляем скролл-панель с таблицей вместо старого booksPanel
    }

    private void initPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem readItem = new JMenuItem("Читать книгу");
        JMenuItem reviewItem = new JMenuItem("Оставить отзыв / впечатление");

        JMenuItem viewReviewItem = new JMenuItem("Посмотреть отзывы и цитаты");

        JMenuItem quoteItem = new JMenuItem("Выделить цитату из книги");
        JMenuItem editItem = new JMenuItem("Редактировать содержимое (описание)");
        JMenuItem deleteItem = new JMenuItem("Удалить книгу");

        popupMenu.add(readItem);
        popupMenu.addSeparator();
        popupMenu.add(reviewItem);
        popupMenu.add(viewReviewItem); // Добавляем на панельку
        popupMenu.add(quoteItem);
        popupMenu.addSeparator();
        popupMenu.add(editItem);
        popupMenu.addSeparator();
        popupMenu.add(deleteItem);

        // Слушатель мыши для вызова PopupMenu
        catalogTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { handlePopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { handlePopup(e); }

            private void handlePopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = catalogTable.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        catalogTable.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        // ДЕЙСТВИЕ: Читать книгу
        readItem.addActionListener(e -> {
            int selectedRow = catalogTable.getSelectedRow();
            if (selectedRow != -1) {
                Book selectedBook = BookStorage.getBooks().get(selectedRow);
                if (selectedBook.getContent() != null && !selectedBook.getContent().isEmpty()) {
                    JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    BookReaderFrame reader = new BookReaderFrame(mainFrame, selectedBook);
                    reader.setVisible(true);
                } else if (selectedBook.getPreviewUrl() != null && !selectedBook.getPreviewUrl().isEmpty()) {
                    openWebPage(selectedBook.getPreviewUrl(), "Открыть официальную страницу предпросмотра книги?");
                } else {
                    try {
                        String searchQuery = selectedBook.getTitle() + " " + selectedBook.getAuthors() + " читать онлайн";
                        String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
                        String fallbackUrl = "https://www.google.com/search?q=" + encodedQuery;
                        openWebPage(fallbackUrl, "Прямая ссылка отсутствует. Попробовать найти текст книги в интернете?");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Не удалось сформировать поисковый запрос.");
                    }
                }
            }
        });

        reviewItem.addActionListener(e -> {
            int selectedRow = catalogTable.getSelectedRow();
            if (selectedRow == -1) return;
            Book selectedBook = BookStorage.getBooks().get(selectedRow);

            JDialog reviewDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Отзыв о книге", true);
            reviewDialog.setSize(600, 380);
            reviewDialog.setLocationRelativeTo(this);
            reviewDialog.setLayout(null);

            // Верхняя плашка
            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            titlePanel.setBounds(10, 10, 565, 35);
            JLabel titleLabel = new JLabel("Оставить отзыв (впечатление) о прочитанной книге: " + selectedBook.getTitle());
            titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            titlePanel.add(titleLabel, BorderLayout.WEST);
            reviewDialog.add(titlePanel);

            // Центральное текстовое поле отзыва
            JTextArea reviewArea = new JTextArea();
            reviewArea.setText(selectedBook.getReviewText());
            reviewArea.setLineWrap(true);
            reviewArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(reviewArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            scrollPane.setBounds(25, 60, 535, 180);
            reviewDialog.add(scrollPane);

            // Лейбл "Текст (отзыв)" внутри пустого поля, если текста нет
            if (selectedBook.getReviewText().isEmpty()) {
                reviewArea.setText("Введите ваш отзыв здесь...");
            }

            // Компоненты рейтинга внизу
            JLabel ratingLabel = new JLabel("Рейтинг");
            ratingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            ratingLabel.setBounds(25, 255, 60, 25);
            reviewDialog.add(ratingLabel);

            Integer[] ratings = {1, 2, 3, 4, 5};
            JComboBox<Integer> ratingCombo = new JComboBox<>(ratings);
            ratingCombo.setSelectedItem(selectedBook.getReviewRating());
            ratingCombo.setBounds(90, 255, 80, 25);
            reviewDialog.add(ratingCombo);

            // Кнопка Отправить
            JButton sendButton = new JButton("Отправить");
            sendButton.setBounds(230, 295, 120, 30);
            sendButton.addActionListener(sendEvt -> {
                String text = reviewArea.getText().trim();
                if (text.isEmpty() || text.equals("Введите ваш отзыв здесь...")) {
                    JOptionPane.showMessageDialog(reviewDialog, "Пожалуйста, напишите текст отзыва перед отправкой!", "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Сохраняем отзыв в модель и отправляем в лог
                selectedBook.setReviewText(text);
                selectedBook.setReviewRating((Integer) ratingCombo.getSelectedItem());

                Logger.info("Пользователь оставил отзыв на книгу '" + selectedBook.getTitle() + "' с оценкой: " + selectedBook.getReviewRating());

                // Сохраняем состояние каталога на диск
                org.example.service.BookStorage.add(selectedBook);

                JOptionPane.showMessageDialog(reviewDialog, "Отзыв успешно сохранен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                reviewDialog.dispose();
            });
            reviewDialog.add(sendButton);

            reviewDialog.setVisible(true);
        });

        // --- ДЕЙСТВИЕ: ВЫДЕЛИТЬ ЦИТАТУ ИЗ КНИГИ ---
        quoteItem.addActionListener(e -> {
            int selectedRow = catalogTable.getSelectedRow();
            if (selectedRow == -1) return;
            Book selectedBook = BookStorage.getBooks().get(selectedRow);

            // Проверяем, открыта ли сейчас читалка BookReaderFrame
            Window[] windows = Window.getWindows();
            String selectedText = "";

            for (Window window : windows) {
                // Проверяем, что окно видимое и является экземпляром JFrame
                if (window instanceof JFrame && window.isVisible()) {
                    JFrame frame = (JFrame) window; // Приводим Window к JFrame

                    // Теперь метод getTitle() доступен без ошибок!
                    if (frame.getTitle() != null && frame.getTitle().contains(selectedBook.getTitle())) {
                        selectedText = findSelectedTextInContainer(frame);
                        break;
                    }
                }
            }

            if (selectedText != null && !selectedText.trim().isEmpty()) {
                selectedBook.addQuote(selectedText.trim());
                Logger.info("В книгу '" + selectedBook.getTitle() + "' добавлена цитата: \"" + selectedText.trim() + "\"");
                JOptionPane.showMessageDialog(this, "Цитата успешно вырезана и сохранена:\n\n\"" + selectedText + "\"", "Цитата сохранена", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Если текст не выделен или книга закрыта, даем возможность ввести цитату вручную
                String manualQuote = JOptionPane.showInputDialog(this,
                        "Выделите текст в окне чтения книги или введите цитату вручную:",
                        "Добавление цитаты для: " + selectedBook.getTitle(),
                        JOptionPane.QUESTION_MESSAGE);

                if (manualQuote != null && !manualQuote.trim().isEmpty()) {
                    selectedBook.addQuote(manualQuote.trim());
                    Logger.info("Вручную добавлена цитата в книгу '" + selectedBook.getTitle() + "'");
                    JOptionPane.showMessageDialog(this, "Цитата сохранена!");
                }
            }
        });

        viewReviewItem.addActionListener(e -> {
            int selectedRow = catalogTable.getSelectedRow();
            if (selectedRow != -1) {
                // Берем выбранную книгу
                Book selectedBook = BookStorage.getBooks().get(selectedRow);

                // Находим главное окно приложения
                Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(this);

                // Создаем и открываем наше новое диалоговое окно
                BookReviewViewDialog viewDialog = new BookReviewViewDialog(mainFrame, selectedBook);
                viewDialog.setVisible(true);
            }
        });

        // ДЕЙСТВИЕ: Редактировать описание
        editItem.addActionListener(e -> {
            int selectedRow = catalogTable.getSelectedRow();
            if (selectedRow != -1) {
                Book selectedBook = BookStorage.getBooks().get(selectedRow);
                JTextArea editArea = new JTextArea(8, 35);
                editArea.setText(selectedBook.getDescription());
                editArea.setLineWrap(true);
                editArea.setWrapStyleWord(true);
                JScrollPane scrollPane = new JScrollPane(editArea);

                int result = JOptionPane.showConfirmDialog(this, scrollPane,
                        "Редактирование описания: " + selectedBook.getTitle(),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    selectedBook.setDescription(editArea.getText().trim());
                    Logger.info("Изменено описание книги: '" + selectedBook.getTitle() + "'");
                    JOptionPane.showMessageDialog(this, "Описание обновлено!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // ДЕЙСТВИЕ: Удалить книгу
        deleteItem.addActionListener(e -> {
            int selectedRow = catalogTable.getSelectedRow();
            if (selectedRow != -1) {
                Book selectedBook = BookStorage.getBooks().get(selectedRow);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Удалить книгу \"" + selectedBook.getTitle() + "\" из каталога?",
                        "Удаление", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    BookStorage.remove(selectedBook);
                    refreshBooks();
                    JOptionPane.showMessageDialog(this, "Книга удалена.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private String findSelectedTextInContainer(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof javax.swing.text.JTextComponent) {
                String selectedText = ((javax.swing.text.JTextComponent) comp).getSelectedText();
                if (selectedText != null) {
                    return selectedText;
                }
            } else if (comp instanceof Container) {
                String selectedText = findSelectedTextInContainer((Container) comp);
                if (selectedText != null) {
                    return selectedText;
                }
            }
        }
        return null;
    }

    private void openWebPage(String url, String confirmMessage) {
        int response = JOptionPane.showConfirmDialog(this,
                confirmMessage,
                "Чтение книги через интернет",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    JOptionPane.showMessageDialog(this, "Ваша операционная система не поддерживает автоматическое открытие браузера.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Не удалось открыть браузер: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshBooks() {
        if (tableModel == null) return;

        // Очищаем старые строки в таблице
        tableModel.setRowCount(0);

        // Заполняем актуальными данными из хранилища
        for (Book book : BookStorage.getBooks()) {
            tableModel.addRow(new Object[]{
                    book.getTitle(),
                    book.getAuthors(),
                    book.getPublisher()
            });
        }
    }
}

package org.example.service;

import org.example.model.Book;

import javax.swing.*;
import java.awt.*;

public class BookReaderFrame extends JDialog {

    public BookReaderFrame(JFrame parent, Book book) {
        // Настраиваем модальное окно (блокирует главное, пока открыто)
        super(parent, book.getTitle(), true);
        setSize(600, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Верхняя панель с названием и автором
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        infoPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(book.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel authorLabel = new JLabel("Автор: " + book.getAuthors());
        authorLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        authorLabel.setForeground(Color.GRAY);

        infoPanel.add(titleLabel);
        infoPanel.add(authorLabel);
        add(infoPanel, BorderLayout.NORTH);

        // Центральная область — сам текст книги
        JTextArea readerArea = new JTextArea();

        // Проверяем наличие контента (если метода getContent нет, временно замените на getDescription)
        readerArea.setText(book.getDescription());
        readerArea.setEditable(false);
        readerArea.setLineWrap(true);
        readerArea.setWrapStyleWord(true);

        readerArea.setFont(new Font("Georgia", Font.PLAIN, 16));
        readerArea.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JScrollPane scrollPane = new JScrollPane(readerArea);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Ниже кнопка "Закрыть книгу"
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton closeButton = new JButton("Закрыть книгу");

        closeButton.setBackground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        closeButton.addActionListener(e -> dispose());

        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}

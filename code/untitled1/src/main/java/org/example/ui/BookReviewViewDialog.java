package org.example.ui;

import org.example.model.Book;

import javax.swing.*;
import java.awt.*;

public class BookReviewViewDialog extends JDialog {

    public BookReviewViewDialog(Frame owner, Book book) {
        super(owner, "Карточка отзывов и цитат", true);
        setSize(550, 500);
        setLocationRelativeTo(owner);
        setLayout(null);

        // --- ВЕРХНЯЯ ПЛАШКА С НАЗВАНИЕМ КНИГИ ---
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        titlePanel.setBounds(15, 15, 505, 40);

        JLabel titleLabel = new JLabel("Отзывы и цитаты: " + book.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel);

        // --- БЛОК ОЦЕНКИ И ОТЗЫВА ---
        JLabel ratingTitleLabel = new JLabel("Оценка пользователя:");
        ratingTitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        ratingTitleLabel.setBounds(20, 70, 150, 20);
        add(ratingTitleLabel);

        int rating = book.getReviewRating();

        JLabel ratingValueLabel = new JLabel(rating + " из 5 ");
        ratingValueLabel.setFont(new Font("Arial", Font.BOLD, 13));
        ratingValueLabel.setForeground(new Color(215, 155, 0));
        ratingValueLabel.setBounds(175, 70, 200, 20);
        add(ratingValueLabel);

        JLabel reviewLabel = new JLabel("Текст отзыва (впечатление):");
        reviewLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        reviewLabel.setBounds(20, 100, 200, 20);
        add(reviewLabel);

        JTextArea reviewArea = new JTextArea();
        reviewArea.setEditable(false);
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);
        reviewArea.setFont(new Font("Arial", Font.PLAIN, 12));

        if (book.getReviewText() == null || book.getReviewText().trim().isEmpty()) {
            reviewArea.setText("Пользователь еще не оставил отзыв об этой книге.");
            reviewArea.setForeground(Color.GRAY);
        } else {
            reviewArea.setText(book.getReviewText());
        }

        JScrollPane reviewScroll = new JScrollPane(reviewArea);
        reviewScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        reviewScroll.setBounds(15, 125, 505, 110);
        add(reviewScroll);

        JLabel quotesLabel = new JLabel("Выделенные цитаты из книги:");
        quotesLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        quotesLabel.setBounds(20, 250, 250, 20);
        add(quotesLabel);

        JTextArea quotesArea = new JTextArea();
        quotesArea.setEditable(false);
        quotesArea.setLineWrap(true);
        quotesArea.setWrapStyleWord(true);
        quotesArea.setFont(new Font("Georgia", Font.ITALIC, 12)); // Курсивный шрифт для цитат

        if (book.getQuotes() == null || book.getQuotes().isEmpty()) {
            quotesArea.setText("В этой книге пока нет сохраненных цитат.");
            quotesArea.setForeground(Color.GRAY);
        } else {
            StringBuilder sb = new StringBuilder();
            int counter = 1;
            for (String quote : book.getQuotes()) {
                sb.append(counter).append(". \"").append(quote).append("\"\n");
                sb.append("— — — — — — — — — — — — — — — — — — — — — — —\n");
                counter++;
            }
            quotesArea.setText(sb.toString());
        }

        JScrollPane quotesScroll = new JScrollPane(quotesArea);
        quotesScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        quotesScroll.setBounds(15, 275, 505, 130);
        add(quotesScroll);

        // --- КНОПКА ЗАКРЫТЬ ---
        JButton closeButton = new JButton("Закрыть");
        closeButton.setBounds(215, 420, 120, 30);
        closeButton.addActionListener(e -> dispose());
        add(closeButton);
    }
}

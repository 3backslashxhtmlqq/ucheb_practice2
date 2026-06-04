package org.example.ui.panels;

import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URI;

public class AboutPanel extends JPanel {
    public AboutPanel() {
        setLayout(new BorderLayout(10, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Отступы от краев окна

        JLabel headerLabel = new JLabel("О приложении", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        headerLabel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(new LineBorder(Color.BLACK, 1));

        JLabel infoLabel = new JLabel("<html><center>Версия: 1.0<br>Автор: Жидков И.В.<br>Год создания: 2026<br></center></html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        centerPanel.add(infoLabel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton sourceCodeButton = new JButton("Исходный код");

        sourceCodeButton.setFocusPainted(false);
        sourceCodeButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        sourceCodeButton.addActionListener(e -> {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI("https://github.com/3backslashxhtmlqq/ucheb_practice2"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Не удалось открыть ссылку на проект\nПричина: " +  ex.getMessage());
            }
        });

        bottomPanel.add(sourceCodeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}

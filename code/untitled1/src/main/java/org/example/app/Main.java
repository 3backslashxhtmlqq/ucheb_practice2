package org.example.app;

import org.example.service.BookStorage;
import org.example.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        BookStorage.load();
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
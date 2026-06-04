package org.example.ui.admin;

import org.example.debug.LogService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class AdminLogsPanel extends JPanel {

    private JTextArea logsArea;
    private LogService logService = new LogService();

    public AdminLogsPanel() {
        setLayout(null);

        // Верхняя плашка "Логи"
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        titlePanel.setBounds(20, 20, 620, 40); // Четкая сетка (x=20, width=620)

        JLabel title = new JLabel("Логи");
        title.setFont(new Font("Arial", Font.PLAIN, 18));
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titlePanel.add(title, BorderLayout.WEST);
        add(titlePanel);

        // --- ЦЕНТРАЛЬНАЯ ОБЛАСТЬ ЛОГОВ (Как на Макете 8) ---
        logsArea = new JTextArea();
        logsArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Моноширинный шрифт для логов
        logsArea.setEditable(false); // Админ не должен стирать логи руками в программе

        JScrollPane scrollPane = new JScrollPane(logsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollPane.setBounds(20, 80, 620, 410); // Повторяет контуры catalogPanel из прошлых окон
        add(scrollPane);

        // --- КНОПКА "ЭКСПОРТИРОВАТЬ В ФАЙЛ" ---
        JButton exportButton = new JButton("<html><center>Экспортировать<br>в файл</center></html>");
        // Центрируем: (675 - 160) / 2 = ~257. Округлим до 250 для идеального баланса в рабочей области
        exportButton.setBounds(250, 510, 160, 45);

        exportButton.addActionListener(e -> {
            if (logsArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Журнал логов пуст. Нечего экспортировать.", "Внимание", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Сохранить логи как...");
            fileChooser.setSelectedFile(new File("exported_logs.txt"));
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File targetFile = fileChooser.getSelectedFile();

                // Автоматически добавляем расширение .txt, если админ забыл его написать
                if (!targetFile.getName().toLowerCase().endsWith(".txt")) {
                    targetFile = new File(targetFile.getAbsolutePath() + ".txt");
                }

                try {
                    logService.exportLogsTo(targetFile);
                    JOptionPane.showMessageDialog(this, "Логи успешно экспортированы в файл:\n" + targetFile.getAbsolutePath(), "Успех", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при экспорте файла: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(exportButton);

        // Первичная загрузка контента
        refreshLogs();
    }

    /**
     * Считывает свежие логи и выводит их в текстовое поле
     */
    public void refreshLogs() {
        logsArea.setText(""); // Очищаем поле
        List<String> logs = logService.getAllLogs();

        if (logs.isEmpty()) {
            logsArea.setText("Системный журнал пуст.");
            return;
        }

        // Выводим логи. Если хотите, чтобы новые события были сверху — крутим цикл в обратную сторону
        StringBuilder sb = new StringBuilder();
        for (int i = logs.size() - 1; i >= 0; i--) {
            sb.append(logs.get(i)).append("\n");
        }
        logsArea.setText(sb.toString());

        // Скроллим ползунок в самый верх (к свежим записям)
        logsArea.setCaretPosition(0);
    }
}

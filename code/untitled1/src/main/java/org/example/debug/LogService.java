package org.example.debug;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LogService {

    private static final String LOG_FILE_PATH = "data/logs.txt";

    public LogService() {
        try {
            Files.createDirectories(Paths.get("data"));
            File file = new File(LOG_FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Читает все логи из файла для вывода на панель
     */
    public List<String> getAllLogs() {
        List<String> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения логов: " + e.getMessage());
        }
        return logs;
    }

    /**
     * Экспортирует логи в выбранный пользователем файл
     */
    public void exportLogsTo(File targetFile) throws IOException {
        List<String> logs = getAllLogs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
            for (String log : logs) {
                writer.write(log);
                writer.newLine();
            }
        }
    }
}
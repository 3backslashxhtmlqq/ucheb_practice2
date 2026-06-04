package org.example.debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String LOG_FILE_PATH = "data/logs.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Флаг режима отладки. Если true — в консоль и файл пишутся подробные DEBUG-логи
    private static boolean debugMode = true;

    static {
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

    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Основной метод логирования
     */
    public static synchronized void log(LogLevel level, String message) {
        // Если это отладочный лог, а режим отладки выключен — игнорируем
        if (level == LogLevel.DEBUG && !debugMode) {
            return;
        }

        String timestamp = LocalDateTime.now().format(formatter);
        String fullLogMessage = String.format("%s %s %s", timestamp, level.getPrefix(), message);

        // 1. Дублирование в консоль (как просил макет функционала)
        if (level == LogLevel.ERROR) {
            System.err.println(fullLogMessage); // Ошибки подсвечиваем красным в консоли IDE
        } else {
            System.out.println(fullLogMessage);
        }

        // 2. Запись в постоянный текстовый файл логов
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(fullLogMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Критическая ошибка записи в лог-файл: " + e.getMessage());
        }
    }

    // Удобные обертки для вызовов в коде
    public static void info(String msg) { log(LogLevel.INFO, msg); }
    public static void debug(String msg) { log(LogLevel.DEBUG, msg); }
    public static void warn(String msg) { log(LogLevel.WARN, msg); }
    public static void error(String msg) { log(LogLevel.ERROR, msg); }
}
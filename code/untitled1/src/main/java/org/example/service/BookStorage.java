package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.debug.Logger;
import org.example.model.Book;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookStorage {
    private static List<Book> books = new ArrayList<>();
    private static String currentUsername = null;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void initUserSession(String username) {
        currentUsername = username;
        Logger.info("Инициализирована сессия пользователя: [" + username + "]");
        load();
    }

    public static List<Book> getBooks() {
        return books;
    }

    public static void add(Book book) {
        books.add(book);
        Logger.info("Пользователь [" + (currentUsername != null ? currentUsername : "Guest") +
                "] добавил книгу в свой каталог: '" + book.getTitle() + "'");
        save();
    }

    public static void remove(Book book) {
        books.remove(book);
        Logger.info("Пользователь [" + (currentUsername != null ? currentUsername : "Guest") +
                "] удалил книгу из своего каталога: '" + book.getTitle() + "'");
        save();
    }

    private static File getStorageFile() {
        if (currentUsername == null) {
            return new File("guest_catalog.json");
        }
        return new File(currentUsername + "_catalog.json");
    }

    private static void save() {
        File file = getStorageFile();
        Logger.debug("Попытка автоматического сохранения текущего каталога в файл: " + file.getName());
        try {
            mapper.writeValue(file, books);
            Logger.debug("Автосохранение успешно завершено. Записано книг: " + books.size());
        } catch (Exception e) {
            Logger.error("Ошибка при автоматическом сохранении каталога в файл " + file.getName() + ": " + e.getMessage());
        }
    }

    public static void load() {
        File file = getStorageFile();
        Logger.debug("Попытка загрузки каталога из файла: " + file.getName());
        try {
            if (file.exists()) {
                books = mapper.readValue(file, new TypeReference<List<Book>>() {});
                Logger.info("Каталог пользователя [" + (currentUsername != null ? currentUsername : "Guest") +
                        "] успешно загружен. Найдено книг: " + books.size());
            } else {
                books = new ArrayList<>();
                Logger.debug("Файл каталога " + file.getName() + " не найден. Создан новый пустой список.");
            }
        } catch (Exception e) {
            Logger.error("Критическая ошибка при загрузке JSON каталога из файла " + file.getName() + ": " + e.getMessage());
            books = new ArrayList<>();
        }
    }

    public static List<String> getAllUserCatalogs() {
        Logger.debug("Запущен скан директории для поиска файлов пользовательских каталогов (*_catalog.json)...");
        File dir = new File(".");
        String[] files = dir.list((dir1, name) -> name.endsWith("_catalog.json") && !name.startsWith("guest_"));

        List<String> usernames = new ArrayList<>();
        if (files != null) {
            for (String file : files) {
                usernames.add(file.replace("_catalog.json", ""));
            }
            Logger.debug("Сканирование завершено. Обнаружено уникальных каталогов: " + usernames.size());
        } else {
            Logger.warn("Не удалось прочитать корневую директорию проекта при поиске каталогов.");
        }
        return usernames;
    }

    // --- МЕТОДЫ ДЛЯ АДМИНИСТРАТИВНОГО УПРАВЛЕНИЯ ЧУЖИМИ ФАЙЛАМИ ---

    public static void saveBooksToFile(File file, List<Book> bookList) {
        Logger.debug("Администратор инициировал принудительное сохранение списка в чужой файл: " + file.getName());
        try {
            mapper.writeValue(file, bookList);
            Logger.info("Файл каталога [" + file.getName() + "] успешно перезаписан администратором. Количество книг: " + bookList.size());
        } catch (Exception e) {
            Logger.error("Ошибка администрирования при записи в файл " + file.getName() + ": " + e.getMessage());
        }
    }

    public static List<Book> loadBooksFromFile(File file) {
        Logger.debug("Администратор затребовал чтение чужого файла: " + file.getName());
        try {
            if (file.exists()) {
                List<Book> loaded = mapper.readValue(file, new TypeReference<List<Book>>() {});
                Logger.debug("Файл " + file.getName() + " успешно прочитан админом. Прочитано элементов: " + loaded.size());
                return loaded;
            } else {
                Logger.warn("Администратор пытался прочитать несуществующий файл каталога: " + file.getName());
            }
        } catch (Exception e) {
            Logger.error("Ошибка администрирования при чтении файла " + file.getName() + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }
}

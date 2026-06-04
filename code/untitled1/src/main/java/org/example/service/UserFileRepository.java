package org.example.service;

import org.example.debug.Logger;
import org.example.model.Role;
import org.example.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserFileRepository {

    private static final String FILE_PATH = "data/users.txt";

    public UserFileRepository() {
        try {
            Files.createDirectories(Paths.get("data"));
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Сохранение ОДНОГО пользователя (аппенд в конец файла)
    public void save(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            // Добавляем флаг блокировки в самый конец строки (true/false)
            writer.write(user.getLogin() + ";"
                    + user.getPassword() + ";"
                    + user.getRole().name() + ";"
                    + user.isBlocked());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        Logger.debug("Низкоуровневое чтение базы данных пользователей из файла: " + FILE_PATH);

        int lineCounter = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCounter++;
                String[] parts = line.split(";");

                if (parts.length >= 3) {
                    try {
                        User user = new User(parts[0], parts[1], Role.valueOf(parts[2]));

                        // Если в файле уже записан 4-й параметр (блокировка), считываем его
                        if (parts.length >= 4) {
                            user.setBlocked(Boolean.parseBoolean(parts[3]));
                        }

                        users.add(user);
                    } catch (IllegalArgumentException ex) {
                        // Логируем случай, если роль в файле написана с ошибкой (например, ADMN вместо ADMIN)
                        Logger.warn("Ошибка распознавания роли в users.txt на строке #" + lineCounter + ": '" + parts[2] + "'");
                    }
                } else {
                    // Предупреждаем, если строка имеет неверное количество разделителей (битая строка)
                    Logger.warn("Пропущена поврежденная строка в users.txt на позиции #" + lineCounter + ": '" + line + "'");
                }
            }

            Logger.debug("Чтение файла пользователей успешно завершено. Загружено аккаунтов: " + users.size());

        } catch (IOException e) {
            // Критическая ошибка: файла нет, или к нему нет прав доступа
            Logger.error("Критический сбой ввода-вывода (I/O) при чтении файла " + FILE_PATH + ": " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    public User findByLogin(String login) {
        return findAll().stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }

    public void update(User updatedUser) {
        List<User> users = findAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getLogin().equals(updatedUser.getLogin())) {
                users.set(i, updatedUser); // Обновляем объект (включая роль и бан)
                break;
            }
        }
        saveAll(users); // Перезаписываем файл
    }

    public void deleteByLogin(String login) {
        List<User> users = findAll();
        users.removeIf(user -> user.getLogin().equals(login));
        saveAll(users); // Перезаписываем файл
    }

    public void saveAll(List<User> users) {
        // Обратите внимание: FileWriter(FILE_PATH, false) — второй параметр FALSE
        // очищает файл перед новой записью!
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (User user : users) {
                writer.write(user.getLogin() + ";"
                        + user.getPassword() + ";"
                        + user.getRole().name() + ";"
                        + user.isBlocked());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при перезаписи файла пользователей: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package org.example.service;

import org.example.model.Role;
import org.example.model.User;

import java.util.List;

public class AuthService {

    private final UserFileRepository repository =
            new UserFileRepository();

    public boolean register(
            String login,
            String password
    ) {

        if (
                repository.findByLogin(login)
                        != null
        ) {
            return false;
        }

        Role role =
                login.equalsIgnoreCase("admin")
                        ? Role.ADMIN
                        : Role.USER;


        User user =
                new User(
                        login,
                        password,
                        role
                );

        repository.save(user);

        return true;
    }

    public User login(String login, String password) {
        User user = repository.findByLogin(login); // или ваша логика поиска
        if (user != null && user.getPassword().equals(password)) {
            if (user.isBlocked()) {
                throw new RuntimeException("Ваш аккаунт заблокирован администратором!");
            }
            return user;
        }
        return null;
    }

    // 2. Метод сохранения/обновления данных пользователя в репозитории/файле
    public void updateUser(User user) {
        repository.update(user); // или repository.save() в зависимости от вашей реализации
    }

    // 3. Метод добавления нового пользователя
    public boolean registerNewUser(String login, String password, Role role) {
        if (repository.findByLogin(login) != null) {
            return false; // Такой логин уже занят
        }
        User newUser = new User(login, password, role);
        repository.save(newUser); // Сохраняем в БД/файл
        return true;
    }

    // 4. Метод удаления пользователя
    public void deleteUser(String login) {
        repository.deleteByLogin(login); // Удаляем из репозитория
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

}

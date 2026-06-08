package units;

import org.example.model.Role;
import org.example.model.User;
import org.example.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class UnitAuthService {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        File testDb = new File("data/users.txt");
        if (testDb.exists()) {
            testDb.delete();
        }
        authService = new AuthService();
    }

    @Test
    @DisplayName("Успешная регистрация нового уникального пользователя")
    void testRegisterNewUser_Success() {
        boolean isRegistered = authService.registerNewUser("user3", "user3", Role.USER);

        assertTrue(isRegistered, "Метод должен возвращать true при успешной регистрации");
    }

    @Test
    @DisplayName("Отказ в регистрации, если логин уже занят")
    void testRegisterNewUser_DuplicateLogin() {
        authService.registerNewUser("user3", "user4", Role.USER);

        boolean isRegisteredAgain = authService.registerNewUser("user3", "user4", Role.ADMIN);

        assertFalse(isRegisteredAgain, "Метод должен возвращать false, если логин дублируется");
    }

    @Test
    @DisplayName("Успешный вход в систему с корректными данными")
    void testLogin_Success() {
        authService.registerNewUser("user", "1234", Role.USER);

        User loggedInUser = authService.login("user", "1234");

        assertNotNull(loggedInUser, "Объект пользователя не должен быть null");
        assertEquals("user", loggedInUser.getLogin());
        assertEquals(Role.USER, loggedInUser.getRole());
    }

    @Test
    @DisplayName("Возврат null при авторизации с неверным паролем")
    void testLogin_WrongPassword() {
        authService.registerNewUser("user", "1234", Role.USER);

        User loggedInUser = authService.login("user", "12345");

        assertNull(loggedInUser, "При неверном пароле метод login() обязан возвращать null");
    }

    @Test
    @DisplayName("Выброс исключения при попытке входа заблокированного пользователя")
    void testLogin_BlockedUser_ThrowsException() {
        authService.registerNewUser("user1", "user1", Role.USER);

        // Имитируем блокировку пользователя администратором
        User user = authService.login("user1", "user1");
        user.setBlocked(true);
        authService.updateUser(user);

        // Проверяем, выбрасывает ли метод RuntimeException
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.login("user1", "user1");
        });

        assertTrue(exception.getMessage().contains("заблокирован"), "Текст ошибки должен сообщать о блокировке");
    }
}

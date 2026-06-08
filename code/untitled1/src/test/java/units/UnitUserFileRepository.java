package units;

import org.example.model.User;
import org.example.service.UserFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UnitUserFileRepository {

    private UserFileRepository repository;
    private static final String FILE_PATH = "data/users.txt";

    @BeforeEach
    void setUp() {
        repository = new UserFileRepository();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("");
        } catch (IOException e) {
            fail("Не удалось подготовить файл базы данных для тестов");
        }
    }

    @Test
    @DisplayName("Корректный пропуск битых или поврежденных строк при чтении файла")
    void testFindAll_SkipsCorruptedLines() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("user;1234;USER;false"); writer.newLine();
            writer.write("fff_fff_fff_fff"); writer.newLine();
            writer.write("user3;user3;GUEST;false"); writer.newLine();
        }

        List<User> users = repository.findAll();

        assertEquals(1, users.size(), "Репозиторий должен отфильтровать некорректные строки и вернуть только 1 валидного юзера");
        assertEquals("user", users.get(0).getLogin());
    }
}

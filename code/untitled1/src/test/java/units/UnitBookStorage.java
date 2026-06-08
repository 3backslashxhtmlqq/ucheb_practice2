package units;

import org.example.model.Book;
import org.example.service.BookStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitBookStorage {

    @AfterEach
    void tearDown() {
        new File("test_catalog.json").delete();
    }

    @Test
    @DisplayName("Проверка сохранения и загрузки книги с отзывом, оценкой и цитатами")
    void testUserSession_SaveAndLoadWithReviewsAndQuotes() {
        // Инициализируем сессию под тестовым именем
        BookStorage.initUserSession("test");

        // Создаем тестовую книгу
        Book sampleBook = new Book("Капитанская дочка", "А.С. Пушкин", "Эксмо", "Описание", "http://preview", true);
        sampleBook.setReviewText("Шедевр русской литературы!");
        sampleBook.setReviewRating(5);
        sampleBook.addQuote("Береги честь смолоду.");

        BookStorage.add(sampleBook);

        File expectedJsonFile = new File("test_catalog.json");
        assertTrue(expectedJsonFile.exists(), "Файл каталога пользователя должен быть создан на диске");

        BookStorage.initUserSession("test");
        List<Book> loadedBooks = BookStorage.getBooks();

        assertEquals(1, loadedBooks.size());
        Book loadedBook = loadedBooks.get(0);

        assertEquals("Капитанская дочка", loadedBook.getTitle());
        assertEquals("Шедевр русской литературы!", loadedBook.getReviewText());
        assertEquals(5, loadedBook.getReviewRating());
        assertEquals(1, loadedBook.getQuotes().size());
        assertEquals("Береги честь смолоду.", loadedBook.getQuotes().get(0));
    }
}

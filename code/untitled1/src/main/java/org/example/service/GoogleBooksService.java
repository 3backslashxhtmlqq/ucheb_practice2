package org.example.service;

import org.example.debug.Logger;
import org.example.model.Book;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GoogleBooksService {

    public List<Book> searchBooks(String query) throws Exception {
        Logger.info("Запущен поиск книг через Google Books API по запросу: '" + query + "'");

        List<Book> books = new ArrayList<>();

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // Логируем формирование URL на уровне DEBUG (для отладки разработчиком)
            String urlString = "https://www.googleapis.com/books/v1/volumes?q="
                    + encodedQuery + "&key=AIzaSyC_hn9_LIOmyhuY-OWVShBJ5_8tqPixk88" + "&maxResults=40";
            Logger.debug("Сформирован URL запроса: " + urlString);

            URL url = new URL(urlString);

            Logger.debug("Установка соединения с серверами Google...");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Logger.debug("Ответ от Google Books API успешно получен. Начинается парсинг JSON.");

            JSONObject root = new JSONObject(response.toString());

            if (!root.has("items")) {
                // Если результатов нет, это нормальная ситуация, пишем INFO
                Logger.info("Поиск завершен: по запросу '" + query + "' ничего не найдено.");
                return books;
            }

            JSONArray items = root.getJSONArray("items");
            Logger.debug("В ответе обнаружено " + items.length() + " элементов (книг).");

            for (int i = 0; i < items.length(); i++) {
                JSONObject volumeInfo = items.getJSONObject(i).getJSONObject("volumeInfo");

                String title = volumeInfo.optString("title", "Без названия");
                String authors = "";

                if (volumeInfo.has("authors")) {
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                    StringBuilder builder = new StringBuilder();

                    for (int j = 0; j < authorsArray.length(); j++) {
                        builder.append(authorsArray.getString(j));
                        if (j < authorsArray.length() - 1) {
                            builder.append(", ");
                        }
                    }
                    authors = builder.toString();
                }

                String publisher = volumeInfo.optString("publisher", "-");
                String description = volumeInfo.optString("description", "-");
                String previewUrl = volumeInfo.optString("previewLink", "");

                books.add(new Book(
                        title,
                        authors,
                        publisher,
                        description,
                        previewUrl,
                        true
                ));
            }

            // Логируем успешный исход операции
            Logger.info("Успешно обработано и возвращено книг: " + books.size());

        } catch (Exception e) {
            // Если пропал интернет или API выдало ошибку (например, лимит ключа) — пишем ERROR.
            // Это подсветится красным в консоли IDE и зафиксируется в файле
            Logger.error("Критический сбой при работе с Google Books API: " + e.getMessage());
            // Пробрасываем ошибку дальше, чтобы UI (SearchPanel) мог показать JOptionPane
            throw e;
        }

        return books;
    }
}
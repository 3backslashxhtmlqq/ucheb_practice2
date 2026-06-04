package org.example.model;

public class Book {
    private String title;
    private String authors;
    private String publisher;
    private String description;
    private String content;
    private String previewUrl;

    private String reviewText = "";
    private int reviewRating = 5;
    private java.util.List<String> quotes = new java.util.ArrayList<>();

    public Book() {
    }

    public Book(String title, String authors, String publisher, String description, String content) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.description = description;
        this.content = content;
        this.previewUrl = null; // у файла нет веб-ссылки
    }

    public Book(String title, String authors, String publisher, String description, String previewUrl, boolean isApi) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.description = description;
        this.previewUrl = previewUrl;
        this.content = null; // у API нет полного текста
    }

    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public String getPublisher() { return publisher; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getPreviewUrl() { return previewUrl; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }
    public void setDescription(String description) { this.description = description; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public int getReviewRating() { return reviewRating; }
    public void setReviewRating(int reviewRating) { this.reviewRating = reviewRating; }

    public java.util.List<String> getQuotes() { return quotes; }
    public void addQuote(String quote) { this.quotes.add(quote); }

}

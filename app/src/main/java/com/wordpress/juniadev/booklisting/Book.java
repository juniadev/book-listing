package com.wordpress.juniadev.booklisting;

/**
 * Class representing a book that will be retrieved from the API and displayed in the app.
 */
public class Book {

    private final String thumbnail;
    private final String title;
    private final String author;
    private final String publisher;
    private final String publishedDate;

    public Book(String thumbnail, String title, String author, String publisher, String publishedDate) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }
}

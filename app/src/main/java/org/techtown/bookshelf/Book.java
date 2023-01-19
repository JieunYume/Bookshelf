package org.techtown.bookshelf;

public class Book{
    private String push;
    private String title;
    private String author;
    private String publisher;
    private String genre;
    private String bookimage_address="";

    public Book(String push, String title, String author, String publisher, String genre, String bookimage_address) {
        this.push = push;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.bookimage_address = bookimage_address;
    }

    public String getBookimage_address() {
        return bookimage_address;
    }

    public void setBookimage_address(String bookimage_address) {
        this.bookimage_address = bookimage_address;
    }


    public String getPush() {
        return push;
    }

    public void setPush(String push) {
        this.push = push;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}

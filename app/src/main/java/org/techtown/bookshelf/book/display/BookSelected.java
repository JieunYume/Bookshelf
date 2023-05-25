package org.techtown.bookshelf.book.display;

import java.io.Serializable;

public class BookSelected implements Serializable {
    private String push;
    private String title;
    private String author;
    private String amount;
    private String read_amount;
    private String record_num;
    private String bookimage_address="";

    public BookSelected(String push, String title, String author, String amount, String read_amount, String record_num, String bookimage_address) {
        this.push = push;
        this.title = title;
        this.author = author;
        this.amount = amount;
        this.read_amount = read_amount;
        this.record_num = record_num;
        this.bookimage_address = bookimage_address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRead_amount() {
        return read_amount;
    }

    public void setRead_amount(String read_amount) {
        this.read_amount = read_amount;
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

    public String getBookimage_address() {
        return bookimage_address;
    }

    public void setBookimage_address(String bookimage_address) {
        this.bookimage_address = bookimage_address;
    }

    public String getRecord_num() {
        return record_num;
    }

    public void setRecord_num(String record_num) {
        this.record_num = record_num;
    }
}

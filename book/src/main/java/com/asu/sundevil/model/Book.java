package com.asu.sundevil.model;

import java.io.Serializable;

public class Book implements Serializable {
    private String isbn, title, author;

    public Book() {}
    public Book(String isbn, String title, String author) {
        this.isbn   = isbn;
        this.title  = title;
        this.author = author;
    }

    public String getIsbn()         { return isbn; }
    public void   setIsbn(String i) { this.isbn = i; }
    public String getTitle()        { return title; }
    public void   setTitle(String t){ this.title = t; }
    public String getAuthor()       { return author; }
    public void   setAuthor(String a){ this.author = a; }
}

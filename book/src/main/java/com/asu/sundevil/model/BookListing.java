package com.asu.sundevil.model;

import java.io.Serializable;

public class BookListing implements Serializable {
    private String listingId;
    private Book   book;
    private double price;
    private String status;   // ACTIVE, RESERVED, EXPIRED
    private String seller;   // username
    private String photoUrl; // optional

    public BookListing() {}

    public BookListing(String id, Book b, double p, String s, String sel, String photo) {
        this.listingId = id;
        this.book      = b;
        this.price     = p;
        this.status    = s;
        this.seller    = sel;
        this.photoUrl  = photo;
    }

    // getters & setters omitted for brevity—please include for all fields
    public String getListingId() { return listingId; }
    public void   setListingId(String l) { this.listingId = l; }
    public Book   getBook()      { return book; }
    public void   setBook(Book b){ this.book = b; }
    public double getPrice()     { return price; }
    public void   setPrice(double p) { this.price = p; }
    public String getStatus()    { return status; }
    public void   setStatus(String s) { this.status = s; }
    public String getSeller()    { return seller; }
    public void   setSeller(String sel) { this.seller = sel; }
    public String getPhotoUrl()  { return photoUrl; }
    public void   setPhotoUrl(String u)  { this.photoUrl = u; }
}

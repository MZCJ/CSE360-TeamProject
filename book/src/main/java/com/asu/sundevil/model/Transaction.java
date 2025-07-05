package com.asu.sundevil.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private String        transactionId;
    private String        listingId;
    private String        buyer;
    private LocalDateTime timestamp;
    private String        status; // RESERVED or COMPLETED

    public Transaction() {}

    public Transaction(String tx, String li, String b, LocalDateTime ts, String st) {
        this.transactionId = tx;
        this.listingId     = li;
        this.buyer         = b;
        this.timestamp     = ts;
        this.status        = st;
    }

    // getters & setters omitted for brevity
    public String        getTransactionId() { return transactionId; }
    public void          setTransactionId(String t) { this.transactionId = t; }
    public String        getListingId()     { return listingId; }
    public void          setListingId(String l) { this.listingId = l; }
    public String        getBuyer()         { return buyer; }
    public void          setBuyer(String b) { this.buyer = b; }
    public LocalDateTime getTimestamp()     { return timestamp; }
    public void          setTimestamp(LocalDateTime ts) { this.timestamp = ts; }
    public String        getStatus()        { return status; }
    public void          setStatus(String s){ this.status = s; }
}

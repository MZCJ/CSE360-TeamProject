// DataService.java
package com.asu.sundevil;

import com.asu.sundevil.model.*;
import java.io.*;
import java.util.*;

public class DataService {
    private static final String U = "data/users.dat";
    private static final String L = "data/listings.dat";
    private static final String T = "data/transactions.dat";

    public static List<User> users;
    public static List<BookListing> listings;
    public static List<Transaction> transactions;

    @SuppressWarnings("unchecked")
    public static void init() {
        users        = (List<User>)      load(U);
        listings     = (List<BookListing>)load(L);
        transactions = (List<Transaction>)load(T);

        boolean changed = false;
        if (users.stream().noneMatch(u -> u.getUsername().equals("buyer"))) {
            users.add(new User("buyer",  "BUYER"));   changed = true;
        }
        if (users.stream().noneMatch(u -> u.getUsername().equals("seller"))) {
            users.add(new User("seller", "SELLER"));  changed = true;
        }
        if (users.stream().noneMatch(u -> u.getUsername().equals("admin"))) {
            users.add(new User("admin",  "ADMIN"));   changed = true;
        }
        if (changed) save(users, U);
    }

    private static Object load(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return in.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveAll() {
        save(users,        U);
        save(listings,     L);
        save(transactions, T);
    }

    private static void save(Object data, String path) {
        try {
            new File("data").mkdirs();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
                out.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Save failed " + path + ": " + e);
        }
    }
}

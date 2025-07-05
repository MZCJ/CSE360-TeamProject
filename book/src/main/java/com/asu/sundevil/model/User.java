package com.asu.sundevil.model;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String role;

    public User() {}
    public User(String username, String role) {
        this.username = username;
        this.role     = role;
    }

    public String getUsername() { return username; }
    public void   setUsername(String u) { this.username = u; }
    public String getRole()     { return role; }
    public void   setRole(String r) { this.role = r; }
}

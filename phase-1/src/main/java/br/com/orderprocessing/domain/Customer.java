package br.com.orderprocessing.domain;

import java.util.UUID;

public class Customer {
    private UUID id;
    private String name;
    private String email;
    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public Customer(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    public Customer() {
    }
    
}

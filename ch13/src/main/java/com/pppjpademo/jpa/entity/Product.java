package com.pppjpademo.jpa.entity;

import javax.persistence.*;

@Entity
public class Product {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private int price;

    public Product() {}
    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
}
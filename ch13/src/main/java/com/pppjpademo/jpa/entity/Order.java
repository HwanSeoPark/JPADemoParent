package com.pppjpademo.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private int quantity;

    public Order() {}
    public Order(Member member, Product product, int quantity) {
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

    public Member getMember() { return member; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
}
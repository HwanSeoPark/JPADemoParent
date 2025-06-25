package com.pppjpademo.jpa;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private int quantity;

    public OrderItem(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }
}

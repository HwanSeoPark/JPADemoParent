package com.pppjpademo.jpa;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "order")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // FK 생성
    private Order order;

    public OrderItem(String product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // 연관관계 설정자
    public void setOrder(Order order) {
        this.order = order;
    }
}

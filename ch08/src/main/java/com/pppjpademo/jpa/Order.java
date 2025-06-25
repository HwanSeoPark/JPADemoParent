package com.pppjpademo.jpa;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ORDERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "orderItems")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customer;

    // mappedBy : 관계의 owner[주인]를 정의하는 필드. 관계가 양방향인 경우 필수.
    // 그러므로, 이 양방향 관계의 주인은 OrderItem.order.
    // 외래키를 가지고 있는 엔티티 클래스가 주인.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(String customer) {
        this.customer = customer;
    }

    // 연관관계 편의 메서드
    public void addItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this); // 연관관계 주인 쪽 설정
    }

    public void removeItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
}

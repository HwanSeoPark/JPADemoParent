package com.pppjpademo.jpa;


import lombok.*;

import javax.persistence.*;

@Entity
@SequenceGenerator(
    name = "orderitem_seq_generator",
    sequenceName = "orderitem_seq", // DB 시퀀스 이름
    initialValue = 1,
    allocationSize = 100
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@ToString(exclude = "order")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, 
	generator = "orderitem_seq_generator")
    private Long id;

    private String product;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // FK 생성
    private Order order;  // 무슨 소리일까요...물론 jpa가 조인 쿼리 만들때 Left table 기준.

    public OrderItem(String product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // 연관관계 설정자
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public void setQuantity(int quantity) {
    	this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", product='" + product + '\'' +
                ", order=" + (order != null ? order : "null") +
                '}';
    }
}

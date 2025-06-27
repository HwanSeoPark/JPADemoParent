package com.pppjpademo.jpa;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ORDERS")
@SequenceGenerator(
	    name = "order_seq_generator",
	    sequenceName = "order_seq", // DB 시퀀스 이름
	    initialValue = 1,
	    allocationSize = 100
	)
//@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "orderItems")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, 
  	generator = "order_seq_generator")
    private Long id;

    private String customer;

    // 서로 참조 하고 있음 양방향
    // mappedBy 속성을 설정했다는 것은(mappedBy는 @OneToMany에 있음
    // 내가 owner가 아니라 owner의 반대편(inverse side)이다.
    // 여기의 order은 OrderItem의 order
    // orphanRemoval 설정으로 인해 remove 했을시 DB에서도 remove 됨
    // 부모클래스가 삭제 되버림,
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, 
    		   orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(String customer) {
        this.customer = customer;
    }

    public Long getId() {
    	return this.id;
    }
    
    public List<OrderItem> getOrderItems() {
    	return this.orderItems;
    }

    
    
    /////////////////////////////////////////////////
    // 연관관계 편의 메서드(inverse side, 주인 아닌쪽에서 정의함)
    public void addItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this); // 연관관계 주인 쪽 설정 : 이 설정을 하지 않으면, 
        					 // OrderItem의 ORDER_ID가 NULL 설정됨.
    }
    ///////////////////////////////////////////////////
    
    public String getCustomer() {
    	return this.customer;
    }
    
    public void removeItem(OrderItem item) {
        orderItems.remove(item); // remove는 list에서 제거하겠다는 것이지 삭제 한다는 것이 아님
        ////////////////////
        item.setOrder(null);  // 부모테이블을 없에버림
        ////////////////////
    }
}
